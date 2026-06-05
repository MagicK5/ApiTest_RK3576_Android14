package com.example.zjfb_android14;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.net.Uri; // 添加此行
import android.widget.Toast;

import com.example.fbsdk.FbSDK;

import java.util.ArrayList;
import java.util.List;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    private Uri selectedApkUri;
    private FbSDK fbSDK;
    // 将这些定义为成员变量，以便所有方法共享
    private List<FbSDK.QyScanResultInfo> wifiDataList = new ArrayList<>();
    private WifiListAdapter adapter;
    private ListView wifiListView;
    private static String TAG = "FB_TEST";

    private EditText etSerialPath, etSerialBaud, etSendData;
    private Button btnOpenSerial, btnCloseSerial, btnSendStr, btnSendHex, btnClearLog;
    private TextView tvReceiveLog;
    private ScrollView svLogScroll;

    private String mCurrentOpenedPath = null; // 当前打开的串口名
    private FbSDK.SerialCallback mSerialCallback; // 接收监听器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        fbSDK = new FbSDK(this);
        final Handler mHandler = new Handler(Looper.getMainLooper());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /**
         * Brightness
         */
        TextView getBrightness = findViewById(R.id.getBrightness);
        SeekBar setBrightness = findViewById(R.id.setBrightness);
        TextView getMaxBrightness = findViewById(R.id.getMaxBrightness);
        TextView getMinBrightness = findViewById(R.id.getMinBrightness);
        TextView isRotationLocked = findViewById(R.id.isRotationLocked);
        Button setForcedLandscape = findViewById(R.id.setForcedLandscape);
        Button setScreenOffTimeout = findViewById(R.id.setScreenOffTimeout);
        EditText setScreenOffTimeout_et = findViewById(R.id.setScreenTimeout_et);
        TextView getScreenOffTimeout = findViewById(R.id.getScreenOffTimeout);

        int brightness = fbSDK.getBrightness();
        getBrightness.setText(""+brightness);

        setBrightness.setProgress(brightness);
        setBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    fbSDK.setBrightness(progress);

                    getBrightness.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 当用户开始拖动 SeekBar 时的处理
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 当用户停止拖动 SeekBar 时的处理
            }
        });

        getMaxBrightness.setText(""+fbSDK.getMaxBrightness());
        getMinBrightness.setText(""+fbSDK.getMinBrightness());

        isRotationLocked.setText(fbSDK.isRotationLocked() ? "Locked" : "Unlocked");

        setForcedLandscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fbSDK.setForcedLandscape(true);
            }
        });

        getScreenOffTimeout.setText(""+fbSDK.getScreenOffTimeout());

        setScreenOffTimeout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeoutMs = setScreenOffTimeout_et.getText().toString().trim();
                if (!timeoutMs.isEmpty()) {
                    try {
                        // 将文本内容转换为整数（如果输入的确实是数字）
                        int timeout = Integer.parseInt(timeoutMs);
                        // 调用 qySDK 中的 setRecorderTime 方法
                        fbSDK.setScreenOffTimeout(timeout);
                        getScreenOffTimeout.setText(String.valueOf(fbSDK.getScreenOffTimeout()));

                    } catch (NumberFormatException e) {
                        setScreenOffTimeout_et.setText("输入的数字不规范！请重新输入！");
                    }
                } else {
                    // 如果 EditText 为空，可以提示用户输入内容
                    setScreenOffTimeout_et.setText("输入不能为空！请重新输入！");
                }

            }
        });

        /**
         * Volume
         */
        TextView getVolume = findViewById(R.id.getVolume);
        SeekBar setVolume = findViewById(R.id.setVolume);
        TextView getMaxVolume = findViewById(R.id.getMaxVolume);
        TextView getMinVolume = findViewById(R.id.getMinVolume);

        int maxVol = fbSDK.getMaxVolume();
        int minVol = fbSDK.getMinVolume();
        int currentVol = fbSDK.getVolume();

        getVolume.setText(""+currentVol);

        setVolume.setMax(maxVol);
        setVolume.setProgress(currentVol);
        setVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    fbSDK.setVolume(progress);

                    getVolume.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 当用户开始拖动 SeekBar 时的处理
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 当用户停止拖动 SeekBar 时的处理
            }
        });

        getMaxVolume.setText(String.valueOf(maxVol));
        getMinVolume.setText(String.valueOf(minVol));


        /**
         * SystemBar
         */
        Switch switchStatusBar = findViewById(R.id.switch_statusbar);
        Switch switchNavigationBar = findViewById(R.id.switch_navigationbar);

        switchStatusBar.setChecked(fbSDK.isStatusBarStatus());
        switchNavigationBar.setChecked(fbSDK.isNavigationBarStatus());

        switchStatusBar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            fbSDK.setStatusBarStatus(isChecked);
        });
        switchNavigationBar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            fbSDK.setNavigationBarStatus(isChecked);
        });


        /**
         * ScreenControl
         */
        Button setScreenState =findViewById(R.id.setScreenState);

        setScreenState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TestScreen", "准备执行：熄屏");
                fbSDK.setScreenState(false);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TestScreen", "延时结束：准备执行亮屏");
                        fbSDK.setScreenState(true);
                    }
                }, 3000);
            }
        });

        /**
         * InstallSilently
         */
        Button selectPath = findViewById(R.id.selectPath_bt);
        // 选择文件逻辑
        selectPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/vnd.android.package-archive");
                startActivityForResult(intent, 1);
            }
        });
        EditText setApkPath = findViewById(R.id.setApkPath_et);
        Button install = findViewById(R.id.installSilently);
        EditText packageName_et = findViewById(R.id.packageName_et);
        Button uninstall = findViewById(R.id.uninstallSilently);

        // 选择文件路径
        selectPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 打开文件选择器
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("application/vnd.android.package-archive");  // 只显示 APK 文件
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);  // 请求码 1
            }
        });

        // 静默安装
        install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedApkUri != null) {
                    // 调用接受 URI 的 SDK 方法
                    fbSDK.installPackage(selectedApkUri, (success, msg) -> {
                        // 在主线程更新 UI 提示
                        runOnUiThread(() -> {
                            setApkPath.setText("安装结果: " + (success ? "成功" : "失败 - " + msg));
                        });
                    });
                } else {
                    setApkPath.setText("请先选择 APK 文件！");
                }
            }
        });

        // 静默卸载
        uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String packageName = packageName_et.getText().toString().trim();
                if (!packageName.isEmpty()) {
                    fbSDK.uninstallPackage(packageName);
                } else {
                    setApkPath.setText("输入不能为空！请重新输入！");
                }
            }
        });



        //Wifi模块
        // --- WiFi 模块初始化 ---
        wifiListView = findViewById(R.id.wifi_list_view);
        Button showConnectedWifi = findViewById(R.id.showConnectedWifi);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch wifiSwitch = findViewById(R.id.wifi_switch);

        // 设置初始状态
        boolean isWifiOn = fbSDK.isWifiEnabled();
        wifiSwitch.setChecked(isWifiOn);
        wifiListView.setVisibility(isWifiOn ? View.VISIBLE : View.GONE);

        wifiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            fbSDK.enableWifi(isChecked);
            wifiListView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
        adapter = new WifiListAdapter(this, wifiDataList);
        wifiListView.setAdapter(adapter);
        findViewById(R.id.refreshWifi_bt).setOnClickListener(v -> performScan());

        wifiListView.setOnItemClickListener((parent, view, position, id) -> {
            FbSDK.QyScanResultInfo selectedWifi = wifiDataList.get(position);
            showPasswordDialog(selectedWifi);
        });

        showConnectedWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWiFiInfo();

            }
        });

        //WhiteList
        EditText packageName_whiteList = findViewById(R.id.packageName_whiteList);
        Button addToWhiteList = findViewById(R.id.addToWhiteList);
        Button removeFromWhiteList = findViewById(R.id.removeFromWhiteList);
        Button getWhiteList = findViewById(R.id.getWhiteList);
        Button isAllowedToStart = findViewById(R.id.isAllowedToStart);

        List<String> whitelist = new ArrayList<>();

        addToWhiteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String packageName_wl = packageName_whiteList.getText().toString().trim();
                if (!packageName_wl.isEmpty()) {
                    fbSDK.addToWhiteList(packageName_wl);
                } else {
                    Log.e(TAG,"输入不能为空！请重新输入！");
                }
            }
        });

        removeFromWhiteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String packageName_wl = packageName_whiteList.getText().toString().trim();
                if(!packageName_wl.isEmpty()){
                    fbSDK.removeFromWhiteList(packageName_wl);
                } else {
                    Log.e(TAG,"输入不能为空！请重新输入！");
                }
            }
        });

        getWhiteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whitelist.add(fbSDK.getWhiteList().toString());
                Log.d(TAG, whitelist.toString());
            }
        });

        isAllowedToStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String packageName_wl = packageName_whiteList.getText().toString().trim();
                if(!packageName_wl.isEmpty()){
                    boolean allow = fbSDK.isAllowedToStart(packageName_wl);
                    if(allow){
                        Log.d(TAG,packageName_wl+" is allowed.");
                    }else{
                        Log.d(TAG,packageName_wl+" is not allowed.");
                    }
                }else {
                    Log.e(TAG,"input can not be empty");
                }

            }
        });


        //Power
        Button reboot = findViewById(R.id.reboot);
        Button shutdown = findViewById(R.id.shutdownDevice);

        reboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"about to reboot");
                fbSDK.reboot();
            }
        });

        shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"about to shutdown");
                fbSDK.shutdownDevice();
            }
        });

        Button getBatteryLevel = findViewById(R.id.getBatteryLevel);
        TextView getBatteryLevel_tv = findViewById(R.id.getBatteryLevel_tv);
        getBatteryLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int battery = fbSDK.getBatteryLevel();
                getBatteryLevel_tv.setText(String.valueOf(battery)+"%");
            }
        });

        //Screenshot
        Button screenshot = findViewById(R.id.takeScreenshot);

        screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ans = fbSDK.takeScreenshotAndSave();
                if(ans) Log.d(TAG,"Successfully took screenshot.");
            }
        });

        /**
         * Serial
         */

        // 1. 初始化串口相关的 UI 元素
        etSerialPath = findViewById(R.id.et_serial_path);
        etSerialBaud = findViewById(R.id.et_serial_baud);
        etSendData = findViewById(R.id.et_send_data);
        btnOpenSerial = findViewById(R.id.btn_open_serial);
        btnCloseSerial = findViewById(R.id.btn_close_serial);
        btnSendStr = findViewById(R.id.btn_send_str);
        btnSendHex = findViewById(R.id.btn_send_hex);
        btnClearLog = findViewById(R.id.btn_clear_log);
        tvReceiveLog = findViewById(R.id.tv_receive_log);
        svLogScroll = findViewById(R.id.sv_log_scroll);

        // 2. 初始化串口接收回调
        mSerialCallback = new FbSDK.SerialCallback() {
            @Override
            public void onDataReceived(final byte[] data) {
                // 【避坑指南】Binder线程不可直接修改UI，必须抛到主线程(UI 线程)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (data != null && data.length > 0) {
                            String timeString = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(new Date());
                            String hexStr = bytesToHexString(data);
                            String asciiStr = new String(data).replaceAll("[\\x00-\\x1F\\x7F]", "."); // 剔除不可见字符
                            String logLine = String.format("[%s] RX =>\n HEX: %s\n ASC: %s\n\n", timeString, hexStr, asciiStr);
                            tvReceiveLog.append(logLine);

                            // 自动滚动到行到底部
                            svLogScroll.post(new Runnable() {
                                @Override
                                public void run() {
                                    svLogScroll.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                        }
                    }
                });
            }
        };
        // 3. 点击事件 - 打开串口并开始注册回调
        btnOpenSerial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = etSerialPath.getText().toString().trim();
                String baudStr = etSerialBaud.getText().toString().trim();
                if (path.isEmpty() || baudStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "路径或波特率不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                int baudRate = Integer.parseInt(baudStr);
                // 调用底层的底层服务，这里数据位写死 8, 停止位 1, 无校验 'N'。
                // 也可以根据需要做下拉栏让用户选择配置。
                boolean isSuccess = fbSDK.openSerial(path, baudRate, 8, 1, 'N');
                if (isSuccess) {
                    mCurrentOpenedPath = path;

                    // 打开成功后必须注册监听器才能收到串口反馈
                    fbSDK.registerSerialCallback(path, mSerialCallback);

                    Toast.makeText(MainActivity.this, "串口 " + path + " 打开成功", Toast.LENGTH_SHORT).show();
                    updateSerialUiState(true);
                    tvReceiveLog.setText("[" + path + " 串口已开启]\n");
                } else {
                    Toast.makeText(MainActivity.this, "串口打开失败，请检查机型配置或SELinux权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 4. 点击事件 - 关闭串口并注销回调
        btnCloseSerial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentOpenedPath != null) {
                    fbSDK.unregisterSerialCallback(mCurrentOpenedPath, mSerialCallback);
                    fbSDK.closeSerial(mCurrentOpenedPath);
                    Toast.makeText(MainActivity.this, "串口 " + mCurrentOpenedPath + " 已关闭", Toast.LENGTH_SHORT).show();
                    mCurrentOpenedPath = null;
                    updateSerialUiState(false);
                }
            }
        });
        // 5. 将文本作为普通 ASCII 字符串发送
        btnSendStr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentOpenedPath == null) {
                    Toast.makeText(MainActivity.this, "请先打开串口", Toast.LENGTH_SHORT).show();
                    return;
                }
                String dataStr = etSendData.getText().toString();
                byte[] data = dataStr.getBytes();
                int ret = fbSDK.writeSerial(mCurrentOpenedPath, data);
                if (ret >= 0) {
                    Toast.makeText(MainActivity.this, "写入成功: " + ret + "字节", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "写入失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 6. 将编辑框内容作为 HEX 发送 (例如输入 55 AA 01 02)
        btnSendHex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentOpenedPath == null) {
                    Toast.makeText(MainActivity.this, "请先打开串口", Toast.LENGTH_SHORT).show();
                    return;
                }
                String rawStr = etSendData.getText().toString().replace(" ", "");
                try {
                    byte[] data = hexStringToByteArray(rawStr);
                    int ret = fbSDK.writeSerial(mCurrentOpenedPath, data);
                    if (ret >= 0) {
                        Toast.makeText(MainActivity.this, "Hex写入成功: " + ret + "字节", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "写入失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (IllegalArgumentException e) {
                    Toast.makeText(MainActivity.this, "Hex解析错误，请确认格式是否正确", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 7. 清理日志控制
        btnClearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvReceiveLog.setText("");
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedApkUri = data.getData(); // 获取 URI
            EditText setApkPath = findViewById(R.id.setApkPath_et);
            if (selectedApkUri != null) {
                setApkPath.setText(selectedApkUri.toString()); // UI 显示 URI
            }
        }
    }
    // --- 扫描方法：提取到类级别 ---
    private void performScan() {
        Toast.makeText(this, "正在扫描...", Toast.LENGTH_SHORT).show();
        fbSDK.startScan(new com.example.fbsdk.FbSDK.WifiScanListener() {

            @Override
            public void onScanResults(List<FbSDK.QyScanResultInfo> results) {
                runOnUiThread(() -> {
                    wifiDataList.clear();
                    wifiDataList.addAll(results);
                    adapter.notifyDataSetChanged();
                });
            }
            @Override
            public void onError(int errorCode) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "扫描失败: " + errorCode, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
    private void showPasswordDialog(FbSDK.QyScanResultInfo wifiInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("连接到 " + wifiInfo.ssid);
        final EditText passwordInput = new EditText(this);
        passwordInput.setHint("输入密码");
        builder.setView(passwordInput);

        final String[] securityTypes = {"WPA2", "OPEN", "WPA_PSK"};
        final int[] checkedItem = {0};
        builder.setSingleChoiceItems(securityTypes, 0, (dialog, which) -> checkedItem[0] = which);

        builder.setPositiveButton("连接", (dialog, which) -> {
            String pass = passwordInput.getText().toString();
            String type = securityTypes[checkedItem[0]];
            fbSDK.connectTo(wifiInfo.ssid, pass, type);
            Toast.makeText(this, "正在连接...", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void showWiFiInfo() {
        FbSDK.QyWiFiInfo wifiInfo = fbSDK.getConnectedWiFiInfo();

        if (wifiInfo == null) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("当前未连接到 WiFi 或 SDK 未返回有效信息")
                    .setPositiveButton("确定", null)
                    .show();
            return;
        }

        StringBuilder infoBuilder = new StringBuilder();
        infoBuilder.append("SSID: ").append(wifiInfo.getSsid()).append("\n");
        infoBuilder.append("BSSID: ").append(wifiInfo.getBssid()).append("\n");
        infoBuilder.append("IP Address: ").append(wifiInfo.getIpAddress()).append("\n");
        infoBuilder.append("Gateway: ").append(wifiInfo.getGateway()).append("\n");
        infoBuilder.append("Netmask: ").append(wifiInfo.getNetmask()).append("\n");
        infoBuilder.append("DNS1: ").append(wifiInfo.getDns1()).append("\n");
        infoBuilder.append("DNS2: ").append(wifiInfo.getDns2()).append("\n");
        infoBuilder.append("MAC Address: ").append(wifiInfo.getMacAddress()).append("\n");

        new AlertDialog.Builder(this)
                .setTitle("WiFi Information")
                .setMessage(infoBuilder.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * 打开/关闭 状态切换下的控件使能控制
     */
    private void updateSerialUiState(boolean isOpened) {
        btnOpenSerial.setEnabled(!isOpened);
        etSerialPath.setEnabled(!isOpened);
        etSerialBaud.setEnabled(!isOpened);

        btnCloseSerial.setEnabled(isOpened);
        btnSendStr.setEnabled(isOpened);
        btnSendHex.setEnabled(isOpened);
    }
    /**
     * 辅助转换方法：十六进制字符串转Byte[]
     */
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have an even length");
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    /**
     * 辅助转换方法：Byte[] 转化为 十六进制字符串
     */
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 如果 Activity 退出，清理串口后台数据监听，防止 Binder 泄露
        if (mCurrentOpenedPath != null && mSerialCallback != null) {
            fbSDK.unregisterSerialCallback(mCurrentOpenedPath, mSerialCallback);
        }
    }


}
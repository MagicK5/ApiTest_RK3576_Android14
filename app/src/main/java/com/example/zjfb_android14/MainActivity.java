package com.example.zjfb_android14;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    private Uri selectedApkUri;
    private FbSDK fbSDK;
    // 将这些定义为成员变量，以便所有方法共享
    private List<FbSDK.QyScanResultInfo> wifiDataList = new ArrayList<>();
    private WifiListAdapter adapter;
    private ListView wifiListView;
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

}
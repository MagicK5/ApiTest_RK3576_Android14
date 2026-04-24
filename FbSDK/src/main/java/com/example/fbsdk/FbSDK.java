package com.example.fbsdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.qyapi.QyApiManager;
import android.util.Log;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import java.io.FileNotFoundException;
import android.qyapi.WiFiInfo;
import android.qyapi.ScanResultInfo;

import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.Result;

public class FbSDK {
    private String TAG = "FbSdk";
    private Context context;
    private final QyApiManager manager;

    public interface OperationCallback {
        void onResult(boolean success, String message);
    }

    @SuppressLint("WrongConstant")
    public FbSDK(Context context){
        this.context = context;
        this.manager = (QyApiManager) context.getSystemService("qy_api_service");
    }

    /**
     * wifi信息类
     */
    public static class QyWiFiInfo{
        private String ssid;
        private String bssid;
        private String ipAddress;
        private String gateway;
        private String netmask;
        private String dns1;
        private String dns2;
        private String macAddress;

        public void setSsid(String ssid) {
            this.ssid = ssid;
        }

        public void setBssid(String bssid) {
            this.bssid = bssid;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public void setGateway(String gateway) {
            this.gateway = gateway;
        }

        public void setNetmask(String netmask) {
            this.netmask = netmask;
        }

        public void setDns1(String dns1) {
            this.dns1 = dns1;
        }

        public void setDns2(String dns2) {
            this.dns2 = dns2;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }

        public String getSsid() {
            return this.ssid;
        }

        public String getBssid() {
            return this.bssid;
        }

        public String getIpAddress() {
            return this.ipAddress;
        }

        public String getGateway() {
            return this.gateway;
        }

        public String getNetmask() {
            return this.netmask;
        }

        public String getDns1() {
            return this.dns1;
        }

        public String getDns2() {
            return this.dns2;
        }

        public String getMacAddress() {
            return this.macAddress;
        }

        public List<String> toStringList() {
            List<String> data = new ArrayList<>();
            data.add(ssid);
            data.add(bssid);
            data.add(ipAddress);
            data.add(gateway);
            data.add(netmask);
            data.add(dns1);
            data.add(dns2);
            data.add(macAddress);
            return data;
        }

        public static QyWiFiInfo fromStringList(List<String> data) {
            if (data == null || data.size() < 8) {
                throw new IllegalArgumentException("Invalid data list for WiFiInfo");
            }
            QyWiFiInfo wiFiInfo = new QyWiFiInfo();
            wiFiInfo.setSsid(data.get(0));
            wiFiInfo.setBssid(data.get(1));
            wiFiInfo.setIpAddress(data.get(2));
            wiFiInfo.setGateway(data.get(3));
            wiFiInfo.setNetmask(data.get(4));
            wiFiInfo.setDns1(data.get(5));
            wiFiInfo.setDns2(data.get(6));
            wiFiInfo.setMacAddress(data.get(7));
            return wiFiInfo;
        }


    }

    /**
     * 扫描结果返回类
     */
    public static class QyScanResultInfo {
        public  String ssid;
        public  String bssid;
        public  int level;
        public  int frequency;

        public QyScanResultInfo( String ssid, String bssid, int level, int frequency) {
            this.ssid = ssid;
            this.bssid = bssid;
            this.level = level;
            this.frequency = frequency;
        }
    }

    /**
     * 扫描结果监听器
     */
    public interface WifiScanListener {
        void onScanResults(List<QyScanResultInfo> results);
        void onError(int errorCode);
    }

    /**
     * 设置系统亮度
     * @param value 亮度值，只接受0-255之间的int
     * @return 0成功，-1失败，详细日志关注Log中的QyApi***。
     */
    public int setBrightness(int value) {
        if(value>255){
            value = 255;
            Log.e(TAG,"reset Max brightness to 255!");
        }
        if(value<0){
            value = 0;
            Log.e(TAG,"reset Min brightness to 0!");
        }
        if (manager != null) {
            return manager.setBrightness(value);
        }
        return -1;
    }

    /**
     * 获取当前系统亮度
     * @return 当前系统亮度，通常为0-255之间的int,-1代表失败，详细日志关注Log中的QyApi***
     */
    public int getBrightness(){
        if(manager != null){
            return manager.getBrightness();
        }
        return -1;
    }

    /**
     * 获取当前系统能够设置的最大亮度
     * @return 系统允许的最大亮度，通常为0-255之间的int,-1代表失败，详细日志关注Log中的QyApi***
     */
    public int getMaxBrightness(){
        if(manager != null){
            return manager.getMaxBrightness();
        }
        return -1;
    }

    /**
     * 获取当前系统能够设置的最小亮度
     * @return 系统允许的最小亮度，通常为0-255之间的int,-1代表失败，详细日志关注Log中的QyApi***
     */
    public int getMinBrightness(){
        if(manager != null){
            return manager.getMinBrightness();
        }
        return -1;
    }

    /**
     * 设置媒体音量
     * @param volume 设定的音量值，只接受 - 的int
     * @return 0成功，-1失败，详细日志关注Log中的QyApi***
     */
    public int setVolume(int volume){
        if(manager != null){
            return manager.setVolume(volume);
        }
        return -1;
    }

    /**
     * 获取当前媒体音量
     * @return 当前媒体音量，通常为 - 之间的int,-1代表失败，详细日志关注Log中的QyApi***
     */
    public int getVolume(){
        if(manager != null){
            return manager.getVolume();
        }
        return -1;
    }

    /**
     * 获取系统允许的最大音量值
     * @return 当前系统允许最大音量，通常为0-15之间的int,-1代表失败，详细日志关注Log中的QyApi***
     */
    public int getMaxVolume(){
        if(manager != null){
            return manager.getMaxVolume();
        }
        return -1;
    }

    /**
     * 获取系统允许的最小音量值
     * @return 当前系统允许最小音量值，通常为0-15之间的int,-1代表失败，详细日志关注Log中的QyApi***
     */
    public int getMinVolume(){
        if(manager != null){
            return manager.getMinVolume();
        }
        return -1;
    }


    /**
     * SystemBar
     */

    /**
     * 控制导航栏显示/隐藏状态
     * @param visible true显示，false隐藏
     * @return 0成功，-1失败，详细日志关注Log中的QyApi***
     */
    public int setNavigationBarStatus(boolean visible){
        if(manager != null){
            return manager.setNavigationBarStatus(visible);
        }
        return -1;
    }

    /**
     * 控制状态栏显示/隐藏昨天
     * @param visible true显示，false隐藏
     * @return 0成功，-1失败，详细日志关注Log中的QyApi***
     */
    public int setStatusBarStatus(boolean visible){
        if(manager != null){
            return manager.setStatusBarStatus(visible);
        }
        return -1;
    }

    /**
     * 获取当前导航栏显示状态
     * @return true显示，false隐藏；失败也返回false,详情关注Log中的QyApi***
     */
    public boolean isNavigationBarStatus(){
        if(manager != null){
            return manager.isNavigationBarVisible();
        }
        return false;
    }

    /**
     * 获取当前状态栏显示状态
     * @return true显示，false隐藏；失败也返回false,详情关注Log中的QyApi***
     */
    public boolean isStatusBarStatus(){
        if(manager != null){
            return manager.isStatusBarVisible();
        }
        return false;
    }


    /**
     * ScreenControl
     */

    /**
     * 控制屏幕亮灭
     * @param on true唤醒，false休眠
     * @return 0成功，-1失败，详情关注Log中的QyApi***
     */
    public int setScreenState(boolean on){
        if(manager != null){
            return manager.setScreenState(on);
        }
        return -1;
    }

    /**
     * InstallSilently
     */

    // 重载1：简版安装（不需要回调，只看日志）
    public void installPackage(Uri apkUri) {
        installPackage(apkUri, (success, msg) -> {
            if (!success) Log.e(TAG, "Install failed: " + msg);
        });
    }
    // 重载2：完整版安装（带回调）
    public void installPackage(Uri apkUri, OperationCallback callback) {
        try {
            // 使用 ContentResolver 打开文件描述符
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(apkUri, "r");
            manager.installPackageSilently(pfd, (code, msg) -> {
                if (callback != null) callback.onResult(code == 0, msg);
            });
        } catch (FileNotFoundException e) {
            callback.onResult(false, "File not found: " + e.getMessage());
        }
    }
    // --- 优化后的卸载方法 ---
    // 简版卸载
    public void uninstallPackage(String packageName) {
        uninstallPackage(packageName, (success, msg) -> {
            if (!success) Log.e(TAG, "Uninstall failed: " + msg);
        });
    }
    // 完整版卸载
    public void uninstallPackage(String packageName, OperationCallback callback) {
        try{
            manager.uninstallSilently(packageName ,(code, msg) -> {
                if (callback != null) callback.onResult(code == 0, msg != null ? msg : "Code: " + code);

            });
        }catch (Exception e){
            callback.onResult(false,"Uninstall Failed:" +e.getMessage());
        }

    }


    /**
     * WiFi
     */

    public boolean isWifiEnabled() {
        if(manager != null){
            return manager.isWifiEnabled();
        }
        return false;
    }

    public QyWiFiInfo getConnectedWiFiInfo(){
        if (manager == null) return null;

        WiFiInfo info = manager.getConnectedInfo();
        if(info == null)return null;
        QyWiFiInfo wifiInfo = new QyWiFiInfo();
        wifiInfo.setBssid(info.bssid);
        wifiInfo.setDns1(info.dns1);
        wifiInfo.setDns2(info.dns2);
        wifiInfo.setGateway(info.gateway);
        wifiInfo.setSsid(info.ssid);
        wifiInfo.setNetmask(info.netmask);
        wifiInfo.setIpAddress(info.ipAddress);
        wifiInfo.setMacAddress(info.macAddress);

        return wifiInfo;
    }

    public String getWifiIpAddress(){
        if(manager != null) {
            return manager.getWifiIpAddress();
        }
        return "0.0.0.0";
    }

    public int enableWifi(boolean enable){
        if(manager != null) {
            return manager.enableWifi(enable);
        }
        return -1;
    }

    public void startScan(final WifiScanListener listener) {
        if (manager == null) {
            if (listener != null) listener.onError(-1);
            return;
        }
        try {
            // 实例化 ResultReceiver，专门负责处理 IPC 回调
            ResultReceiver receiver = new ResultReceiver(new Handler(Looper.getMainLooper())) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    if (listener == null) return;

                    if (resultData == null) {
                        listener.onError(-2);
                        return;
                    }
                    // 获取服务端回传的 ArrayList
                    // 注意：这里的 Key "results" 必须与 WifiController.java 里的 Key 一致
                    ArrayList<ScanResultInfo> results = resultData.getParcelableArrayList("results");

                    if (results != null) {
                        // 转换数据给上层监听器
                        List<QyScanResultInfo> sdkResults = new ArrayList<>();
                        for (ScanResultInfo res : results) {
                            sdkResults.add(new QyScanResultInfo(res.ssid, res.bssid, res.level, res.frequency));
                        }
                        listener.onScanResults(sdkResults);
                    } else {
                        listener.onError(0); // 空结果
                    }
                }
            };

            // 传入 ResultReceiver 实例
            manager.startScan(receiver);

        } catch (Exception e) {
            Log.e(TAG, "Start Scan Failed: " + e.getMessage(), e);
            if (listener != null) listener.onError(-2);
        }
    }

    public void connectTo(String ssid, String password, String securityType){
        if(manager != null){
            manager.connectTo(ssid,password,securityType);
        }
    }

    public void test(){
        Log.e(TAG,"null");
    }
}

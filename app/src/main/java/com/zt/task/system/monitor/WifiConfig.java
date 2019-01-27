package com.zt.task.system.monitor;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 获取Wifi配置信息
 *
 * @author yangyp
 * @created 2015/05/27
 */
public class WifiConfig {

    private WifiInfo wifiInfo;
    private String gatewayIp; // 网关IP

    public WifiConfig(WifiInfo wifiInfo, String gatewayIp) {
        this.wifiInfo = wifiInfo;
        this.gatewayIp = gatewayIp;
    }

    /**
     * Wifi名称
     *
     * @return
     */
    public String getSSID() {
        String ssid = wifiInfo.getSSID();
        if (!TextUtils.isEmpty(ssid)) {
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.replace("\"", "");
            }
        }
        return ssid;
    }

    /**
     * Wifi信号强度
     *
     * @param numLevels
     * @return
     */
    public int getSignalLevel(int numLevels) {
        return WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);
    }

    /**
     * Wifi Mac地址
     *
     * @return the BSSID, in the form of a six-byte MAC address:
     * {@code XX:XX:XX:XX:XX:XX}
     */
    public String getBSSID() {
        return wifiInfo.getBSSID();
    }

    /**
     * Wifi  IpAddress
     *
     * @return
     */
    public String getIpAddress() {
        return intIP2StringIP(wifiInfo.getIpAddress());
    }

    /**
     * Wifi速度
     *
     * @return
     */
    public int getLinkSpeed() {
        return wifiInfo.getLinkSpeed();
    }

    public String getGatewayIp() {
        return gatewayIp;
    }

    // network available cannot ensure Internet is available
    public boolean isNetWorkAvailable(final Context context) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process pingProcess = runtime.exec("/system/bin/ping -c 1 www.baidu.com");
            int exitCode = pingProcess.waitFor();
            return (exitCode == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
//            Log.e(LOG_TAG, ex.toString());
        }
        return null;
    }

    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
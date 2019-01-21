package com.zt.task.system.monitor;

import android.content.Context;
import android.database.Observable;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.widget.Toast;

import com.zt.task.system.util.LogUtils;
import com.zt.task.system.util.ToastUtil;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;


/**
 * Pisen Wifi监听器
 *
 * @author yangyp
 */
public class WifiMonitor extends Observable<WifiMonitor.WifiStateCallback> implements IMonitor {

    private ConnectivityManager connectivityManager;
    private Context context;

    public interface WifiStateCallback {
        /**
         * 网络已连接
         */
        void onConnected(WifiConfig config);

        /**
         * 网络已断开
         */
        void onDisconnected(WifiConfig config);
    }

    public interface VpnStateCallback {

        void onVpnConnected();

        void onVpnDisconnected();

    }

    ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() {

        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);

            ToastUtil.show(context, "网络-Available", Toast.LENGTH_SHORT);
            boolean vpnUsed = isVpnUsed();
            if (vpnUsed) {
                LogUtils.e("vpn Connnected.....");
//                vpnConnected(); 表示已经连接上了
            } else {
//                vpnDisconnected();表示已经断开了
                LogUtils.e("vpn Disconnnected.....");
            }
            LogUtils.e("ConnectivityReceiver.onReceive()..." + connectivityManager.isDefaultNetworkActive());

            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                LogUtils.e("Network connected Type  = " + netInfo.getTypeName() + ", State = " + netInfo.getState());
                connected(context, netInfo);
            }
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            LogUtils.e("Network onLost");
            disconnected();
        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
            LogUtils.e("Network onLosing");
        }

        /**
         * 当建立网络连接时，回调连接的属性
         * */
        @Override
        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
            LogUtils.e("Network onLinkPropertiesChanged");
        }

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            LogUtils.e(" high power state=" + connectivityManager.isDefaultNetworkActive());
            LogUtils.e("Network onCapabilitiesChanged::hasNetWork=" + networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED));
        }
    };

    public static boolean isVpnUsed() {
        try {
            Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
            if (niList != null) {
                for (NetworkInterface intf : Collections.list(niList)) {
                    if (!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    LogUtils.e("isVpnUsed() NetworkInterface Name: " + intf.getName());
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
                        return true; // The VPN is up
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 过滤品胜路由器
     */
    public static final String PISEN_BSSID_PREFIX = "3c:40:4f";
    private boolean connected = false;
    private WifiConfig wifiConfig;
    static WifiMonitor instance = null;

    private WifiMonitor() {
    }

    public static WifiMonitor getInstance() {
        if (instance == null) {
            instance = new WifiMonitor();
        }
        return instance;
    }

    private static final NetworkRequest VPN_REQUEST = new NetworkRequest.Builder()
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)
            .build();

    @Override
    public void startMonitor(Context context) {
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        connectivityManager.requestNetwork(VPN_REQUEST, mNetworkCallback);
        connectivityManager.registerNetworkCallback(VPN_REQUEST,mNetworkCallback);
    }

    @Override
    public void stopMonitor(Context context) {
        unregisterAll();
    }

    public boolean isWifiConnected() {
        return connected;
    }


    @Deprecated
    public WifiConfig getWifiConfig() {
        return wifiConfig;
    }

    /**
     * 网络已连接
     */
    public void notifyConnected(WifiConfig config) {
        synchronized (mObservers) {
            for (WifiStateCallback observer : mObservers) {
                observer.onConnected(config);
            }
        }
    }

    /**
     * 网络已断开
     */
    public void notifyDisconnected(WifiConfig config) {
        synchronized (mObservers) {
            for (WifiStateCallback observer : mObservers) {
                observer.onDisconnected(config);
            }
        }
    }

    private void connected(Context context, NetworkInfo netInfo) {
        connected = true;

        int netType = netInfo.getType();
        if (netType == ConnectivityManager.TYPE_WIFI) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                wifiInfo.getIpAddress();
                DhcpInfo info = wifiManager.getDhcpInfo();
                String gateway = (info != null) ? Formatter.formatIpAddress(info.gateway) : null;
                wifiConfig = new WifiConfig(wifiInfo, gateway);
                notifyConnected(wifiConfig);
            }
        } else if (netType == ConnectivityManager.TYPE_ETHERNET) {
//                    context.getSystemService(Context.ETHERNET_SERVICE);
//            notifyConnected(wifiConfig);
        } else if (netType == connectivityManager.TYPE_VPN) {
            notifyConnected(wifiConfig);
        }
    }

    private void disconnected() {
        if (wifiConfig != null) {
            connected = false;
            notifyDisconnected(wifiConfig);
            wifiConfig = null;
        }
    }


}

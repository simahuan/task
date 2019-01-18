package android.izy.app;

import android.content.Context;
import android.izy.service.NetworkManager;
import android.izy.service.NetworkStateCallback;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

/**
 * Activity基类
 * <p>
 * 使用到的权限： <br>
 * 1)android.permission.INTERNET<br>
 * 2)android.permission.CHANGE_NETWORK_STATE 允许程序改变网络连接状态<br>
 * 3)android.permission.CHANGE_WIFI_STATE 允许程序改变Wi-Fi连接状态<br>
 * 4)android.permission.ACCESS_NETWORK_STATE 是获取网络状态的权限控制<br>
 * 5)android.permission.ACCESS_WIFI_STATE 是获取使用Wi-Fi状态的权限控制
 * </p>
 * 
 * @author yangyp
 * @version 1.0, 2014年11月14日 上午9:37:35
 */
public abstract class NetActivitySupport extends ActivitySupport implements NetworkStateCallback {

	private NetworkManager networkManager;
	private WifiInfo info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkManager = new NetworkManager(this);
		networkManager.registerConnectivityReceiver(this);
	}

	@Override
	public void onConnected(int netType) {
		if (netType == ConnectivityManager.TYPE_WIFI) {
			WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			info = wifiMgr.getConnectionInfo();
			onWifiConnected(info);
		}
	}

	@Override
	public void onDisconnected(int netType) {
		WifiInfo infoTemp = info;
		if (infoTemp != null) {
			info = null;
			onWifiDisconnected(infoTemp);
		}
	}

	/**
	 * 当前连接的Wifi
	 */
	public void onWifiConnected(WifiInfo info) {
	}

	/**
	 * 当前失去连接的Wifi
	 */
	public void onWifiDisconnected(WifiInfo info) {
	}

	@Override
	protected void onDestroy() {
		networkManager.unregisterConnectivityReceiver();
		super.onDestroy();
	}

}

package android.izy.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Wifi监听服务
 * <p>
 * 添加权限 android:name="android.permission.ACCESS_NETWORK_STATE"
 * </p>
 * 
 * @author yangyp
 * @version 1.0, 2014年8月5日 下午1:01:54
 */
public abstract class WifiService extends NetworkService {

	static final String TAG = WifiService.class.getSimpleName();
	private WifiInfo info;

	@Override
	public final void onConnected(int netType) {
		super.onConnected(netType);
		if (netType == ConnectivityManager.TYPE_WIFI) {
			WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			info = wifiMgr.getConnectionInfo();
			onWifiConnected(info);
		}
	}

	@Override
	public final void onDisconnected(int netType) {
		super.onDisconnected(netType);
		WifiInfo infoTemp = info;
		if (infoTemp != null) {
			info = null;
			onWifiDisconnected(infoTemp);
		}
	}

	/**
	 * 去除Wifi双引号
	 * 
	 * @param string
	 * @return
	 */
	public static String removeDoubleQuotes(String string) {
		if (string == null)
			return null;
		final int length = string.length();
		if ((length > 1) && (string.charAt(0) == '"') && (string.charAt(length - 1) == '"')) {
			return string.substring(1, length - 1);
		}
		return string;
	}

	/**
	 * 当前连接的Wifi
	 */
	public abstract void onWifiConnected(WifiInfo info);

	/**
	 * 当前失去连接的Wifi
	 */
	public abstract void onWifiDisconnected(WifiInfo info);

}

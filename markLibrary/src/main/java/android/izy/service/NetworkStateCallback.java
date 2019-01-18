package android.izy.service;

/**
 * 网络连接状态回调
 * 
 * @author yangyp
 * 
 */
public interface NetworkStateCallback {

	/**
	 * 网络已连接
	 * 
	 * @param netType
	 *            网络类型
	 * @link ConnectivityManager#TYPE_MOBILE
	 * @link ConnectivityManager#TYPE_WIFI
	 * @link ConnectivityManager#TYPE_WIMAX
	 * @link ConnectivityManager#TYPE_ETHERNET
	 * @link ConnectivityManager#TYPE_BLUETOOTH
	 * @link ConnectivityManager
	 */
	void onConnected(int netType);

	/**
	 * 网络已断开
	 */
	void onDisconnected(int netType);
}

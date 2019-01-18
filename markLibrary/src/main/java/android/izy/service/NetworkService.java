package android.izy.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 网络服务
 * <p>
 * 添加权限 android:name="android.permission.ACCESS_NETWORK_STATE"
 * </p>
 * 
 * @author yangyp
 * @version 1.0, 2014年8月5日 下午1:01:16
 */
public abstract class NetworkService extends Service implements NetworkStateCallback {

	static final String TAG = NetworkService.class.getSimpleName();

	private NetworkManager networkManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		networkManager = new NetworkManager(this);
		networkManager.registerConnectivityReceiver(this);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy()...");
		networkManager.unregisterConnectivityReceiver();
	}

	@Override
	public void onConnected(int netType) {
		Log.d(TAG, "connect()...");
	}

	@Override
	public void onDisconnected(int netType) {
		Log.d(TAG, "disconnect()...");
	}

}

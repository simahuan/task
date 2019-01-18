package android.izy.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * 检查网络连接状态
 * 
 * @author yangyp
 * 
 */
public class ConnectivityReceiver extends BroadcastReceiver {

	private static final String TAG = ConnectivityReceiver.class.getSimpleName();
	private NetworkStateCallback callback;

	public ConnectivityReceiver(NetworkStateCallback callback) {
		this.callback = callback;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "ConnectivityReceiver.onReceive()...");
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			Log.i(TAG, "Network connected Type  = " + netInfo.getTypeName() + ", State = " + netInfo.getState());
			callback.onConnected(netInfo.getType());
		} else {
			Log.i(TAG, "Network unavailable");
			callback.onDisconnected(-1);
		}
	}
}

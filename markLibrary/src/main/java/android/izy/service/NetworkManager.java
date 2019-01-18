package android.izy.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 网络管理类
 * 
 * @author yangyp
 * @version 1.0, 2014年8月4日 上午11:45:52
 */
public class NetworkManager implements NetworkStateCallback {

	static final String TAG = NetworkManager.class.getSimpleName();
	private Context context;
	private TelephonyManager telephonyManager;
	private BroadcastReceiver connectivityReceiver;
	private PhoneStateListener phoneStateListener;
	private NetworkStateCallback call;

	public NetworkManager(Context context) {
		this.context = context;
		telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		connectivityReceiver = new ConnectivityReceiver(this);
		phoneStateListener = new PhoneStateChangeListener(this);
	}

	public void registerConnectivityReceiver(NetworkStateCallback call) {
		this.call = call;
		Log.d(TAG, "registerConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
		// context.registerReceiver(connectivityReceiver, new
		// IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
		context.registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}

	public void unregisterConnectivityReceiver() {
		Log.d(TAG, "unregisterConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		context.unregisterReceiver(connectivityReceiver);
	}

	@Override
	public void onConnected(int netType) {
		if (call != null) {
			call.onConnected(netType);
		}
	}

	@Override
	public void onDisconnected(int netType) {
		if (call != null) {
			call.onDisconnected(netType);
		}
	}
}

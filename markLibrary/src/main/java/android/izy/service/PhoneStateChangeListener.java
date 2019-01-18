package android.izy.service;

import android.net.ConnectivityManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 接收手机连接状态
 * 
 * @author yangyp
 * 
 */
public class PhoneStateChangeListener extends PhoneStateListener {

	private static final String TAG = PhoneStateChangeListener.class.getSimpleName();
	private final NetworkStateCallback callback;

	public PhoneStateChangeListener(NetworkStateCallback callback) {
		this.callback = callback;
	}

	@Override
	public void onDataConnectionStateChanged(int state) {
		super.onDataConnectionStateChanged(state);
		Log.i(TAG, "onDataConnectionStateChanged()...Connection State = " + getState(state));

		// 此方法只能检查手机连接状态，不能检查手机无线连接状态(即：如果手机没手机卡，但是无线有连接，也会返回false)
		if (state == TelephonyManager.DATA_CONNECTED) {
			callback.onConnected(ConnectivityManager.TYPE_MOBILE);
		}
	}

	private String getState(int state) {
		switch (state) {
		case 0: // '\0'
			return "DATA_DISCONNECTED";
		case 1: // '\001'
			return "DATA_CONNECTING";
		case 2: // '\002'
			return "DATA_CONNECTED";
		case 3: // '\003'
			return "DATA_SUSPENDED";
		}
		return "DATA_<UNKNOWN>";
	}

}

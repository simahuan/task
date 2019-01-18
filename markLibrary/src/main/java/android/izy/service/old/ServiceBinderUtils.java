package android.izy.service.old;

import java.util.HashMap;

import android.izy.service.WifiService;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * 服务绑定工具类
 * 
 * @author yangyp
 * @version 1.0, 2014年8月19日 下午8:31:34
 */
public abstract class ServiceBinderUtils {

	private static final String TAG = "ServiceUtils";
	private static final HashMap<Class<?>, ServiceBinderObservable> mConnectionMap = new HashMap<Class<?>, ServiceBinderObservable>();

	/**
	 * 绑定服务
	 * 
	 * @param context
	 * @return
	 */
	public static void bindToService(Context context, Class<? extends Service> clazz) {
		bindToService(context, clazz);
	}

	/**
	 * 绑定服务
	 * <p>
	 * 每绑定一次，增加一个新的绑定
	 * </p>
	 * 
	 * @param context
	 * @param callback
	 * @return
	 */
	public static void bindToService(Context context, Class<? extends Service> clazz, ServiceConnection callback) {
		ServiceBinderObservable observable = mConnectionMap.get(clazz);
		if (observable == null) {
			observable = mConnectionMap.put(clazz, observable);
			context.startService(new Intent(context, clazz));
		}
		// ServiceBinder sb = new ServiceBinder(clazz);
		if (context.bindService((new Intent()).setClass(context, clazz), callback, 0)) {
			observable.registerObserver(callback);
			return;
		}
		Log.e(TAG, "Failed to bind to service");
	}

	/**
	 * 解除服务
	 * <p>
	 * 只需调用一次，就可能解除所有绑定
	 * </p>
	 * 
	 * @param token
	 */
	public static void unbindFromService(Context context, Class<? extends Service> clazz) {
		ServiceBinderObservable observable = mConnectionMap.remove(clazz);
		if (observable == null) {
			Log.e(TAG, "Trying to unbind for unknown Context");
			return;
		}
		observable.notifyUnbindService(context, clazz);
		context.stopService(new Intent(context, clazz));
	}

	public static void main(String[] args) {
		ServiceBinderUtils.bindToService(null, WifiService.class, new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {

			}
		});

		ServiceBinderUtils.unbindFromService(null, WifiService.class);
	}
}

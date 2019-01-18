package android.izy;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.izy.AppUncaughtException.UncaughtExceptionHandler;
import android.izy.preference.PreferencesUtils;
import android.util.Log;
import android.volley.RequestManager;

/**
 * Application基类
 * <p>
 * 使用到的权限： <br>
 * 1)android.permission.INTERNET<br>
 * 2)android.permission.WRITE_EXTERNAL_STORAGE<br>
 * 4)android.permission.ACCESS_NETWORK_STATE<br>
 * 5)android.permission.ACCESS_WIFI_STATE<br>
 * 6)android.permission.READ_PHONE_STATE
 * </p>
 * 
 * @author yangyp
 * @version 1.0, 2014年11月14日 上午9:37:35
 */
public class ApplicationSupport extends Application implements UncaughtExceptionHandler {

	static final String TAG = ApplicationSupport.class.getName();
	private AppUncaughtException uncaughtException;
	private Map<String, Object> intentExtras = new HashMap<String, Object>();

	@Override
	public void onCreate() {
		super.onCreate();
		uncaughtException = AppUncaughtException.getInstance(this);
		uncaughtException.setUncaughtExceptionHandler(this);

		RequestManager.init(this);
		PreferencesUtils.init(this);
	}

	/**
	 * 根据name取出Intent数据包
	 * 
	 * @param name
	 * @return
	 */
	public Object[] getIntentExtras(String name) {
		return (Object[]) intentExtras.remove(name);
	}

	/**
	 * 根据name加入Intent数据包
	 * 
	 * @param name
	 * @param value
	 */
	public void putIntentExtras(String name, Object... value) {
		intentExtras.put(name, value);
	}

	/**
	 * 第一个activity被执行时调用
	 */
	public void onActivityLauncher() {
		Log.i("BaseApplication", "onActivityLauncher()");
	}

	/**
	 * 所有activity关闭时调用
	 */
	public void onActivityTerminate() {
		Log.i("BaseApplication", "onActivityTerminate()");
		intentExtras.clear();
	}

	@Override
	public void uncaughtException(AppUncaughtException handler, Throwable ex) {
		Log.e(TAG, "uncaughtException: ", ex);
		/*
		 * File[] crFiles = uncaughtException.getCrashReportFiles(); if (crFiles
		 * != null) { for (File cr : crFiles) { Log.d(TAG, "正在发送错误报告文件: " +
		 * cr.getName()); // file.delete();// 删除已发送的报告 } }
		 */
	}

}

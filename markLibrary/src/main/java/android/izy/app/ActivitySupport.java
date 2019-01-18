package android.izy.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.izy.ApplicationSupport;
import android.izy.content.IntentWrapper;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

/**
 * Activity基类
 * 
 * @author yangyp
 * @version 1.0, 2014-7-8 下午1:02:45
 */
public abstract class ActivitySupport extends FragmentActivity implements IActivity {

	protected static final String TAG = ActivitySupport.class.getSimpleName();
	public static final String DEFAULT_LOADING_MESSAGE = "加载中,请稍后..."; // "Loading. Please wait...";

	private static boolean activityLauncher = false;

	protected AlertDialog progressDialog;
	private boolean destroyed = false;

	// 写一个广播的内部类，当收到动作时，结束activity
	private BroadcastReceiver exitReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// unregisterReceiver(this);
			// finishApplication();
			finish();
		}
	};

	// ***************************************
	// Activity methods
	// ***************************************
	@Override
	public ApplicationSupport getApplicationContext() {
		return (ApplicationSupport) super.getApplicationContext();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!activityLauncher) {
			getApplicationContext().onActivityLauncher();
			activityLauncher = true;
		}

		IntentWrapper intentWrapper = new IntentWrapper(getApplicationContext(), getIntent());
		onCreate(savedInstanceState, intentWrapper.getIntentExtras());
		registerReceiver(exitReceiver, new IntentFilter(getPackageName())); // 注册
	}

	/**
	 * 活动开始时调用
	 * 
	 * @param savedInstanceState
	 * @param extras
	 *            intent参数
	 * @see #startActivity(Intent, Object...)
	 */
	protected void onCreate(Bundle savedInstanceState, Object[] extras) {

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		IntentWrapper intentWrapper = new IntentWrapper(getApplicationContext(), getIntent());
		onNewIntent(intent, intentWrapper.getIntentExtras());
	}

	/**
	 * activity已经存在时调用些方法
	 * 
	 * @param savedInstanceState
	 * @param extras
	 *            intent参数
	 */
	protected void onNewIntent(Intent intent, Object[] extras) {

	}

	/**
	 * 启动activity
	 * 
	 * @param cls
	 * @param extras
	 * @see #onCreate(Bundle, Object[])
	 */
	public void startActivity(Class<?> cls, Object... extras) {
		startActivity(new Intent(this, cls), extras);
	}

	/**
	 * 启动activity
	 * 
	 * @param action
	 * @param extras
	 * @see #onCreate(Bundle, Object[])
	 */
	public void startActivity(String action, Object... extras) {
		startActivity(new Intent(action), extras);
	}

	/**
	 * 启动activity
	 * 
	 * @param intent
	 * @param extras
	 * @see #onCreate(Bundle, Object[])
	 */
	public void startActivity(Intent intent, Object... extras) {
		IntentWrapper intentWrapper = new IntentWrapper(getApplicationContext(), intent);
		intentWrapper.putIntentExtras(extras);
		startActivity(intentWrapper);
	}

	/**
	 * 启动模式为FLAG_ACTIVITY_SINGLE_TOP的activity
	 * 
	 * @param cls
	 * @param extras
	 */
	public void startActivityForSingleTop(Class<?> cls, Object... extras) {
		startActivity(new Intent(this, cls).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), extras);
	}

	/**
	 * 启动模式为FLAG_ACTIVITY_SINGLE_TOP的activity
	 * 
	 * @param action
	 * @param extras
	 */
	public void startActivityForSingleTop(String action, Object... extras) {
		startActivity(new Intent(action).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), extras);
	}

	/**
	 * 启动service
	 * 
	 * @param intent
	 * @param extras
	 * @see #onCreate(Bundle, Object[])
	 */
	@Deprecated
	public ComponentName startService(Intent service, Object... extras) {
		IntentWrapper intentWrapper = new IntentWrapper(getApplicationContext(), service);
		intentWrapper.putIntentExtras(extras);
		return startService(intentWrapper);
	}

	@Deprecated
	public void sendBroadcast(Intent intent, Object... extras) {
		IntentWrapper intentWrapper = new IntentWrapper(getApplicationContext(), intent);
		intentWrapper.putIntentExtras(extras);
		sendBroadcast(intentWrapper);
	}

	/**
	 * 退出整个应用
	 */
	public void finishApplication() {
		sendBroadcast(new Intent(getPackageName()));
		getApplicationContext().onActivityTerminate();
		activityLauncher = false;
	}

	@Override
	protected void onDestroy() {
		destroyed = true;
		unregisterReceiver(exitReceiver);
		super.onDestroy();
	}

	// ***************************************
	// Public methods
	// ***************************************

	public void setProgressDialog(AlertDialog progressDialog) {
		this.progressDialog = progressDialog;
	}

	@Override
	public void showLoadingProgressDialog() {
		showProgressDialog(DEFAULT_LOADING_MESSAGE);
	}

	@Override
	public void showProgressDialog(CharSequence message) {
		showProgressDialog(message, new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				cancelProgressDialog();
			}
		});
	}

	@Override
	public void showProgressDialog(CharSequence message, final DialogInterface.OnCancelListener listener) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			// progressDialog.setIndeterminate(true);
			// 点击ProgressDialog以外的区域不ProgressDialog dismiss掉
			progressDialog.setCancelable(false);
			progressDialog.setOnCancelListener(null);
			progressDialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
						if (listener != null) {
							listener.onCancel(dialog);
						}
					}
					return true;
				}
			});
		}

		progressDialog.setMessage(message);
		progressDialog.show();
	}

	public void cancelProgressDialog() {
		dismissProgressDialog();
	}

	@Override
	public void dismissProgressDialog() {
		if (progressDialog != null && !destroyed) {
			progressDialog.dismiss();
		}
	}

}

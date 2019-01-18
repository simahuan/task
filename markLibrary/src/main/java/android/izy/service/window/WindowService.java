package android.izy.service.window;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Window窗口服务
 * 
 * @author yangyp
 * @version 1.0, 2014-6-4 上午10:35:28
 */
public class WindowService extends Service {

	static final String TAG = WindowService.class.getSimpleName();

	private WindowViewManager mWindowViewManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mWindowViewManager = new WindowViewManager(this);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		mWindowViewManager.detach();
		super.onDestroy();
	}

	public WindowController getWindowViewManager() {
		return mWindowViewManager;
	}

	public void addToBackStack(WindowViewBase view) {
		mWindowViewManager.addToBackStack(view);
	}

	public void addToBackStack(WindowViewBase view, boolean backStack) {
		mWindowViewManager.addToBackStack(view, backStack);
	}

	public void popBackStack() {
		mWindowViewManager.popBackStack();
	}

}

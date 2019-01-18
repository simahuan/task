package test;

import android.content.Intent;
import android.izy.R;
import android.izy.app.NetActivitySupport;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

/**
 * 启动引导页
 * 
 * @author zy
 * @created 2014年8月8日 下午11:59:26
 */
public class BootstrapActivity extends NetActivitySupport {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.ui_bootstrap);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(BootstrapActivity.this, Activity2xSupport.class));
				finish();
			}
		}, 3000);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}
}
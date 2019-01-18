package android.izy.app;

import android.graphics.drawable.Drawable;
import android.izy.view.UINavigationBar;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Activity基类带导航
 * <p>
 * 使用到的权限： <br>
 * 1)android.permission.INTERNET<br>
 * 2)android.permission.CHANGE_NETWORK_STATE 允许程序改变网络连接状态<br>
 * 3)android.permission.CHANGE_WIFI_STATE 允许程序改变Wi-Fi连接状态<br>
 * 4)android.permission.ACCESS_NETWORK_STATE 是获取网络状态的权限控制<br>
 * 5)android.permission.ACCESS_WIFI_STATE 是获取使用Wi-Fi状态的权限控制
 * </p>
 * 
 * @author yangyp
 * @version 1.0, 2014年11月14日 上午9:37:35
 */
public abstract class NavigationActivitySupport extends NetActivitySupport {

	private LinearLayout layout;
	private UINavigationBar mActionBar;
	private FrameLayout contentView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		mActionBar = new UINavigationBar(this);
		contentView = new FrameLayout(this);

		layout.addView(mActionBar);
		layout.addView(contentView);
		// layout.addView(toolbar);
		super.setContentView(layout);
	}

	public UINavigationBar getNavigationBar() {
		return mActionBar;
	}

	public void setBackgroundColor(int color) {
		layout.setBackgroundColor(color);
	}

	public void setBackgroundResource(int resid) {
		layout.setBackgroundResource(resid);
	}

	public void setBackground(Drawable background) {
		layout.setBackground(background);
	}

	@Override
	public void setContentView(int layoutResID) {
		setContentView(View.inflate(this, layoutResID, null));
	}

	@Override
	public void setContentView(View view) {
		setContentView(view, view.getLayoutParams());
	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		contentView.removeAllViewsInLayout();
		contentView.addView(view);
	}

	@Override
	public View findViewById(int id) {
		return contentView.findViewById(id);
	}

}

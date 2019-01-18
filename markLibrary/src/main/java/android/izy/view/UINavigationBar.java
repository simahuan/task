package android.izy.view;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

public class UINavigationBar extends FrameLayout {

	public UINavigationBar(Context context) {
		super(context);
	}

	public void setNavigationView(int layoutResID) {
		setNavigationView(View.inflate(getContext(), layoutResID, null));
	}

	public void setNavigationView(View view) {
		removeAllViewsInLayout();
		addView(view);
	}

	public void show() {
		setVisibility(View.VISIBLE);
	}

	public void hide() {
		setVisibility(View.GONE);
	}

}

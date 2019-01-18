package test;

import android.app.ActionBar;
import android.izy.R;
import android.izy.app.NetActivitySupport;
import android.izy.app.AlertDialogUtils;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class Activity2xSupport extends NetActivitySupport {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.ui_actionbar);
		actionBar.getCustomView().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {/*
				switch (v.getId()) {
				case R.id.btnBack:
					AlertDialogUtils.showToast(Activity2xSupport.this, "关闭");
					finish();
					break;
				default:
					break;
				}
			*/}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setNavigationBar(boolean hidden, boolean animated) {

	}

}

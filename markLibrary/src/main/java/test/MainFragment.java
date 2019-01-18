package test;


import android.izy.R;
import android.os.Bundle;
import android.support.v4.app.BaseFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class MainFragment extends BaseFragment {

	private MainActivity mainActivity;

	public MainFragment(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.main_fragment, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.findViewById(R.id.btnClose).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// mainActivity.popViewController();
				mainActivity.finishApplication();//.pushViewController(new PlaceholderFragment(mainActivity), true);
			}
		});
	}
}

package android.support.v4.app;

import android.izy.R;
import android.izy.app.ActivitySupport;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Fragment框架
 * 
 * @author yangyp
 * @version 1.0, 2014年8月15日 上午9:23:33
 */
public abstract class NavigationActivity extends ActivitySupport implements IFragmentActivity {

	@Override
	public void pushToRootFragment(Fragment viewController) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.container, viewController, viewController.getClass().getName());
		ft.commit();
	}

	@Override
	public void pushFragment(Fragment viewController, boolean animated) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (animated) {
			ft.setCustomAnimations(R.anim.slide_right_enter, R.anim.slide_right_exit, R.anim.slide_left_enter, R.anim.slide_left_exit);
		}
		ft.replace(R.id.container, viewController, viewController.getClass().getName());
		ft.addToBackStack(null);
		ft.commit();
	}

	@Override
	public void popFragment() {
		getSupportFragmentManager().popBackStack();
	}

	@Override
	public void popToFragment(Fragment viewController) {
		getSupportFragmentManager().popBackStack(viewController == null ? null : viewController.getClass().getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	@Override
	public void popToRootFragment() {
		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
}

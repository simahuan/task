package android.support.v4.app;

import android.support.v4.app.Fragment;

public interface IFragmentActivity {

	public void pushToRootFragment(Fragment viewFragment);

	/**
	 * （加载视图控制器）– 添加指定的视图控制器并予以显示，后接：是否动画显示
	 * 
	 * @param viewFragment
	 * @param animated
	 */
	public void pushFragment(Fragment viewFragment, boolean animated);

	/**
	 * （弹出当前试图控制器）– 弹出并向左显示前一个视图
	 */
	public void popFragment();

	/**
	 * （弹出到制定视图控制器）– 回到指定视图控制器， 也就是不只弹出一个
	 * 
	 * @param viewFragment
	 */
	public void popToFragment(Fragment viewFragment);

	/**
	 * （弹出到根视图控制器）– 比如说你有一个“Home”键，也许就会实施这个方法了。
	 */
	public void popToRootFragment();

}

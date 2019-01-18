package android.izy.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 顶部ViewPager 自动滚动以及无限循环滚动
 * 
 * @author yangyp
 * @version 1.0, 2014年8月21日 上午11:32:57
 */
public class BannerViewPager extends ViewPager implements Runnable {

	private static final int POST_DELAYED_TIME = 5 * 1000;
	private PagerAdapter pagerAdapter;
	private PagerAdapter pagerAdapterWrapper;
	private int duration = POST_DELAYED_TIME;

	// 是否可以滑动
	private boolean isCanScroll = true;

	public BannerViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// postDelayed(this, POST_DELAYED_TIME);
	}

	/**
	 * 是否可以滑动，默认可以左右滑动
	 * 
	 * @param isCanScroll
	 *            true可以滑动 | false不能滑动
	 */
	public void setScanScroll(boolean isCanScroll) {
		this.isCanScroll = isCanScroll;
	}

	public void startAutoCycle() {
		startAutoCycle(duration);
	}

	public void startAutoCycle(int duration) {
		this.duration = duration >= 1000 ? duration : 1000;
		removeCallbacks(this);
		postDelayed(this, duration);
	}

	public void stopAutoCycle() {
		removeCallbacks(this);
	}

	@Override
	public void scrollTo(int x, int y) {
		if (isCanScroll) {
			super.scrollTo(x, y);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action & 0xFF) {
		case MotionEvent.ACTION_DOWN:
			stopAutoCycle();
			break;
		case MotionEvent.ACTION_UP:
			startAutoCycle();
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 这里只有继承了LoopPagerAdapter类，notifyDataSetChanged通知才有效
	 */
	@Override
	public void setAdapter(PagerAdapter adapter) {
		if (adapter != null) {
			pagerAdapter = adapter;
			pagerAdapterWrapper = new PagerAdapterWrapper(adapter);
			super.setAdapter(pagerAdapterWrapper);
			if (adapter.getCount() != 0) {
				setCurrentItem(0, false);
			}
		}
	}

	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		super.setOnPageChangeListener(listener == null ? null : new PageChangeListenerWrapper(listener));
	}

	@Override
	public PagerAdapter getAdapter() {
		return pagerAdapter;
	}

	public PagerAdapter getPagerAdapterWrapper() {
		return pagerAdapterWrapper;
	}

	@Override
	public int getCurrentItem() {
		return super.getCurrentItem() - 1;
	}

	private int getSuperCurrentItem() {
		return super.getCurrentItem();
	}

	@Override
	public void setCurrentItem(int item) {
		setCurrentItem(item, true);
	}

	public void nextItem() {
		setCurrentItem(getCurrentItem() + 1);
	}

	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		super.setCurrentItem(item + 1, smoothScroll);
	}

	@Override
	// 自动滚动关键
	public void run() {
		if (pagerAdapter != null && pagerAdapter.getCount() > 1) {
			nextItem();
		}
		startAutoCycle(duration);
	}

	/**
	 * 获取实际的position
	 * 
	 * @param position
	 * @return
	 */
	private int getRealPosition(int position) {
		int count = pagerAdapter.getCount();
		return position == 0 ? count - 1 : (position == count + 1 ? 0 : position - 1);
	}

	// 对setAdapter的数据进行包装
	private class PagerAdapterWrapper extends PagerAdapter {
		private PagerAdapter adapter;

		public PagerAdapterWrapper(PagerAdapter adapter) {
			this.adapter = adapter;
			if (adapter instanceof LoopPagerAdapter) {
				((LoopPagerAdapter) adapter).setAdapterWrapper(this);
			}
		}

		// 关键之一:修改Count长度
		@Override
		public int getCount() {
			return adapter.getCount() > 1 ? adapter.getCount() + 2 : adapter.getCount();
		}

		// 这里是关键之二:修改索引(如果不考虑内容问题可以全部加载进数组然后操作更简单)
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			return adapter.instantiateItem(container, getRealPosition(position));
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			adapter.destroyItem(container, getRealPosition(position), object);
		}

		@Override
		public boolean isViewFromObject(View paramView, Object paramObject) {
			return adapter.isViewFromObject(paramView, paramObject);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	// 包装setOnPageChangeListener的数据
	private class PageChangeListenerWrapper implements OnPageChangeListener {

		private OnPageChangeListener listener;

		public PageChangeListenerWrapper(OnPageChangeListener listener) {
			this.listener = listener;
		}

		// 关键之三:
		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == SCROLL_STATE_IDLE) {
				int item = getRealPosition(getSuperCurrentItem());
				setCurrentItem(item, false);
			}

			listener.onPageScrollStateChanged(state);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}

		// 关键四:
		@Override
		public void onPageSelected(int position) {
			listener.onPageSelected(getRealPosition(position));
		}

	}

	static public abstract class LoopPagerAdapter extends PagerAdapter {

		private PagerAdapter adapterWrapper;

		public void setAdapterWrapper(PagerAdapter adapterWrapper) {
			this.adapterWrapper = adapterWrapper;
		}

		@Override
		public void notifyDataSetChanged() {
			if (adapterWrapper != null) {
				adapterWrapper.notifyDataSetChanged();
			}
		}

		@Override
		public boolean isViewFromObject(View paramView, Object paramObject) {
			return paramView == paramObject; // 官方提示这样写
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}
}

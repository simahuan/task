package android.izy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * ScrollView内嵌ViewPager导致ViewPager滑动困难问题
 * 
 * @author yangyp
 * @version 1.0, 2014年8月26日 上午9:00:05
 */
public class VScrollView extends ScrollView {

	private GestureDetector mGestureDetector;

	public VScrollView(Context context) {
		super(context);
		init(context);
	}

	public VScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public VScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mGestureDetector = new GestureDetector(context, new YScrollDetector());
		setFadingEdgeLength(0);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
	}

	private class YScrollDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return Math.abs(distanceY) >= Math.abs(distanceX);
		}
	}
}

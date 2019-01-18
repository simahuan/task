package android.izy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * listview的HeadView左右切换图片（仿新浪，网易，百度等切换图片）
 * <p>
 * ViewPager和listview共存的问题，二者都有自身的滑动事件，必然要产生冲突
 * </p>
 * 
 * @author yangyp
 * @version 1.0, 2014年8月20日 下午3:27:42
 */
public class ListHeadPagerView extends ListView {

	private GestureDetector mGestureDetector;

	public ListHeadPagerView(Context context) {
		super(context);
		init(context);
	}

	public ListHeadPagerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ListHeadPagerView(Context context, AttributeSet attrs, int defStyle) {
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

	class YScrollDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return Math.abs(distanceY) >= Math.abs(distanceX);
		}
	}
}

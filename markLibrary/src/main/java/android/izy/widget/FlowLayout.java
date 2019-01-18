package android.izy.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 流式布局
 * <p>
 * 组件按照加入的先后顺序按照设置的对齐方式从左向右排列，一行排满到下一行开始继续排列
 * </p>
 * 
 * @author yangyp
 * @version 1.0, 2014年12月16日 上午8:44:04
 */
public class FlowLayout extends ViewGroup {

	public FlowLayout(Context context) {
		super(context);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int mViewGroupWidth = getMeasuredWidth(); // 当前ViewGroup的总宽度

		int mPainterPosX = l; // 当前绘图光标横坐标位置
		int mPainterPosY = t; // 当前绘图光标纵坐标位置

		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {

			View childView = getChildAt(i);

			int width = childView.getMeasuredWidth();
			int height = childView.getMeasuredHeight();

			FlowLayout.LayoutParams lp = (FlowLayout.LayoutParams) childView.getLayoutParams();
			// ChildView占用的width = width+leftMargin+rightMargin
			// ChildView占用的height = height+topMargin+bottomMargin
			// 如果剩余的空间不够，则移到下一行开始位置
			if (mPainterPosX + width + lp.leftMargin + lp.rightMargin > mViewGroupWidth) {
				mPainterPosX = l;
				mPainterPosY += height + lp.topMargin + lp.bottomMargin;
			}

			// 执行ChildView的绘制
			childView.layout(mPainterPosX + lp.leftMargin, mPainterPosY + lp.topMargin, mPainterPosX + lp.leftMargin + width, mPainterPosY + lp.topMargin
					+ height);

			mPainterPosX += width + lp.leftMargin + lp.rightMargin;
		}
	}

	@Override
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new FlowLayout.LayoutParams(getContext(), attrs);
	}

	@Override
	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new FlowLayout.LayoutParams(p);
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof FlowLayout.LayoutParams;
	}

	public static class LayoutParams extends ViewGroup.MarginLayoutParams {

		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}

		public LayoutParams(ViewGroup.MarginLayoutParams source) {
			super(source);
		}

	}
}

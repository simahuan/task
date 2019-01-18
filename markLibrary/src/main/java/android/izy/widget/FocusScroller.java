package android.izy.widget;

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.Rect;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * This class encapsulates scrolling with the ability to overshoot the bounds of
 * a scrolling operation. This class is a drop-in replacement for
 * {@link android.widget.Scroller} in most cases.
 */
public class FocusScroller {

	private int mMode;
	private SplineFocusScroller mScroller;
	private Interpolator mInterpolator;

	private static final int DEFAULT_DURATION = 250;
	private static final int SCROLL_MODE = 0;

	// private static final int FLING_MODE = 1;

	/**
	 * Creates an OverScroller with a viscous fluid scroll interpolator and
	 * flywheel.
	 * 
	 * @param context
	 */
	public FocusScroller(Context context) {
		this(context, new DecelerateInterpolator());
	}

	/**
	 * Creates an OverScroller with flywheel enabled.
	 * 
	 * @param context
	 *            The context of this application.
	 * @param interpolator
	 *            The scroll interpolator. If null, a default (viscous)
	 *            interpolator will be used.
	 */
	public FocusScroller(Context context, Interpolator interpolator) {
		setInterpolator(interpolator);
		mScroller = new SplineFocusScroller(context);
	}

	public void setInterpolator(Interpolator interpolator) {
		if (interpolator == null) {
			mInterpolator = new DecelerateInterpolator();
		} else {
			mInterpolator = interpolator;
		}
	}

	/**
	 *
	 * Returns whether the scroller has finished scrolling.
	 *
	 * @return True if the scroller has finished scrolling, false otherwise.
	 */
	public final boolean isFinished() {
		return mScroller.mFinished;
	}

	/**
	 * Force the finished field to a particular value. Contrary to
	 * {@link #abortAnimation()}, forcing the animation to finished does NOT
	 * cause the scroller to move to the final x and y position.
	 *
	 * @param finished
	 *            The new finished value.
	 */
	public final void forceFinished(boolean finished) {
		mScroller.mFinished = finished;
	}

	/**
	 * Returns the current Rect offset in the scroll.
	 *
	 * @return The new Rect offset as an absolute distance from the origin.
	 */
	public final Rect getCurrRect() {
		return mScroller.mCurrentPosition;
	}

	/**
	 * Returns the start Rect offset in the scroll.
	 *
	 * @return The start Rect offset as an absolute distance from the origin.
	 */
	public final Rect getStartRect() {
		return mScroller.mStart;
	}

	/**
	 * Returns where the scroll will end. Valid only for "fling" scrolls.
	 *
	 * @return The final Rect offset as an absolute distance from the origin.
	 */
	public final Rect getFinalRect() {
		return mScroller.mFinal;
	}

	/**
	 * Call this when you want to know the new location. If it returns true, the
	 * animation is not yet finished.
	 */
	public boolean computeScrollOffset() {
		if (isFinished()) {
			return false;
		}

		switch (mMode) {
		case SCROLL_MODE:
			long time = AnimationUtils.currentAnimationTimeMillis();
			// Any scroller can be used for time, since they were started
			// together in scroll mode. We use X here.
			final long elapsedTime = time - mScroller.mStartTime;

			final int duration = mScroller.mDuration;
			if (elapsedTime < duration) {
				final float q = mInterpolator.getInterpolation(elapsedTime / (float) duration);
				mScroller.updateScroll(q);
			} else {
				abortAnimation();
			}
			break;
		}

		return true;
	}

	public void startScroll(Rect start, Rect end) {
		startScroll(start, end, DEFAULT_DURATION);
	}

	public void startScroll(Rect start, Rect end, int duration) {
		mMode = SCROLL_MODE;
		mScroller.startScroll(start, end, duration);
	}

	/**
	 * Stops the animation. Contrary to {@link #forceFinished(boolean)},
	 * aborting the animating causes the scroller to move to the final x and y
	 * positions.
	 *
	 * @see #forceFinished(boolean)
	 */
	public void abortAnimation() {
		mScroller.finish();
	}

	static class SplineFocusScroller {
		// Initial position
		private Rect mStart;
		// Current position
		private Rect mCurrentPosition;
		// Final position
		private Rect mFinal;

		// Animation starting time, in system milliseconds
		private long mStartTime;

		// Animation duration, in milliseconds
		private int mDuration;

		// Whether the animation is currently in progress
		private boolean mFinished;

		SplineFocusScroller(Context context) {
			mFinished = true;
			mStart = new Rect();
			mFinal = new Rect();
			mCurrentPosition = new Rect();
		}

		public void startScroll(Rect start, Rect distance, int duration) {
			mFinished = false;

			mStart.set(start);
			mFinal.set(distance);

			mStartTime = AnimationUtils.currentAnimationTimeMillis();
			mDuration = duration;
		}

		public void updateScroll(float q) {
			mCurrentPosition.left = calculateCurrentPos(q, mStart.left, mFinal.left);
			mCurrentPosition.top = calculateCurrentPos(q, mStart.top, mFinal.top);
			mCurrentPosition.right = calculateCurrentPos(q, mStart.right, mFinal.right);
			mCurrentPosition.bottom = calculateCurrentPos(q, mStart.bottom, mFinal.bottom);
		}

		public void finish() {
			mCurrentPosition.set(mFinal);
			mFinished = true;
		}

		static int calculateCurrentPos(float q, int _start, int _final) {
			return _start + Math.round(q * (_final - _start));
		}
	}

}

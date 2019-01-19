package com.zt.task.system.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class DialogService extends Service {
    private final String TAG = DialogService.class.getSimpleName() + "[" + getClass().getSimpleName() + "]";

    /**
     * The {@link Intent} that must be declared as handled by the service.
     */
    public static final String SERVICE_INTERFACE = "com.pisen.ott.service.window.DialogService";

    private WindowManager mWindow;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private FrameLayout mWindowHost;
    private boolean mFullscreen;
    private boolean mScreenBright = true;
    private boolean mFinished;

    private boolean mDebug = true;

    // begin public api
    /**
     * Retrieves the current {@link android.view.WindowManager} for the dream.
     * Behaves similarly to {@link android.app.Activity#getWindowManager()}.
     *
     * @return The current window manager, or null if the dream is not started.
     */
    public WindowManager getWindowManager() {
        return mWindow;
    }

    /**
     * Inflates a layout resource and set it to be the content view for this
     * Dream. Behaves similarly to
     * {@link android.app.Activity#setContentView(int)}.
     *
     * <p>
     * Note: Requires a window, do not call before {@link #onAttachedToWindow()}
     * </p>
     *
     * @param layoutResID
     *            Resource ID to be inflated.
     *
     * @see #setContentView(android.view.View)
     * @see #setContentView(android.view.View,
     *      android.view.ViewGroup.LayoutParams)
     */
    public void setContentView(int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }

    /**
     * Sets a view to be the content view for this Dream. Behaves similarly to
     * {@link android.app.Activity#setContentView(android.view.View)} in an
     * activity, including using {@link ViewGroup.LayoutParams#MATCH_PARENT} as
     * the layout height and width of the view.
     *
     * <p>
     * Note: This requires a window, so you should usually call it during
     * {@link #onAttachedToWindow()} and never earlier (you
     * <strong>cannot</strong> call it during {@link #onCreate}).
     * </p>
     *
     * @see #setContentView(int)
     * @see #setContentView(android.view.View,
     *      android.view.ViewGroup.LayoutParams)
     */
    public void setContentView(View view) {
        setContentView(view, null);
    }

    /**
     * Sets a view to be the content view for this Dream. Behaves similarly to
     * {@link android.app.Activity#setContentView(android.view.View, android.view.ViewGroup.LayoutParams)}
     * in an activity.
     *
     * <p>
     * Note: This requires a window, so you should usually call it during
     * {@link #onAttachedToWindow()} and never earlier (you
     * <strong>cannot</strong> call it during {@link #onCreate}).
     * </p>
     *
     * @param view
     *            The desired content to display.
     * @param params
     *            Layout parameters for the view.
     *
     * @see #setContentView(android.view.View)
     * @see #setContentView(int)
     */
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mWindowHost.removeAllViewsInLayout();
        mWindowHost.addView(view, params);
        mWindowHost.setVisibility(view.getVisibility());
        // getWindowManager().updateViewLayout(mWindowHost,
        // mWindowLayoutParams);
    }

    /**
     * Adds a view to the Dream's window, leaving other content views in place.
     *
     * <p>
     * Note: Requires a window, do not call before {@link #onAttachedToWindow()}
     * </p>
     *
     * @param view
     *            The desired content to display.
     * @param params
     *            Layout parameters for the view.
     */
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        mWindowHost.addView(view, params);
        mWindowHost.setVisibility(View.VISIBLE);
    }

    /**
     * Finds a view that was identified by the id attribute from the XML that
     * was processed in {@link #onCreate}.
     *
     * <p>
     * Note: Requires a window, do not call before {@link #onAttachedToWindow()}
     * </p>
     *
     * @return The view if found or null otherwise.
     */
    public View findViewById(int id) {
        return mWindowHost.findViewById(id);
    }

    /**
     * Controls {@link android.view.WindowManager.LayoutParams#FLAG_FULLSCREEN}
     * on the dream's window.
     *
     * @param fullscreen
     *            If true, the fullscreen flag will be set; else it will be
     *            cleared.
     */
    public void setFullscreen(boolean fullscreen) {
        if (mFullscreen != fullscreen) {
            mFullscreen = fullscreen;
            int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            applyWindowFlags(mFullscreen ? flag : 0, flag);
        }
    }

    /**
     * Returns whether or not this dream is in fullscreen mode. Defaults to
     * false.
     *
     * @see #setFullscreen(boolean)
     */
    public boolean isFullscreen() {
        return mFullscreen;
    }

    /**
     * Marks this dream as keeping the screen bright while dreaming.
     *
     * @param screenBright
     *            True to keep the screen bright while dreaming.
     */
    public void setScreenBright(boolean screenBright) {
        if (mScreenBright != screenBright) {
            mScreenBright = screenBright;
            int flag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            applyWindowFlags(mScreenBright ? flag : 0, flag);
        }
    }

    /**
     * Called when this Dream is constructed.
     */
    @Override
    public void onCreate() {
        if (mDebug)
            Log.v(TAG, "onCreate()");
        super.onCreate();
        attach();
    }

    @Override
    public final IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Stops the dream and detaches from the window.
     * <p>
     * When the dream ends, the system will be allowed to go to sleep fully
     * unless there is a reason for it to be awake such as recent user activity
     * or wake locks being held.
     * </p>
     */
    public final void finish() {
        if (mDebug)
            Log.v(TAG, "finish(): mFinished=" + mFinished);

        if (!mFinished) {
            mFinished = true;
            stopSelf();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onDestroy() {
        if (mDebug)
            Log.v(TAG, "onDestroy()");
        // hook for subclasses

        // Just in case destroy came in before detach, let's take care of that
        // now
        detach();

        super.onDestroy();
    }

    // end public api

    /**
     * Called by DreamController.stopDream() when the Dream is about to be
     * unbound and destroyed.
     *
     * Must run on mHandler.
     */
    private final void detach() {
        if (mWindow != null) {
            // force our window to be removed synchronously
            if (mDebug)
                Log.v(TAG, "detach(): Removing window from window manager");
            mWindow.removeViewImmediate(mWindowHost);
            mWindow = null;
        }
    }

    /**
     * Called when the Dream is ready to be shown.
     *
     * Must run on mHandler.
     *
     * @param windowToken
     *            A window token that will allow a window to be created in the
     *            correct layer.
     */
    private final void attach() {
        if (mWindowHost == null) {
            mWindowHost = new WindowViewHost(this);
            mWindowHost.setVisibility(View.GONE);
            mWindow = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            // lp.windowAnimations =
            // com.android.internal.R.style.Animation_Dream;
            lp.flags |= (WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN //
                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR //
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //
                    | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON //
                    | (mFullscreen ? WindowManager.LayoutParams.FLAG_FULLSCREEN : 0) //
                    | (mScreenBright ? WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON : 0));

            lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
            lp.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;
            lp.alpha = 1f;

            lp.setTitle("Keyguard");
            mWindowLayoutParams = lp;
            getWindowManager().addView(mWindowHost, lp);
        }
    }

    private void applyWindowFlags(int flags, int mask) {
        if (mWindow != null) {
            mWindowLayoutParams.flags = applyFlags(mWindowLayoutParams.flags, flags, mask);
            mWindow.updateViewLayout(mWindowHost, mWindowLayoutParams);
        }
    }

    private int applyFlags(int oldFlags, int flags, int mask) {
        return (oldFlags & ~mask) | (flags & mask);
    }

    /***
     * Helper class to host the keyguard view.
     */
    static private class WindowViewHost extends FrameLayout {

        private WindowViewHost(Context context) {
            super(context);
        }

    }
}

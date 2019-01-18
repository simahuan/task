package android.izy.app.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

/**
 * Encapsulate dialog behavior with blur effect for app using
 * {@link android.app.DialogFragment}.
 * <p/>
 * All the screen behind the dialog will be blurred except the action bar.
 */
public class BlurDialog extends Dialog {

	/**
	 * Log cat
	 */
	private static final String TAG = BlurDialog.class.getSimpleName();

	/**
	 * Bundle key used to start the blur dialog with a given scale factor
	 * (float).
	 */
	public static final String BUNDLE_KEY_DOWN_SCALE_FACTOR = "bundle_key_down_scale_factor";

	/**
	 * Bundle key used to start the blur dialog with a given blur radius (int).
	 */
	public static final String BUNDLE_KEY_BLUR_RADIUS = "bundle_key_blur_radius";

	/**
	 * Engine used to blur.
	 */
	private BlurDialogEngine mBlurEngine;

	public BlurDialog(Context context) {
		super(context);
		initViews(context);
	}

	public BlurDialog(Context context, int theme) {
		super(context, theme);
		initViews(context);
	}

	private void initViews(Context context) {
		if (context instanceof Activity) {
			setOwnerActivity((Activity) context);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBlurEngine = new BlurDialogEngine(getOwnerActivity());
	}

	@Override
	protected void onStart() {
		super.onStart();
		mBlurEngine.onResume(false);
	}

	@Override
	public void dismiss() {
		super.dismiss();
		mBlurEngine.onDismiss();
		mBlurEngine.onDestroy();
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		setDismissMessage(null);
	}

	/**
	 * Set the blur radius used by the
	 * {@link fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine}
	 *
	 * @param radius
	 *            down scaled factor used to reduce the size of the source
	 *            image. Range : [1,infinity)
	 */
	public void setBlurRadius(int radius) {
		if (radius > 0) {
			mBlurEngine.setBlurRadius(radius);
		}
	}

	/**
	 * Set the down scale factor used by the
	 * {@link fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine}
	 *
	 * @param factor
	 *            down scaled factor used to reduce the size of the source
	 *            image. Range : ]0,infinity)
	 */
	public void setDownScaleFactor(float factor) {
		if (factor > 0) {
			mBlurEngine.setDownScaleFactor(factor);
		}
	}

}

package android.izy.app;

import android.izy.ApplicationSupport;
import android.content.DialogInterface;

/**
 * @author yangyp
 */
public interface IActivity {

	public ApplicationSupport getApplicationContext();

	public void showLoadingProgressDialog();

	public void showProgressDialog(CharSequence message);

	public void showProgressDialog(CharSequence message, DialogInterface.OnCancelListener listener);

	public void dismissProgressDialog();

}

package android.izy.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * 消息工具类
 * 
 * @author yangyp
 * @version 1.0, 2014年10月18日 上午9:49:16
 */
public abstract class AlertDialogUtils {

	public static void showToast(Context context, int resId) {
		showToast(context, context.getText(resId));
	}

	public static void showToast(Context context, CharSequence message) {
		showToast(context, message, Toast.LENGTH_SHORT);
	}

	public static void showToastLong(Context context, int resId) {
		showToastLong(context, context.getText(resId));
	}

	public static void showToastLong(Context context, CharSequence message) {
		showToast(context, message, Toast.LENGTH_LONG);
	}

	public static void showToast(Context context, CharSequence message, int duration) {
		Toast toast = Toast.makeText(context, message, duration);
		// toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/**
	 * 加载窗口
	 * 
	 * @param message
	 * @param listener
	 * @return
	 */
	public static Dialog showLoadingDialog(Context context, CharSequence message, final DialogInterface.OnCancelListener listener) {
		final ProgressDialog dialog = new ProgressDialog(context);
		dialog.setMessage(message);
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.setOnCancelListener(listener);
		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
					listener.onCancel(dialog);
				}
				return false;
			}
		});
		return dialog;
	}

	/**
	 * 弹出消息提示框
	 * 
	 * @param message
	 * @param title
	 * @return
	 */
	public static Dialog showMessageDialog(Context context, CharSequence message, CharSequence title, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setIconAttribute(android.R.attr.alertDialogIcon);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.ok, listener);
		return builder.show();
	}

	/**
	 * 弹出确认消息窗口
	 * 
	 * @param context
	 * @param message
	 * @param title
	 * @param listener
	 * @return
	 */
	public static Dialog showConfirmDialog(Context context, CharSequence message, CharSequence title, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setIconAttribute(android.R.attr.alertDialogIcon);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.ok, listener);
		builder.setNegativeButton(android.R.string.cancel, null);
		return builder.show();
	}

	/**
	 * 弹出列表选择窗口
	 * 
	 * @param context
	 * @param itemsId
	 * @param title
	 * @param listener
	 * @return
	 */
	public static Dialog showListDialog(Context context, int itemsId, CharSequence title, DialogInterface.OnClickListener listener) {
		String[] items = context.getResources().getStringArray(itemsId);
		return showListDialog(context, items, title, listener);
	}

	/**
	 * 弹出列表选择窗口
	 * 
	 * @param context
	 * @param items
	 * @param title
	 * @param listener
	 * @return
	 */
	public static Dialog showListDialog(Context context, CharSequence[] items, CharSequence title, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setItems(items, listener);
		return builder.show();
	}

	/**
	 * 弹出单选窗口
	 * 
	 * @param context
	 * @param items
	 * @param checkedItem
	 * @param title
	 * @param listener
	 * @return
	 */
	public static Dialog showSingleChoiceDialog(Context context, CharSequence[] items, int checkedItem, CharSequence title,
			DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setIconAttribute(android.R.attr.alertDialogIcon);
		builder.setTitle(title);
		builder.setSingleChoiceItems(items, checkedItem, null);
		builder.setPositiveButton(android.R.string.ok, listener);
		builder.setNegativeButton(android.R.string.cancel, null);
		return builder.show();
	}

	/**
	 * 弹出多选窗口
	 * 
	 * @param context
	 * @param items
	 * @param checkedItems
	 * @param title
	 * @param listener
	 * @return
	 */
	public static Dialog showMultipleChoiceDialog(Context context, CharSequence[] items, boolean[] checkedItems, CharSequence title,
			DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setIcon(android.R.attr.alertDialogIcon);
		builder.setTitle(title);
		builder.setMultiChoiceItems(items, checkedItems, null);
		builder.setPositiveButton(android.R.string.ok, listener);
		builder.setNegativeButton(android.R.string.cancel, null);
		return builder.show();
	}

	private static final int MAX_PROGRESS = 100;

	/**
	 * 显示带进度条窗口
	 * 
	 * @param context
	 * @param title
	 * @param listener
	 * @return
	 */
	public static Dialog showProgressDialog(Context context, CharSequence title, DialogInterface.OnClickListener listener) {
		ProgressDialog mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		mProgressDialog.setTitle(title);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(MAX_PROGRESS);
		mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getText(android.R.string.cancel), listener);
		return mProgressDialog;
	}
}

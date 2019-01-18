package android.izy.os;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * 消息工具类
 * 
 * @author yangyp
 * 
 */
public abstract class MessageDialog {

	public static void showToast(Context context, int resId) {
		showToast(context, context.getText(resId));
	}

	public static void showToast(Context context, CharSequence message) {
		showToast(context, message, Toast.LENGTH_SHORT);
	}

	public static void showToast(Context context, CharSequence message, int duration) {
		Toast toast = Toast.makeText(context, message, duration);
		// toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static void showToastLong(Context context, int resId) {
		showToastLong(context, context.getText(resId));
	}

	public static void showToastLong(Context context, CharSequence message) {
		showToast(context, message, Toast.LENGTH_LONG);
	}

	/**
	 * 进行条窗口
	 * 
	 * @param message
	 * @param listener
	 * @return
	 */
	public static ProgressDialog showProgressDialog(Context context, CharSequence message, final DialogInterface.OnCancelListener listener) {
		final ProgressDialog dialog = new ProgressDialog(context);
		dialog.setMessage(message);
		dialog.setIndeterminate(true);
		// 点击ProgressDialog以外的区域不ProgressDialog dismiss掉
		dialog.setCancelable(false);
		dialog.setOnCancelListener(listener);
		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
					if (listener != null) {
						listener.onCancel(dialog);
					}
				}
				return false;
			}
		});
		return dialog;
	}

	public static Dialog showMessageDialog(Context context, CharSequence message) {
		return showMessageDialog(context, message, "系统提示", null);
	}

	public static Dialog showMessageDialog(Context context, CharSequence message, CharSequence title) {
		return showMessageDialog(context, message, title, null);
	}

	public static Dialog showMessageDialog(Context context, CharSequence message, DialogInterface.OnClickListener listener) {
		return showMessageDialog(context, message, "系统提示", listener);
	}

	/**
	 * 消息提示框
	 * 
	 * @param message
	 * @param title
	 * @return
	 */
	public static Dialog showMessageDialog(Context context, CharSequence message, CharSequence title, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("确定", listener);
		return builder.show();
	}

	/**
	 * 消息确认框
	 * 
	 * @param message
	 * @param listener
	 * @return
	 */
	public static Dialog showConfirmDialog(Context context, CharSequence message, DialogInterface.OnClickListener listener) {
		return showConfirmDialog(context, message, "系统提示", listener);
	}

	/**
	 * 消息确认框
	 * 
	 * @param message
	 * @param title
	 * @param listener
	 * @return
	 */

	public static Dialog showConfirmDialog(Context context, CharSequence message, CharSequence title, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("确定", listener);
		builder.setNegativeButton("取消", null);
		return builder.show();
	}

	/**
	 * 单选框
	 * 
	 * @param context
	 * @param items
	 * @param checkedItem
	 * @param listener
	 * @return
	 */
	public static Dialog showSingleChoiceDialog(Context context, CharSequence[] items, int checkedItem, OnDialogClickListener listener) {
		if (listener != null) {
			listener.setIndex(checkedItem);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("请选择");
		builder.setSingleChoiceItems(items, checkedItem/* 记录那个RadioButton被选中 */, listener);
		builder.setPositiveButton("确定", listener);
		builder.setNegativeButton("取消", null);
		return builder.show();
	}

	public static Dialog showSingleChoiceDialog(Context context, int itemsId, int checkedItem, OnDialogClickListener listener) {
		CharSequence[] items = context.getResources().getTextArray(itemsId);
		return showSingleChoiceDialog(context, items, checkedItem, listener);
	}

	public static Dialog showSingleChoiceDialog(Context context, List<? extends Object> items, int checkedItem, OnDialogClickListener listener) {
		CharSequence[] itemsChar = new CharSequence[items.size()];
		int itemIndex = 0;
		for (Object it : items) {
			itemsChar[itemIndex++] = it.toString();
		}
		return showSingleChoiceDialog(context, itemsChar, checkedItem, listener);
	}

	/**
	 * 窗口事件处理类
	 * 
	 * @author yangyp
	 * 
	 */
	public static abstract class OnDialogClickListener implements DialogInterface.OnClickListener {

		private int index; // 表示选项的索引

		public void setIndex(int index) {
			this.index = index;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// which表示单击的按钮索引，所有的选项索引都是大于0，按钮索引都是小于0的。
			if (which >= 0) {
				// 如果单击的是列表项，将当前列表项的索引保存在index中。
				// 如果想单击列表项后关闭对话框，可在此处调用dialog.cancel()
				// 或是用dialog.dismiss()方法。
				index = which;
			} else {
				// 用户单击的是【确定】按钮
				if (which == DialogInterface.BUTTON_POSITIVE) {
					onDialogClick(dialog, index);
				}
				// 用户单击的是【取消】按钮
				else if (which == DialogInterface.BUTTON_NEGATIVE) {

				}
			}
		}

		public abstract void onDialogClick(DialogInterface dialog, int index);
	}

	/**
	 * 多选框
	 * 
	 * @param context
	 * @param items
	 * @param checkedItem
	 * @param listener
	 * @return
	 */
	public static Dialog setMultiChoiceDialog(Context context, CharSequence[] items, int checkedItem, OnMultiChoiceClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("请选择");
		builder.setMultiChoiceItems(items, null, listener);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		builder.setNegativeButton("取消", null);
		return builder.show();
	}

	/**
	 * 发送消息到通知栏
	 * 
	 * @param context
	 * @param notifyId
	 *            消息ID
	 * @param iconId
	 *            消息图标
	 * @param title
	 *            消息标题
	 * @param content
	 *            消息内容
	 * @param contentIntent
	 *            点击消息动作
	 */
	public static void showNotification(Context context, int notifyId, int iconId, CharSequence title, CharSequence content, PendingIntent contentIntent) {
		NotificationManager mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		notification.icon = iconId;
		notification.tickerText = title;
		notification.when = System.currentTimeMillis();
		notification.setLatestEventInfo(context, content, title, contentIntent);
		mNM.notify(notifyId, notification);
	}

	/*
	 * public static Notification getNotification(Context context, int iconId,
	 * CharSequence title, CharSequence content, PendingIntent contentIntent) {
	 * Notification.Builder notification = new Notification.Builder(context);
	 * notification.setContentIntent(contentIntent);
	 * notification.setSmallIcon(iconId);
	 * notification.setWhen(System.currentTimeMillis());
	 * notification.setAutoCancel(false); notification.setContentTitle(title);
	 * notification.setContentText(content); return notification.build(); }
	 */
}

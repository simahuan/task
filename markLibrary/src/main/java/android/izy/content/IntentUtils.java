package android.izy.content;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;

public abstract class IntentUtils {

	public static Intent newIntent(Context context, Class<? extends Context> clazz) {
		Intent intent = new Intent(context, clazz);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}

	public static Intent newIntent(Context context, Class<? extends Context> clazz, int flags) {
		Intent intent = newIntent(context, clazz);
		intent.addFlags(flags);
		return intent;
	}

	/**
	 * 创建一个app启动Intent
	 * 
	 * @param packageName
	 * @param clsName
	 * @return
	 */
	public static Intent newIntent(String packageName, String activityName) {
		ComponentName componentName = new ComponentName(packageName, activityName);
		Intent intent = new Intent(Intent.ACTION_MAIN);
		// intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(componentName);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		return intent;
	}

	public static Intent getLauncherMainIntent(Context context, String packageName) {
		final List<ResolveInfo> matches = findActivitiesForPackage(context, packageName);
		if (matches != null) {
			for (ResolveInfo info : matches) {
				final String _packageName = info.activityInfo.applicationInfo.packageName;
				if (packageName.equals(_packageName)) {
					return newIntent(packageName, info.activityInfo.name);
				}
			}
		}
		return null;
	}

	private static List<ResolveInfo> findActivitiesForPackage(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mainIntent.setPackage(packageName);
		return packageManager.queryIntentActivities(mainIntent, 0);
	}

	/**
	 * 根据路径安装APK
	 * 
	 * @param context
	 * @param apkPath
	 */
	public static void installApk(Context context, String apkPath) {
		File apkfile = new File(apkPath);
		if (apkfile.exists()) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.parse("file://" + apkfile), "application/vnd.android.package-archive");
			context.startActivity(i);
		}
	}

	/**
	 * 根据路径安装APK
	 * 
	 * @param context
	 */
	public static void uninstallApk(Context context) {
		Uri packageUri = Uri.parse("package:" + context.getPackageName());
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
		context.startActivity(uninstallIntent);
	}

	/**
	 * 调用拨号面板
	 * <p>
	 * uses-permission android:name="android.permission.CALL_PHONE"
	 * 
	 * @param context
	 * @param telNumber
	 *            电话号码
	 */
	public static void startTel(Context context, String telNumber) {
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telNumber));
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * 呼叫指定的电话号码
	 * <p>
	 * uses-permission android:name="android.permission.CALL_PHONE"
	 * 
	 * @param context
	 * @param telNumber
	 *            电话号码
	 */
	public static void startTelCall(Context context, String telNumber) {
		Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + telNumber));
		context.startActivity(intent);
	}

	/**
	 * 调用发送短信的程序
	 * 
	 * @param context
	 * @param PhoneNumber
	 * @param content
	 */
	public static void startSms(Context context, String content) {
		Intent it = new Intent(Intent.ACTION_VIEW);
		it.putExtra("sms_body", content);
		it.setType("vnd.android-dir/mms-sms");
		context.startActivity(it);
	}

	/**
	 * 发送短信息
	 * 
	 * @param phoneNumber
	 * @param content
	 */
	public static void startSmsSend(Context context, String phoneNumber, String content) {
		Uri uri = Uri.parse("smsto:" + phoneNumber);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra("sms_body", content);
		context.startActivity(it);
	}

	/**
	 * 播放视频
	 * 
	 * @param context
	 * @param path
	 */
	public static void startVideo(Context context, String path) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri uri = Uri.parse(path/* "file:///sdcard/media.mp4" */);
		intent.setDataAndType(uri, "video/*");
		context.startActivity(intent);
	}

	// Email
	public static void startEmail(Context context, String path) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		String[] tos = { "android1@163.com" };
		String[] ccs = { "you@yahoo.com" };
		intent.putExtra(Intent.EXTRA_EMAIL, tos);
		intent.putExtra(Intent.EXTRA_CC, ccs);
		intent.putExtra(Intent.EXTRA_TEXT, "The email body text");
		intent.putExtra(Intent.EXTRA_SUBJECT, "The email subject text");
		intent.setType("message/rfc822");
		context.startActivity(Intent.createChooser(intent, "Choose Email Client"));
	}

	/*--------13 Intent.ACTION_GET_CONTENT----------*/

	// 选择图片 requestCode 返回的标识
	public static void startImageForResult(Context context, String contentType, int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
		intent.setType(contentType); // 查看类型 String IMAGE_UNSPECIFIED="image/*";
		Intent wrapperIntent = Intent.createChooser(intent, null);
		((Activity) context).startActivityForResult(wrapperIntent, requestCode);
	}

	// 添加音频
	public static void startVideoForResult(Context context, String contentType, int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType(contentType); // String VIDEO_UNSPECIFIED = "video/*";
		Intent wrapperIntent = Intent.createChooser(intent, null);
		((Activity) context).startActivityForResult(wrapperIntent, requestCode);
	}

	// 拍摄视频
	/*
	 * public static void startVoidCaptureForResult(Context context, String
	 * contentType, int requestCode) { int durationLimit =
	 * getVideoCaptureDurationLimit(); //
	 * SystemProperties.getInt("ro.media.enc.lprof.duration",60); Intent intent
	 * = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
	 * intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
	 * intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, sizeLimit);
	 * intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, durationLimit);
	 * startActivityForResult(intent, REQUEST_CODE_TAKE_VIDEO); }
	 */

	// 视频
	public static void startVideoUnspecifiedForResult(Context context, String contentType, int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType(contentType); // String VIDEO_UNSPECIFIED = "video/*";
		Intent wrapperIntent = Intent.createChooser(intent, null);
		((Activity) context).startActivityForResult(wrapperIntent, requestCode);
	}

	// 录音
	/*
	 * public static void startAudioForResult(Context context, String
	 * contentType, int requestCode) { Intent intent = new
	 * Intent(Intent.ACTION_GET_CONTENT); intent.setType(ContentType.AUDIO_AMR);
	 * // String AUDIO_AMR="audio/amr";
	 * intent.setClassName("com.android.soundrecorder",
	 * "com.android.soundrecorder.SoundRecorder"); ((Activity)
	 * context).startActivityForResult(intent, requestCode); }
	 */

	// 拍照 REQUEST_CODE_TAKE_PICTURE 为返回的标识
	/*
	 * public static void startVideoForResult(Context context, String
	 * contentType, int requestCode) { Intent intent = new
	 * Intent(MediaStore.ACTION_IMAGE_CAPTURE); //
	 * "android.media.action.IMAGE_CAPTURE";
	 * intent.putExtra(MediaStore.EXTRA_OUTPUT, Mms.ScrapSpace.CONTENT_URI); //
	 * output,Uri.parse("content://mms/scrapSpace"); ((Activity)
	 * context).startActivityForResult(intent, requestCode); }
	 */

	/**
	 * 用于显示用户的数据
	 * <p>
	 * 比如 tel:13400010001打开拨号程序，http://www.g.cn则会打开浏览器等。
	 * 
	 * @param context
	 * @param uri
	 */
	public static void startActionView(Context context, final String uri) {
		if (uri != null && uri.length() > 0 && (uri.startsWith("http:") || uri.startsWith("https:") || uri.startsWith("tel:") || uri.startsWith("geo:"))) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			context.startActivity(intent);
		}
	}

	/**
	 * 调用相机拍照，拍照完成后触发onActivityResult事件
	 * 
	 * @param activity
	 * @param requestCode
	 *            返回响应
	 * @param imagePath
	 *            图片保存路径
	 */
	public static void startCamera(Activity activity, int requestCode, String imagePath) {
		Uri imageUri = Uri.fromFile(new File(imagePath));
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * 调用相机截图，完成后触发onActivityResult事件
	 * 
	 * @param activity
	 * @param requestCode
	 * @param imagePath
	 */
	public static void startCameraCrop(Activity activity, int requestCode, String imagePath) {
		Uri imageUri = Uri.fromFile(new File(imagePath));
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(imageUri, "image/*");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 4);
		intent.putExtra("aspectY", 3);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 1024);
		intent.putExtra("outputY", 768);
		intent.putExtra("return-data", true);
		activity.startActivityForResult(intent, requestCode);
	}
}

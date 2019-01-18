package android.izy.os;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.Log;

/**
 * 图片工具类
 * 
 * @author yangyp
 * @version 1.0, 2014-7-9 上午10:15:07
 */
public abstract class ImageUtils {

	static final String TAG = ImageUtils.class.getSimpleName();

	public static Bitmap resizeBitmap(String filepath, int inSampleSize) {
		if (filepath == null || !new File(filepath).exists()) {
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filepath, options);
		options.inSampleSize = inSampleSize;
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
		return resizeBitmap(bitmap, Math.round(options.outWidth / (float) inSampleSize), Math.round(options.outHeight / (float) inSampleSize));
	}

	public static Bitmap resizeBitmap(String filepath, int width, int height) {
		if (filepath == null || !new File(filepath).exists()) {
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filepath, options);
		options.inSampleSize = Math.round(Math.max(options.outHeight / (float) width, options.outWidth / (float) height));
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
		return resizeBitmap(bitmap, width, height);
	}

	/**
	 * 根据指定的高度与宽度进行计算，取最大的缩放比较
	 * 
	 */
	public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 计算缩放比例
		float scaleWidth = newWidth / (float) width;
		float scaleHeight = newHeight / (float) height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		bitmap.recycle();
		return resizedBitmap;
	}

	/**
	 * 图片旋转
	 * 
	 * @param bitmap
	 *            原图
	 * @param degrees
	 *            旋转度数
	 * @return 返回旋转后的图
	 */
	public static Bitmap rotate(Bitmap bitmap, float degrees) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	/**
	 * 获取图片的旋转角度
	 * 
	 * @param filepath
	 *            文件路径
	 * @return 返回旋转角度
	 */
	public static int getOrientationRotate(String filepath) {
		int rotate = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(filepath);
			int result = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
			switch (result) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			default:
				rotate = 0;
				break;
			}
		} catch (IOException e) {
			Log.e(TAG, "getOrientation: ", e);
		}
		return rotate;
	}

	public static Drawable getFileImage(String filepath) {
		Drawable result = null;
		File file = new File(filepath);
		if (file.exists()) {
			result = Drawable.createFromPath(filepath);
			result.setBounds(0, 0, result.getIntrinsicWidth(), result.getIntrinsicHeight());
		}
		return result;
	}

	public static Drawable getURLImage(String http) {
		Drawable result = null;
		try {
			URL url = new URL(http);
			result = Drawable.createFromStream(url.openStream(), "url.png");
			result.setBounds(0, 0, result.getIntrinsicWidth(), result.getIntrinsicHeight());
		} catch (IOException e) {
		}
		return result;
	}

	public static boolean saveFile(Bitmap bitmap, String path) {
		return saveFile(bitmap, path, CompressFormat.JPEG, 100);
	}

	public static boolean saveFile(Bitmap bitmap, String path, int quality) {
		return saveFile(bitmap, path, CompressFormat.JPEG, quality);
	}

	public static boolean saveFile(Bitmap bitmap, String path, CompressFormat format, int quality) {
		long currTime = System.currentTimeMillis();
		try {
			OutputStream out = new FileOutputStream(path);
			bitmap.compress(format, quality, out);
			out.flush();
			Log.i(TAG, "saveFile: " + path + "[" + (System.currentTimeMillis() - currTime) / 1000f + "/s]");
			return true;
		} catch (IOException e) {
		}
		return false;
	}
}

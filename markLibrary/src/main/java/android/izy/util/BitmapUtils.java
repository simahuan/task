package android.izy.util;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * 位置工具类
 * 
 * @author yangyp
 * @version 1.0, 2015年1月19日 上午9:42:08
 */
public class BitmapUtils {

	private static final Rect sOldBounds = new Rect();
	private static final Canvas sCanvas = new Canvas();
	static {
		sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, Paint.FILTER_BITMAP_FLAG));
	}

	public static Bitmap getBitmapResources(Context context, int resId) {
		Resources res = context.getResources();
		return BitmapFactory.decodeResource(res, resId);
	}

	public static Bitmap bytes2Bimap(byte[] b) {
		if (b.length == 0) {
			return null;
		}
		return BitmapFactory.decodeByteArray(b, 0, b.length);
	}

	public static byte[] bitmapToBytes(Bitmap bitmap) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, bout);
		return bout.toByteArray();
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		BitmapDrawable bd = (BitmapDrawable) drawable;
		return bd.getBitmap();
	}

	public static Bitmap drawableToBitmapByCanvas(Drawable drawable) {
		synchronized (sCanvas) {
			Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
			Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), config);
			// Canvas canvas = new Canvas(bitmap);
			final Canvas canvas = sCanvas;
			canvas.setBitmap(bitmap);

			sOldBounds.set(drawable.getBounds());
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			drawable.setBounds(sOldBounds);
			canvas.setBitmap(null);

			return bitmap;
		}
	}

	public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(context.getResources(), bitmap);
		return bd;
	}

	/**
	 * 图片透明度处理
	 * 
	 * @param src
	 *            原始图片
	 * @param alpha
	 *            透明度
	 * @return
	 */
	public static Bitmap alphaBitmap(Bitmap src, int alpha) {
		int[] argb = new int[src.getWidth() * src.getHeight()];
		src.getPixels(argb, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());// 获得图片的ARGB值
		alpha = alpha * 255 / 100;
		for (int i = 0; i < argb.length; i++) {
			argb[i] = (alpha << 24) | (argb[i] & 0x00FFFFFF);// 修改最高2位的值
		}
		src = Bitmap.createBitmap(argb, src.getWidth(), src.getHeight(), Config.ARGB_8888);
		return src;
	}

	/**
	 * 图片缩放
	 * 
	 * @param filePath
	 * @param dstWidth
	 * @param dstHeight
	 * @return
	 */
	public static Bitmap scaledBitmap(String filePath, int dstWidth, int dstHeight) {
		Bitmap bitmap = null;
		BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		decodeOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, decodeOptions);

		int actualWidth = decodeOptions.outWidth;
		int actualHeight = decodeOptions.outHeight;
		if (actualWidth > dstWidth || actualHeight > dstHeight) {
			int desiredWidth = getResizedDimension(dstWidth, dstHeight, actualWidth, actualHeight);
			int desiredHeight = getResizedDimension(dstHeight, dstWidth, actualHeight, actualWidth);
			decodeOptions.inJustDecodeBounds = false;
			decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
			Bitmap tempBitmap = BitmapFactory.decodeFile(filePath, decodeOptions);
			if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {
				bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
				tempBitmap.recycle();
			} else {
				bitmap = tempBitmap;
			}
		} else {
			decodeOptions.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(filePath, decodeOptions);
		}
		return bitmap;
	}

	/**
	 * 计算缩放后大小
	 * 
	 * @param maxPrimary
	 * @param maxSecondary
	 * @param actualPrimary
	 * @param actualSecondary
	 * @return
	 */
	private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary, int actualSecondary) {
		// If no dominant value at all, just return the actual.
		if (maxPrimary == 0 && maxSecondary == 0) {
			return actualPrimary;
		}

		// If primary is unspecified, scale primary to match secondary's
		// scaling ratio.
		if (maxPrimary == 0) {
			double ratio = (double) maxSecondary / (double) actualSecondary;
			return (int) (actualPrimary * ratio);
		}

		if (maxSecondary == 0) {
			return maxPrimary;
		}

		double ratio = (double) actualSecondary / (double) actualPrimary;
		int resized = maxPrimary;
		if (resized * ratio > maxSecondary) {
			resized = (int) (maxSecondary / ratio);
		}
		return resized;
	}

	/**
	 * 计算绽放比例
	 * 
	 * @param actualWidth
	 * @param actualHeight
	 * @param desiredWidth
	 * @param desiredHeight
	 * @return
	 */
	private static int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
		double wr = (double) actualWidth / desiredWidth;
		double hr = (double) actualHeight / desiredHeight;
		double ratio = Math.min(wr, hr);
		float n = 1.0f;
		while ((n * 2) <= ratio) {
			n *= 2;
		}

		return (int) n;
	}

	/**
	 * 创建一个倒影图，不包含原图
	 * 
	 * @param originalImage
	 *            原图
	 * @param reflectHeight
	 *            倒影高度
	 * @return
	 */
	public static Bitmap createReflectImage(Bitmap originalImage, int reflectHeight) {
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height - reflectHeight, width, reflectHeight, matrix, false);
		Bitmap bitmapCanvas = Bitmap.createBitmap(width, reflectHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapCanvas);
		canvas.drawBitmap(reflectionImage, 0, 0, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, 0, 0, bitmapCanvas.getHeight(), 0x80000000, Color.TRANSPARENT, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, 0, width, bitmapCanvas.getHeight(), paint);

		return bitmapCanvas;
	}

	/**
	 * 创建一个倒影图，包含原图
	 * 
	 * @param originalImage
	 *            原图
	 * @param reflectHeight
	 *            倒影高度
	 * @param reflectGap
	 *            原图与倒影间距
	 * @return
	 */
	public static Bitmap createReflectImage(Bitmap originalImage, int reflectHeight, int reflectGap) {
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();
		int reflectTop = height + reflectGap;

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height - reflectHeight, width, reflectHeight, matrix, false);
		Bitmap bitmapCanvas = Bitmap.createBitmap(width, reflectTop + reflectHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapCanvas);
		canvas.drawBitmap(originalImage, 0, 0, null);
		canvas.drawBitmap(reflectionImage, 0, reflectTop, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, reflectTop, 0, bitmapCanvas.getHeight(), 0x40000000, Color.TRANSPARENT, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapCanvas.getHeight(), paint);

		return bitmapCanvas;
	}

}

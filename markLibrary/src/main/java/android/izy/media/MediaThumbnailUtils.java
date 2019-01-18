package android.izy.media;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.izy.media.MediaFile.MediaFileType;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Images;
import android.util.Log;

public class MediaThumbnailUtils extends ThumbnailUtils {

	static final String TAG = MediaThumbnailUtils.class.getSimpleName();

	/* Maximum pixels size for created bitmap. */
	private static final int MAX_NUM_PIXELS_THUMBNAIL = 512 * 384;
	private static final int MAX_NUM_PIXELS_MICRO_THUMBNAIL = 160 * 120;
	private static final int UNCONSTRAINED = -1;

	/**
	 * Constant used to indicate we should recycle the input in
	 * {@link #extractThumbnail(Bitmap, int, int, int)} unless the output is the
	 * input.
	 */
	public static final int OPTIONS_RECYCLE_INPUT = 0x2;

	/**
	 * Constant used to indicate the dimension of mini thumbnail.
	 * 
	 * @hide Only used by media framework and media provider internally.
	 */
	public static final int TARGET_SIZE_MINI_THUMBNAIL = 320;

	/**
	 * Constant used to indicate the dimension of micro thumbnail.
	 * 
	 * @hide Only used by media framework and media provider internally.
	 */
	public static final int TARGET_SIZE_MICRO_THUMBNAIL = 96;

	/**
	 * This method first examines if the thumbnail embedded in EXIF is bigger
	 * than our target size. If not, then it'll create a thumbnail from original
	 * image. Due to efficiency consideration, we want to let MediaThumbRequest
	 * avoid calling this method twice for both kinds, so it only requests for
	 * MICRO_KIND and set saveImage to true.
	 *
	 * This method always returns a "square thumbnail" for MICRO_KIND thumbnail.
	 *
	 * @param filePath
	 *            the path of image file
	 * @param kind
	 *            could be MINI_KIND or MICRO_KIND
	 * @return Bitmap, or null on failures
	 */
	public static Bitmap createImageThumbnail(String filePath, int kind) {
		boolean wantMini = (kind == Images.Thumbnails.MINI_KIND);
		int targetSize = wantMini ? TARGET_SIZE_MINI_THUMBNAIL : TARGET_SIZE_MICRO_THUMBNAIL;
		int maxPixels = wantMini ? MAX_NUM_PIXELS_THUMBNAIL : MAX_NUM_PIXELS_MICRO_THUMBNAIL;
		SizedThumbnailBitmap sizedThumbnailBitmap = new SizedThumbnailBitmap();
		Bitmap bitmap = null;
		MediaFileType fileType = MediaFile.getFileType(filePath);
		if (fileType != null && fileType.fileType == MediaFile.FILE_TYPE_JPEG) {
			createThumbnailFromEXIF(filePath, targetSize, maxPixels, sizedThumbnailBitmap);
			bitmap = sizedThumbnailBitmap.mBitmap;
		}

		if (bitmap == null) {
			FileInputStream stream = null;
			try {
				stream = new FileInputStream(filePath);
				FileDescriptor fd = stream.getFD();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFileDescriptor(fd, null, options);
				if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
					return null;
				}
				options.inSampleSize = computeSampleSize(options, targetSize, maxPixels);
				options.inJustDecodeBounds = false;

				options.inDither = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
			} catch (IOException ex) {
				Log.e(TAG, "", ex);
			} catch (OutOfMemoryError oom) {
				Log.e(TAG, "Unable to decode file " + filePath + ". OutOfMemoryError.", oom);
			} finally {
				try {
					if (stream != null) {
						stream.close();
					}
				} catch (IOException ex) {
					Log.e(TAG, "", ex);
				}
			}

		}

		if (kind == Images.Thumbnails.MICRO_KIND) {
			// now we make it a "square thumbnail" for MICRO_KIND thumbnail
			bitmap = extractThumbnail(bitmap, TARGET_SIZE_MICRO_THUMBNAIL, TARGET_SIZE_MICRO_THUMBNAIL, OPTIONS_RECYCLE_INPUT);
		}
		return bitmap;
	}
	
    /**
     * Create a video thumbnail for a video. May return null if the video is
     * corrupt or the format is not supported.
     *
     * @param filePath the path of video file
     * @param kind could be MINI_KIND or MICRO_KIND
     */
    public static Bitmap createVideoThumbnail(String filePath, int kind) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }

        if (bitmap == null) return null;

        if (kind == Images.Thumbnails.MINI_KIND) {
            // Scale down the bitmap if it's too large.
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int max = Math.max(width, height);
            if (max > 512) {
                float scale = 512f / max;
                int w = Math.round(scale * width);
                int h = Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            }
        } else if (kind == Images.Thumbnails.MICRO_KIND) {
            bitmap = extractThumbnail(bitmap,
                    TARGET_SIZE_MICRO_THUMBNAIL,
                    TARGET_SIZE_MICRO_THUMBNAIL,
                    OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

	private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == UNCONSTRAINED) && (minSideLength == UNCONSTRAINED)) {
			return 1;
		} else if (minSideLength == UNCONSTRAINED) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * SizedThumbnailBitmap contains the bitmap, which is downsampled either
	 * from the thumbnail in exif or the full image. mThumbnailData,
	 * mThumbnailWidth and mThumbnailHeight are set together only if mThumbnail
	 * is not null.
	 *
	 * The width/height of the sized bitmap may be different from
	 * mThumbnailWidth/mThumbnailHeight.
	 */
	private static class SizedThumbnailBitmap {
		public byte[] mThumbnailData;
		public Bitmap mBitmap;
		public int mThumbnailWidth;
		public int mThumbnailHeight;
	}

	/**
	 * Creates a bitmap by either downsampling from the thumbnail in EXIF or the
	 * full image. The functions returns a SizedThumbnailBitmap, which contains
	 * a downsampled bitmap and the thumbnail data in EXIF if exists.
	 */
	private static void createThumbnailFromEXIF(String filePath, int targetSize, int maxPixels, SizedThumbnailBitmap sizedThumbBitmap) {
		if (filePath == null)
			return;

		ExifInterface exif = null;
		byte[] thumbData = null;
		try {
			exif = new ExifInterface(filePath);
			thumbData = exif.getThumbnail();
		} catch (IOException ex) {
			Log.w(TAG, ex);
		}

		BitmapFactory.Options fullOptions = new BitmapFactory.Options();
		BitmapFactory.Options exifOptions = new BitmapFactory.Options();
		int exifThumbWidth = 0;
		int fullThumbWidth = 0;

		// Compute exifThumbWidth.
		if (thumbData != null) {
			exifOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(thumbData, 0, thumbData.length, exifOptions);
			exifOptions.inSampleSize = computeSampleSize(exifOptions, targetSize, maxPixels);
			exifThumbWidth = exifOptions.outWidth / exifOptions.inSampleSize;
		}

		// Compute fullThumbWidth.
		fullOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, fullOptions);
		fullOptions.inSampleSize = computeSampleSize(fullOptions, targetSize, maxPixels);
		fullThumbWidth = fullOptions.outWidth / fullOptions.inSampleSize;

		// Choose the larger thumbnail as the returning sizedThumbBitmap.
		if (thumbData != null && exifThumbWidth >= fullThumbWidth) {
			int width = exifOptions.outWidth;
			int height = exifOptions.outHeight;
			exifOptions.inJustDecodeBounds = false;
			sizedThumbBitmap.mBitmap = BitmapFactory.decodeByteArray(thumbData, 0, thumbData.length, exifOptions);
			if (sizedThumbBitmap.mBitmap != null) {
				sizedThumbBitmap.mThumbnailData = thumbData;
				sizedThumbBitmap.mThumbnailWidth = width;
				sizedThumbBitmap.mThumbnailHeight = height;
			}
		} else {
			fullOptions.inJustDecodeBounds = false;
			sizedThumbBitmap.mBitmap = BitmapFactory.decodeFile(filePath, fullOptions);
		}
	}
}

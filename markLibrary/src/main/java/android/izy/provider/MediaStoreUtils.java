package android.izy.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.izy.database.CursorIzy;
import android.izy.database.CursorWapper;
import android.provider.MediaStore;
import android.provider.MediaStore.Files.FileColumns;

/**
 * 媒体库工具类
 * 
 * @author yangyp
 * @version 1.0, 2014年8月18日 上午11:22:18
 */
public abstract class MediaStoreUtils {

	/**
	 * 获取图片
	 * 
	 * @param context
	 */
	public static List<ImageInfo> getImage(Context context) {
		List<ImageInfo> results = new ArrayList<ImageInfo>();
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
		if (cursor != null) {
			CursorIzy cursorIzy = new CursorWapper(cursor);
			while (cursor.moveToNext()) {
				results.add(new ImageInfo(cursorIzy));
			}
		}
		return results;
	}

	/**
	 * 获取音频
	 * 
	 * @param context
	 */
	public static List<AudioInfo> getAudio(Context context, String selection) {
		List<AudioInfo> results = new ArrayList<AudioInfo>();
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection, null, null);
		if (cursor != null) {
			CursorIzy cursorIzy = new CursorWapper(cursor);
			while (cursor.moveToNext()) {
				results.add(new AudioInfo(cursorIzy));
			}
		}
		return results;
	}

	/**
	 * 获取视频
	 * 
	 * @param context
	 */
	public static List<VideoInfo> getVideo(Context context) {
		List<VideoInfo> results = new ArrayList<VideoInfo>();
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
		if (cursor != null) {
			CursorIzy cursorIzy = new CursorWapper(cursor);
			while (cursor.moveToNext()) {
				results.add(new VideoInfo(cursorIzy));
			}
		}
		return results;
	}

	// 文档类型
	public static final String[] DOC_MIME_TYPE = { "text/plain", "application/pdf", "application/msword", "application/vnd.ms-excel" };

	public static List<FileInfo> getFile(Context context, String[] mimeType, String[] suffixName) {
		Iterator<String> it = Arrays.asList(DOC_MIME_TYPE).iterator();
		StringBuilder selection = new StringBuilder();
		while (it.hasNext()) {
			selection.append(FileColumns.MIME_TYPE + "=='" + it.next() + "'");
			if (it.hasNext()) {
				selection.append(" OR ");
			}
		}

		List<FileInfo> results = new ArrayList<FileInfo>();
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(MediaStore.Files.getContentUri("external"), null, selection.toString(), null, null);
		if (cursor != null) {
			CursorIzy cursorIzy = new CursorWapper(cursor);
			while (cursor.moveToNext()) {
				results.add(new FileInfo(cursorIzy));
			}
		}
		return results;
	}
}

package android.izy.provider;

import android.izy.database.CursorIzy;
import android.provider.MediaStore.MediaColumns;

/**
 * 媒体信息
 * 
 * @author yangyp
 *
 */
public abstract class MediaInfo {

	private int _id; // 媒体ID
	private String title; // 名字

	private String displayName; // 文件的名称
	private String mimeType; // 文件类型
	private String path; // URI文件存放路径
	private int size; // 文件大小
	private int dateModified; // 文件最后修改日期

	public MediaInfo(CursorIzy curs) {
		_id = curs.getInt(MediaColumns._ID);
		title = curs.getString(MediaColumns.TITLE);

		displayName = curs.getString(MediaColumns.DISPLAY_NAME);
		mimeType = curs.getString(MediaColumns.MIME_TYPE);
		path = curs.getString(MediaColumns.DATA);
		size = curs.getInt(MediaColumns.SIZE);
		dateModified = curs.getInt(MediaColumns.DATE_MODIFIED);
	}

	public int get_id() {
		return _id;
	}

	public String getTitle() {
		return title;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getPath() {
		return path;
	}

	public int getSize() {
		return size;
	}

	public int getDateModified() {
		return dateModified;
	}
}

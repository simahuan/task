package android.izy.provider;

import android.izy.database.CursorIzy;
import android.provider.MediaStore.Files.FileColumns;

public class FileInfo extends MediaInfo {

	/**
	 * The index of the parent directory of the file
	 */
	private int parent;

	/**
	 * The media type (audio, video, image or playlist) of the file, or 0 for
	 * not a media file
	 * 
	 * @link android.provider.MediaStore.Files.FileColumns#MEDIA_TYPE_NONE
	 * @link android.provider.MediaStore.Files.FileColumns#MEDIA_TYPE_IMAGE
	 * @link android.provider.MediaStore.Files.FileColumns#MEDIA_TYPE_AUDIO
	 * @link android.provider.MediaStore.Files.FileColumns#MEDIA_TYPE_VIDEO
	 * @link android.provider.MediaStore.Files.FileColumns#MEDIA_TYPE_PLAYLIST
	 */
	private String mediaType;

	public FileInfo(CursorIzy curs) {
		super(curs);
		parent = curs.getInt(FileColumns.PARENT);
		//mediaType = curs.getInt(curs.getColumnIndexOrThrow(FileColumns.MEDIA_TYPE));
	}

}

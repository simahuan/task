package android.izy.provider;

import android.izy.database.CursorIzy;
import android.provider.MediaStore.Audio;

public class AudioInfo extends MediaInfo {

	private String artist; // 歌手名
	private String album; // 专辑名
	private int duration; // 歌曲文件播放时间长度
	private String year; // 歌曲文件的发行日期

	public AudioInfo(CursorIzy curs) {
		super(curs);
		artist = curs.getString(Audio.Media.ARTIST); // 歌手名
		album = curs.getString(Audio.Media.ALBUM); // 专辑名
		duration = curs.getInt(Audio.Media.DURATION); // 歌曲文件播放时间长度
		year = curs.getString(Audio.Media.YEAR); // 歌曲文件的发行日期
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public int getDuration() {
		return duration;
	}

	public String getYear() {
		return year;
	}

}

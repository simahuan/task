package android.izy.database;

import android.database.CharArrayBuffer;
import android.database.Cursor;

public class CursorWapper extends android.database.CursorWrapper implements CursorIzy {

	private Cursor mCursor;

	public CursorWapper(Cursor cursor) {
		super(cursor);
		mCursor = cursor;
	}

	@Override
	public byte[] getBlob(String columnName) {
		return mCursor.getBlob(mCursor.getColumnIndexOrThrow(columnName));
	}

	@Override
	public String getString(String columnName) {
		return mCursor.getString(mCursor.getColumnIndexOrThrow(columnName));
	}

	@Override
	public void copyStringToBuffer(String columnName, CharArrayBuffer buffer) {
		mCursor.copyStringToBuffer(mCursor.getColumnIndexOrThrow(columnName), buffer);
	}

	@Override
	public short getShort(String columnName) {
		return mCursor.getShort(mCursor.getColumnIndexOrThrow(columnName));
	}

	@Override
	public int getInt(String columnName) {
		return mCursor.getInt(mCursor.getColumnIndexOrThrow(columnName));
	}

	@Override
	public long getLong(String columnName) {
		return mCursor.getLong(mCursor.getColumnIndexOrThrow(columnName));
	}

	@Override
	public float getFloat(String columnName) {
		return mCursor.getFloat(mCursor.getColumnIndexOrThrow(columnName));
	}

	@Override
	public double getDouble(String columnName) {
		return mCursor.getDouble(mCursor.getColumnIndexOrThrow(columnName));
	}

}

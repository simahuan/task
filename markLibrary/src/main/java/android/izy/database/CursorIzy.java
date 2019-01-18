package android.izy.database;

import android.database.CharArrayBuffer;
import android.database.Cursor;

public interface CursorIzy extends Cursor {

	/**
	 * Returns the value of the requested column as a byte array.
	 *
	 * <p>
	 * The result and whether this method throws an exception when the column
	 * value is null or the column type is not a blob type is
	 * implementation-defined.
	 *
	 * @param columnName
	 *            the name of the target column.
	 * @return the value of that column as a byte array.
	 */
	byte[] getBlob(String columnName);

	/**
	 * Returns the value of the requested column as a String.
	 *
	 * <p>
	 * The result and whether this method throws an exception when the column
	 * value is null or the column type is not a string type is
	 * implementation-defined.
	 *
	 * @param columnName
	 *            the name of the target column.
	 * @return the value of that column as a String.
	 */
	String getString(String columnName);

	/**
	 * Retrieves the requested column text and stores it in the buffer provided.
	 * If the buffer size is not sufficient, a new char buffer will be allocated
	 * and assigned to CharArrayBuffer.data
	 * 
	 * @param columnName
	 *            the name of the target column. if the target column is null,
	 *            return buffer
	 * @param buffer
	 *            the buffer to copy the text into.
	 */
	void copyStringToBuffer(String columnName, CharArrayBuffer buffer);

	/**
	 * Returns the value of the requested column as a short.
	 *
	 * <p>
	 * The result and whether this method throws an exception when the column
	 * value is null, the column type is not an integral type, or the integer
	 * value is outside the range [<code>Short.MIN_VALUE</code>,
	 * <code>Short.MAX_VALUE</code>] is implementation-defined.
	 *
	 * @param columnName
	 *            the name of the target column.
	 * @return the value of that column as a short.
	 */
	short getShort(String columnName);

	/**
	 * Returns the value of the requested column as an int.
	 *
	 * <p>
	 * The result and whether this method throws an exception when the column
	 * value is null, the column type is not an integral type, or the integer
	 * value is outside the range [<code>Integer.MIN_VALUE</code>,
	 * <code>Integer.MAX_VALUE</code>] is implementation-defined.
	 *
	 * @param columnName
	 *            the name of the target column.
	 * @return the value of that column as an int.
	 */
	int getInt(String columnName);

	/**
	 * Returns the value of the requested column as a long.
	 *
	 * <p>
	 * The result and whether this method throws an exception when the column
	 * value is null, the column type is not an integral type, or the integer
	 * value is outside the range [<code>Long.MIN_VALUE</code>,
	 * <code>Long.MAX_VALUE</code>] is implementation-defined.
	 *
	 * @param columnName
	 *            the name of the target column.
	 * @return the value of that column as a long.
	 */
	long getLong(String columnName);

	/**
	 * Returns the value of the requested column as a float.
	 *
	 * <p>
	 * The result and whether this method throws an exception when the column
	 * value is null, the column type is not a floating-point type, or the
	 * floating-point value is not representable as a <code>float</code> value
	 * is implementation-defined.
	 *
	 * @param columnName
	 *            the name of the target column.
	 * @return the value of that column as a float.
	 */
	float getFloat(String columnName);

	/**
	 * Returns the value of the requested column as a double.
	 *
	 * <p>
	 * The result and whether this method throws an exception when the column
	 * value is null, the column type is not a floating-point type, or the
	 * floating-point value is not representable as a <code>double</code> value
	 * is implementation-defined.
	 *
	 * @param columnName
	 *            the name of the target column.
	 * @return the value of that column as a double.
	 */
	double getDouble(String columnName);
}

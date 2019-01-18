package android.izy.util.parse;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.util.Log;

public abstract class SimpleXmlUtils {

	static final String TAG = SimpleXmlUtils.class.getSimpleName();

	public static void write(Object value, File xmlPath) {
		try {
			Serializer serializer = new Persister();
			serializer.write(value, xmlPath);
		} catch (Exception e) {
			Log.e(TAG, "write: ", e);
		}
	}

	public static void write(Object value, OutputStream out) {
		try {
			Serializer serializer = new Persister();
			serializer.write(value, out);
		} catch (Exception e) {
			Log.e(TAG, "write: ", e);
		}
	}

	public static void write(Object value, Writer writer) {
		try {
			Serializer serializer = new Persister();
			serializer.write(value, writer);
		} catch (Exception e) {
			Log.e(TAG, "write: ", e);
		}
	}

	public static <T> T read(String source, Class<T> clazz) {
		T result = null;
		try {
			Serializer serializer = new Persister();
			result = serializer.read(clazz, source);
		} catch (Exception e) {
			Log.e(TAG, "read: ", e);
		}
		return result;
	}

	public static <T> T read(InputStream source, Class<T> clazz) {
		T result = null;
		try {
			Serializer serializer = new Persister();
			result = serializer.read(clazz, source);
		} catch (Exception e) {
			Log.e(TAG, "read: ", e);
		}
		return result;
	}

	public static <T> T read(Reader source, Class<T> clazz) {
		T result = null;
		try {
			Serializer serializer = new Persister();
			result = serializer.read(clazz, source);
		} catch (Exception e) {
			Log.e(TAG, "read: ", e);
		}
		return result;
	}
}

package android.izy.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class PreferencesUtils {

	private static SharedPreferences mSharedPreferences;

	public static void init(Context context) {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static String getString(String key, String defValue) {
		return mSharedPreferences.getString(key, defValue);
	}

	public static void setString(String key, String value) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static int getInt(String key, int defValue) {
		SharedPreferences sharedPref = mSharedPreferences;
		return sharedPref.getInt(key, defValue);
	}

	public static void setInt(String key, int value) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static long getLong(String key, long defValue) {
		SharedPreferences sharedPref = mSharedPreferences;
		return sharedPref.getLong(key, defValue);
	}

	public static void setLong(String key, long value) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static float getFloat(String key, float defValue) {
		SharedPreferences sharedPref = mSharedPreferences;
		return sharedPref.getFloat(key, defValue);
	}

	public static void setFloat(String key, float value) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	public static boolean getBoolean(String key, boolean defValue) {
		SharedPreferences sharedPref = mSharedPreferences;
		return sharedPref.getBoolean(key, defValue);
	}

	public static void setBoolean(String key, boolean value) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static void remove(String key) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.remove(key);
		editor.commit();
	}

	public static void clear() {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.clear();
		editor.commit();
	}
}

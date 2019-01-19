package com.zt.task.system.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;

public final class Preferences {

    private Preferences() {
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static void set(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = Preferences.getSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String key) {
        return Preferences.getSharedPreferences(context).getBoolean(key, false);
    }


    public static void set(Context context, String key, String value) {
        SharedPreferences.Editor editor = Preferences.getSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key) {
        return Preferences.getSharedPreferences(context).getString(key, "");
    }

    public static void set(Context context, String key, int value) {
        SharedPreferences.Editor editor = Preferences.getSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key) {
        return Preferences.getSharedPreferences(context).getInt(key, 0);
    }

    public static void set(Context context, String key, long value) {
        SharedPreferences.Editor editor = Preferences.getSharedPreferences(context).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(Context context, String key) {
        return Preferences.getSharedPreferences(context).getLong(key, 0);
    }

    public static byte[] getBytes(Context context, String key) {
        String s = Preferences.getSharedPreferences(context).getString(key, "");
        if (TextUtils.isEmpty(s)) {
            return null;
        }
        return Base64.decode(s, Base64.NO_WRAP | Base64.URL_SAFE);
    }

    public static void set(Context context, String key, Parcelable value) {
        byte[] bytes = ParcelableUtil.marshal(value);

        SharedPreferences.Editor editor = Preferences.getSharedPreferences(context).edit();
        if (value != null) {
            editor.putString(key, Base64.encodeToString(bytes, Base64.NO_WRAP | Base64.URL_SAFE));
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    public static void remove(Context context, String key) {
        SharedPreferences.Editor editor = Preferences.getSharedPreferences(context).edit();
        editor.remove(key);
        editor.apply();
    }
}

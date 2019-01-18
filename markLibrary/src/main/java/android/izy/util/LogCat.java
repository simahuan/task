package android.izy.util;

import java.util.Locale;
import android.util.Log;

public abstract class LogCat {

	public static String TAG = "AndroidIzy";
	public static boolean DEBUG = Log.isLoggable(TAG, Log.INFO);

	public static void setTag(String tag) {
		d("Changing log tag to %s", tag);
		TAG = tag;
		DEBUG = Log.isLoggable(TAG, Log.VERBOSE);
	}

	public static void v(String format, Object... args) {
		Log.v(TAG, buildMessage(format, args));
	}

	public static void d(String format, Object... args) {
		Log.d(TAG, buildMessage(format, args));
	}

	public static void i(String format, Object... args) {
		Log.i(TAG, buildMessage(format, args));
	}

	public static void e(String format, Object... args) {
		Log.e(TAG, buildMessage(format, args));
	}

	public static void e(Throwable err, String format, Object... args) {
		Log.e(TAG, buildMessage(format, args), err);
	}

	public static void wtf(String format, Object... args) {
		Log.wtf(TAG, buildMessage(format, args));
	}

	public static void wtf(Throwable err, String format, Object... args) {
		Log.wtf(TAG, buildMessage(format, args), err);
	}

	/**
	 * Formats the caller's provided message and prepends useful info like
	 * calling thread ID and method name.
	 */
	 private static String buildMessage(String format, Object[] args)
	    {
	        String msg = (args == null) ? format : String.format(Locale.US, format, args);
	        StackTraceElement[] trace = new Throwable().getStackTrace();

	        String caller = "<unknown>";
	        String fileName;
	        String methodName;
	        int lineNumber;

	        for (int i = 2; i < trace.length; ++i) {
	            Class<? extends StackTraceElement> clazz = trace[i].getClass();
	            if (!(clazz.equals(LogCat.class))) {
	                String callingClass = trace[i].getClassName();
	                callingClass = callingClass.substring(callingClass.lastIndexOf(46) + 1);
	                callingClass = callingClass.substring(callingClass.lastIndexOf(36) + 1);

	                fileName = trace[i].getFileName();
	                lineNumber = trace[i].getLineNumber();
	                methodName = trace[i].getMethodName();

	                caller = callingClass + "." + methodName + " ("+fileName +":"+ lineNumber +") ";
	                break;
	            }
	        }
	        return String.format(Locale.US, "[%d] %s: %s", new Object[] {
	                Long.valueOf(Thread.currentThread().getId()), caller, msg
	        });
	    }
	
	/*
	private static String buildMessage(String format, Object... args) {
		String msg = (args == null) ? format : String.format(Locale.US, format, args);
		StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

		String caller = "<unknown>";
		// Walk up the stack looking for the first caller outside of VolleyLog.
		// It will be at least two frames up, so start there.
		for (int i = 2; i < trace.length; i++) {
			Class<?> clazz = trace[i].getClass();
			if (!clazz.equals(LogCat.class)) {
				String callingClass = trace[i].getClassName();
				callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
				callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);

				caller = callingClass + "." + trace[i].getMethodName();
				break;
			}
		}
		return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread().getId(), caller, msg);
	}*/
}

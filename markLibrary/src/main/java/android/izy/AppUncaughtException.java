package android.izy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.TreeSet;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 * 
 * @author yangyp
 */
public class AppUncaughtException implements UncaughtExceptionHandler {

	static final String TAG = AppUncaughtException.class.getSimpleName();
	private static AppUncaughtException INSTANCE;
	private Context mContext;
	private File mLogDir;
	private UncaughtExceptionHandler mUncaughtExceptionHandler;

	/** 使用Properties来保存设备的信息和错误堆栈信息 */
	private Properties mDeviceCrashInfo = new Properties();
	private static final String VERSION_NAME = "versionName";
	private static final String VERSION_CODE = "versionCode";

	/** 错误报告文件的扩展名 */
	private static final String CRASH_EXTNAME = ".log";

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param ctx
	 */
	private AppUncaughtException(Context ctx) {
		mContext = ctx;
		mLogDir = mContext.getExternalFilesDir("log");
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/** 获取CrashHandler实例 ,单例模式 */
	synchronized public static AppUncaughtException getInstance(Context ctx) {
		if (INSTANCE == null) {
			INSTANCE = new AppUncaughtException(ctx);
		}
		return INSTANCE;
	}

	public void setExternalCacheDir(String externalCacheDir) {
		setExternalCacheDir(new File(externalCacheDir));
	}

	public void setExternalCacheDir(File externalCacheDir) {
		this.mLogDir = externalCacheDir;
	}

	public void setUncaughtExceptionHandler(UncaughtExceptionHandler mUncaughtExceptionHandler) {
		this.mUncaughtExceptionHandler = mUncaughtExceptionHandler;
	}

	/**
	 * 获取已经排序的错误报告文件
	 * 
	 * @param ctx
	 * @return
	 */
	public File[] getCrashReportFiles() {
		// 返回一个字符串数组，这些字符串指定此抽象路径名表示的目录中满足指定过滤器的文件和目录
		File[] crashFiles = mLogDir.listFiles(new FilenameFilter() {
			// 测试指定文件是否应该包含在某一文件列表中。
			public boolean accept(File dir, String name) {
				return name.endsWith(CRASH_EXTNAME);
			}
		});

		TreeSet<File> results = new TreeSet<File>();
		if (crashFiles != null && crashFiles.length > 0) {
			results.addAll(Arrays.asList(crashFiles));
		}
		return results.toArray(new File[0]);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		handleException(ex);
		clearErrorLog();

		// final String errorMsg = ex.getLocalizedMessage();
		if (mUncaughtExceptionHandler != null) {
			mUncaughtExceptionHandler.uncaughtException(this, ex);
		}

		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}

	/**
	 * 保留最近15个错误日志文件，删除之前所有错误日志文件
	 */
	private void clearErrorLog() {
		File[] crFiles = getCrashReportFiles();
		if (crFiles != null && crFiles.length > 15) {
			crFiles[0].delete();
			clearErrorLog();
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */
	private void handleException(Throwable ex) {
		if (ex == null) {
			return;
		}

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Log.e(TAG, "没有找到SD卡，保存错误信息失败.");
			return;
		}

		if (!mLogDir.exists()) {
			mLogDir.mkdirs();
		}

		try {
			SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			String fileName = String.format("crash.%s" + CRASH_EXTNAME, dformat.format(System.currentTimeMillis()));
			File file = new File(mLogDir, fileName);
			FileOutputStream outStream = new FileOutputStream(file, true);
			collectCrashDeviceInfo(mContext);
			mDeviceCrashInfo.store(outStream, "crash " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis()));
			Writer writer = new OutputStreamWriter(outStream);
			writer.write(getErrorMessage(ex));
			writer.flush();
			outStream.close();
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing report file...", e);
		}
	}

	/**
	 * 收集程序崩溃的设备信息
	 * 
	 * @param ctx
	 */
	private void collectCrashDeviceInfo(Context ctx) {
		try {
			PackageManager pManager = ctx.getPackageManager();
			PackageInfo pkgInfo = pManager.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pkgInfo != null) {
				mDeviceCrashInfo.put(VERSION_NAME, pkgInfo.versionName == null ? "not set" : pkgInfo.versionName);
				mDeviceCrashInfo.put(VERSION_CODE, String.valueOf(pkgInfo.versionCode));
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Error while collect package info", e);
		}
		// 使用反射来收集设备信息.在Build类中包含各种设备信息,
		// 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
		// 返回 Field 对象的一个数组，这些对象反映此 Class 对象所表示的类或接口所声明的所有字段
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				// setAccessible(boolean flag)
				// 将此对象的 accessible 标志设置为指示的布尔值。
				// 通过设置Accessible属性为true,才能对私有变量进行访问，不然会得到一个IllegalAccessException的异常
				field.setAccessible(true);
				mDeviceCrashInfo.put(field.getName(), String.valueOf(field.get(null)));
			} catch (Exception e) {
				Log.e(TAG, "Error while collect crash info", e);
			}
		}
	}

	/**
	 * 获取异常错误信息内容
	 * 
	 * @param ex
	 * @return
	 */
	private String getErrorMessage(Throwable ex) {
		Writer errInfo = new StringWriter();
		PrintWriter errWriter = new PrintWriter(errInfo);
		ex.printStackTrace(errWriter);

		// getCause() 返回此 throwable 的 cause；如果 cause 不存在或未知，则返回 null。
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(errWriter);
			cause = cause.getCause();
		}

		errWriter.close();
		return errInfo.toString();
	}

	public interface UncaughtExceptionHandler {
		void uncaughtException(AppUncaughtException handler, Throwable ex);
	}
}
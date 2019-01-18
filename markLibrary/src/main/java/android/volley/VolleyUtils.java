package android.volley;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Environment;
import android.volley.toolbox.BasicNetwork2;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

public class VolleyUtils extends Volley {

	private static final String DEFAULT_CACHE_DIR = "volley-izy";
	private static File cacheDir;

	/**
	 * @param context
	 * @param cacheDir
	 *            "/sdcard/demo/" new File(context.getCacheDir(), "izy")
	 * @param stack
	 */
	public static RequestQueue newRequestQueueInDisk(Context context, File cacheDir, HttpStack stack) {
		VolleyUtils.cacheDir = cacheDir;
		String userAgent = "volley-izy/0";
		try {
			String packageName = context.getPackageName();
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
			userAgent = packageName + "/" + info.versionCode;
		} catch (NameNotFoundException e) {
		}

		if (stack == null) {
			if (Build.VERSION.SDK_INT >= 9) {
				stack = new HurlStack();
			} else {
				// Prior to Gingerbread, HttpUrlConnection was unreliable.
				// See:
				// http://android-developers.blogspot.com/2011/09/androids-http-clients.html
				stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
			}
		}

		Network network = new BasicNetwork2(stack);
		RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
		queue.start();

		return queue;
	}

	public static RequestQueue newRequestQueueInDisk(Context context) {
		return newRequestQueueInDisk(context, getCacheDir(context), null);
	}

	/**
	 * 获取缓存目录
	 * 
	 * @param context
	 * @return
	 */
	private static File getCacheDir(Context context) {
		boolean hasSDCard = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
		File cacheDir = new File(hasSDCard ? context.getExternalCacheDir() : context.getCacheDir(), DEFAULT_CACHE_DIR);
		return cacheDir;
	}

	public static File getCacheDir() {
		return cacheDir;
	}
}

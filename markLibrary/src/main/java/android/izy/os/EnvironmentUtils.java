package android.izy.os;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class EnvironmentUtils extends Environment {

	/**
	 * 获取屏幕分辨率
	 * 
	 * @param context
	 * @return density密度 | widthPixels宽度 | heightPixels高度
	 */
	public static DisplayMetrics getResolution(Context context) {
		WindowManager wmManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wmManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		return displayMetrics;
	}

	/**
	 * 获取多个SD路径
	 * 
	 * @param context
	 * @return
	 */
	public static String[] getExternalStorageDirectoryAll() {
		Set<String> results = new HashSet<String>();
		if (hasSDCard()) {
			results.add(Environment.getExternalStorageDirectory().getPath());
			try {
				// 获取非默认SD卡路径
				File mountFile = new File("/proc/mounts");
				if (mountFile.exists()) {
					Scanner scanner = new Scanner(mountFile);
					while (scanner.hasNext()) {
						String line = scanner.nextLine().trim();
						if (line.startsWith("/dev/block/vold/")) {
							String[] lineElements = line.split(" ");
							String element = lineElements[1];
							if (!element.equals("/mnt/sdcard")) {
								File root = new File(element);
								if (root.exists() && root.isDirectory() && root.canWrite()) {
									results.add(root.getPath());
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return results.toArray(new String[0]);
	}

	/**
	 * 是否SD卡主目录路径
	 * 
	 * @return
	 */
	public static boolean isRootPath(String path) {
		for (String root : getExternalStorageDirectoryAll()) {
			if (root.equals(path)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否有挂载了SD卡
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	/**
	 * 获得SD卡总大小
	 * 
	 * @return
	 */
	public static long getSDTotalSize() {
		File path = Environment.getExternalStorageDirectory();
		return getStatFsTotalSize(path);
	}

	public static String getSDTotalSize(Context context) {
		return Formatter.formatFileSize(context, getSDTotalSize());
	}

	/**
	 * 获得sd卡剩余容量，即可用大小
	 * 
	 * @return
	 */
	public static long getSDAvailableSize() {
		File path = Environment.getExternalStorageDirectory();
		return getStatFsAvailableSize(path);
	}

	public static String getSDAvailableSize(Context context) {
		return Formatter.formatFileSize(context, getSDAvailableSize());
	}

	/**
	 * 获得机身内存总大小
	 * 
	 * @return
	 */
	public static long getRomTotalSize() {
		File path = Environment.getDataDirectory();
		return getStatFsTotalSize(path);
	}

	public static String getRomTotalSize(Context context) {
		return Formatter.formatFileSize(context, getRomTotalSize());
	}

	/**
	 * 获得机身可用内存
	 * 
	 * @return
	 */
	public static long getRomAvailableSize() {
		File path = Environment.getDataDirectory();
		return getStatFsAvailableSize(path);
	}

	public static String getRomAvailableSize(Context context) {
		return Formatter.formatFileSize(context, getRomAvailableSize());
	}

	/** 根据路径获取大小 */
	private static long getStatFsTotalSize(File path) {
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return blockSize * totalBlocks;
	}

	/** 根据路径获取可用大小 */
	private static long getStatFsAvailableSize(File path) {
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return blockSize * availableBlocks;
	}

	/**
	 * 获取IP地址
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getIPAddress(Context context) {
		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiMgr.getConnectionInfo();
		return info != null ? Formatter.formatIpAddress(info.getIpAddress()) : null;
	}

	/**
	 * 获取MAC地址
	 * 
	 * @param context
	 * @return
	 */
	public String getMacAddress(Context context) {
		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiMgr.getConnectionInfo();
		return info != null ? info.getMacAddress() : null;
	}

	/**
	 * 获取网关
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getGateway(Context context) {
		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo info = wifiMgr.getDhcpInfo();
		return info != null ? Formatter.formatIpAddress(info.gateway) : null;
	}

	/**
	 * 获取Wifi SSID名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getWifiSSID(Context context) {
		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiMgr.getConnectionInfo();
		return info != null ? info.getSSID() : null;
	}

	/**
	 * 获取Wifi BSSID名称
	 * 
	 * @param context
	 * @return
	 */
	public String getWifiBSSID(Context context) {
		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiMgr.getConnectionInfo();
		return info != null ? info.getBSSID() : null;
	}

	/**
	 * 去除Wifi双引号
	 * 
	 * @param string
	 * @return
	 */
	public static String removeDoubleQuotes(String string) {
		if (string == null)
			return null;
		final int length = string.length();
		if ((length > 1) && (string.charAt(0) == '"') && (string.charAt(length - 1) == '"')) {
			return string.substring(1, length - 1);
		}
		return string;
	}

	/**
	 * 获取本机电话号码
	 * <p>
	 * 需求添加以下权限<br>
	 * uses-permission android:name="android.permission.CALL_PHONE"
	 * </p>
	 * 
	 * @return
	 */
	public static String getNativePhoneNumber(Context context) {
		TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telManager.getLine1Number();
	}

	/**
	 * Role:Telecom service providers获取手机服务商信息 <BR>
	 * 需要加入权限<uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/> <BR>
	 * Date:2012-3-12 <BR>
	 */
	public static String getProvidersName(Context context) {
		String result = null;
		// 返回唯一的用户ID;就是这张卡的编号神马的
		TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String IMSI = telManager.getSubscriberId();
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			result = "中国移动";
		} else if (IMSI.startsWith("46001")) {
			result = "中国联通";
		} else if (IMSI.startsWith("46003")) {
			result = "中国电信";
		}
		return result;
	}

	/**
	 * 隐藏输入框键盘
	 * 
	 * @param context
	 * @param editText
	 */
	public static void hideSoftInputFromWindow(Context context, EditText editText) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	/**
	 * 判断是否有权限
	 * 
	 * @param context
	 * @param permName
	 * @return
	 * @see android.Manifest.permission
	 */
	public static boolean checkPermission(Context context, String permName) {
		PackageManager pm = context.getPackageManager();
		int hasPerm = pm.checkPermission(permName, context.getPackageName());
		return hasPerm == PackageManager.PERMISSION_GRANTED;
	}
}

package android.izy.util;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * 文件工具类
 * 
 * @author yangyp
 * @version 1.0, 2014年7月22日 上午8:53:33
 */
public abstract class FileUtils {

	private static final String EMPTY = "";
	private static final int UNIT_INTERVAL = 1024;
	private static final int DECIMAL_NUMBER = 100;
	public static final int KB = 1 * 1024;
	public static final int MB = 1 * 1024 * KB;
	public static final int GB = 1 * 1024 * MB;

	/**
	 * 获取文件扩展名(jpg/txt)
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getExtension(File file) {
		return file != null ? getExtension(file.getName()) : EMPTY;
	}

	/**
	 * 获取文件扩展名(jpg/txt)
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getExtension(String fileName) {
		int index = fileName.lastIndexOf(".");
		return index != -1 ? fileName.substring(index + 1) : EMPTY;
	}

	public static String getMimeType(String fileUrl) throws IOException {
		try {
			FileNameMap fileNameMap = URLConnection.getFileNameMap();
			return fileNameMap.getContentTypeFor(fileUrl);
		} catch (Exception e) {
			return EMPTY;
		}
	}

	/**
	 * 将byte转为相应GB/MB/KB/B
	 * 
	 * @param size
	 *            文件大小
	 * @return
	 */
	public static String getFileSize(long size) {
		String result = null;
		if (size > GB) {
			DecimalFormat df = new DecimalFormat("#.##");
			result = String.format("%sGB", df.format(size / (float) GB));
		} else if (size > MB) {
			DecimalFormat df = new DecimalFormat("#.##");
			result = String.format("%sMB", df.format(size / (float) MB));
		} else if (size > KB) {
			DecimalFormat df = new DecimalFormat("#.##");
			result = String.format("%sKB", df.format(size / (float) KB));
		} else {
			result = String.format("%sB", size);
		}

		return result;
	}

	/**
	 * 将GB/MB/KB/B转化为byte
	 * 
	 * @param total
	 * @return
	 */
	public static long getFileSize(String total) {
		long result = -1;
		total = total.toUpperCase(Locale.getDefault());
		if (total.endsWith("GB") || total.endsWith("G")) {
			try {
				// total.split("[KB|K]")[0])
				String totalSize = total.substring(0, total.lastIndexOf("G"));
				return Long.parseLong(totalSize) * GB;
			} catch (NumberFormatException e) {
			}
		} else if (total.endsWith("MB") || total.endsWith("M")) {
			try {
				String totalSize = total.substring(0, total.lastIndexOf("M"));
				return Long.parseLong(totalSize) * MB;
			} catch (NumberFormatException e) {
			}
		} else if (total.endsWith("KB") || total.endsWith("K")) {
			try {
				String totalSize = total.substring(0, total.lastIndexOf("K"));
				return Long.parseLong(totalSize) * KB;
			} catch (NumberFormatException e) {
			}
		}

		return result;
	}

	/**
	 * 将size转为相应TB/GB/MB/KB/B
	 * 
	 * @param size
	 * @return
	 */
	public static String makeSizeString(long size) {
		String unit = "B";
		if (size < DECIMAL_NUMBER) {
			return Long.toString(size) + " " + unit;
		}

		unit = "KB";
		double sizeDouble = (double) size / (double) UNIT_INTERVAL;
		if (sizeDouble > UNIT_INTERVAL) {
			sizeDouble = (double) sizeDouble / (double) UNIT_INTERVAL;
			unit = "MB";
		}
		if (sizeDouble > UNIT_INTERVAL) {
			sizeDouble = (double) sizeDouble / (double) UNIT_INTERVAL;
			unit = "GB";
		}
		if (sizeDouble > UNIT_INTERVAL) {
			sizeDouble = (double) sizeDouble / (double) UNIT_INTERVAL;
			unit = "TB";
		}

		// decimal places
		double formatedSize = Math.round(sizeDouble * DECIMAL_NUMBER) / (double) DECIMAL_NUMBER;
		return Double.toString(formatedSize) + " " + unit;
	}

	/**
	 * 创建副本文件名
	 * 
	 * @param info
	 * @return 副本文件名
	 */
	public static String createCopyFile(File file) {
		if (file.exists()) {
			int i = 1;
			String fileName = String.format("%s- 副本 (%s)", file.getName(), i++);
			while (new File(file.getPath(), fileName).exists()) {
				fileName = String.format("%s- 副本 (%s)", file.getName(), i++);
			}
			return fileName;
		}
		return file.getName();
	}
}

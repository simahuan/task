package android.izy.util;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * URL编码/解码工具
 * 
 * @author yangyp
 * @version 1.0, 2014年8月1日 上午9:38:35
 */
public abstract class URLUtils {

	private static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * 使用默认的UTF-8字符集编码URL
	 * 
	 * @param url
	 * @return
	 */
	public static String encodeURI(String url) {
		try {
			return new URI(null, null, url, null).toASCIIString();
		} catch (URISyntaxException e) {
			return url;
		}
	}

	/**
	 * 使用默认的UTF-8字符集编码URL参数
	 * 
	 * @param param
	 * @return
	 */
	public static String encodeURIComponent(String param) {
		try {
			return java.net.URLEncoder.encode(param, DEFAULT_CHARSET);
		} catch (Exception e) {
			return param;
		}
	}

	/**
	 * 使用默认的UTF-8字符集解码。
	 * 
	 * @param url
	 * @return
	 */
	public static String decodeURI(String url) {
		try {
			return java.net.URLDecoder.decode(url, DEFAULT_CHARSET);
		} catch (Exception e) {
			return url;
		}
	}

	/**
	 * 获取获取URL地址
	 * 
	 * @param url
	 * @return
	 */
	public static String getParentURI(String url) {
		int index = url.lastIndexOf("/");
		if (index < url.length() - 1) {
			return url.substring(0, index + 1);
		} else {
			url = url.substring(0, index);
			return getParentURI(url);
		}
	}

	/**
	 * 获取URL后缀文件名
	 * 
	 * @param url
	 * @return
	 */
	public static String getNameURI(String url) {
		int index = url.lastIndexOf("/");
		if (index < url.length() - 1) {
			return url.substring(index + 1, url.length());
		} else {
			url = url.substring(0, index);
			return getNameURI(url);
		}
	}
}

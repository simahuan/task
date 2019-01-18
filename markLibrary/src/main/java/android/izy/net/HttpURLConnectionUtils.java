package android.izy.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.izy.util.StringUtils;

/**
 * 网络工具类。
 * 
 * @author yangyp
 * @since 1.0, 2014/03/12
 */
public abstract class HttpURLConnectionUtils {

	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded;charset=%s";

	/**
	 * GET用来获取资源，POST用来新建资源（也可以用于更新资源），PUT用来更新资源，DELETE用来删除资源
	 */
	public enum RequestMethod {
		GET, POST, PUT, DELETE
	}

	/**
	 * 执行HTTP GET请求。
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @return 响应字符串
	 * @throws IOException
	 */
	public static String doGet(String url, Map<String, String> params) throws IOException {
		String contentType = String.format(DEFAULT_CONTENT_TYPE, DEFAULT_CHARSET);
		String query = buildQuery(params, contentType);
		return doGet(buildGetUrl(url, query), params, contentType);
	}

	/**
	 * 执行HTTP GET请求。
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param charset
	 *            字符集，如UTF-8, GBK, GB2312
	 * @return 响应字符串
	 * @throws IOException
	 */
	public static String doGet(String url, Map<String, String> params, String contentType) throws IOException {
		return doRequest(url, RequestMethod.GET, contentType, null);
	}

	/**
	 * 执行HTTP POST请求。
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @return 响应字符串
	 * @throws IOException
	 */
	public static String doPost(String url, Map<String, String> params) throws IOException {
		return doPost(url, params, DEFAULT_CHARSET);
	}

	/**
	 * 执行HTTP POST请求。
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param charset
	 *            字符集，如UTF-8, GBK, GB2312
	 * @return 响应字符串
	 * @throws IOException
	 */
	public static String doPost(String url, Map<String, String> params, String charset) throws IOException {
		String contentType = String.format(DEFAULT_CONTENT_TYPE, charset);
		String query = buildQuery(params, charset);
		return doPost(url, contentType, query.getBytes(charset));
	}

	/**
	 * 执行HTTP POST请求。
	 * 
	 * @param url
	 *            请求地址
	 * @param contentType
	 *            请求类型
	 * @param content
	 *            请求字节数组
	 * @return 响应字符串
	 * @throws IOException
	 */
	public static String doPost(String url, String contentType, byte[] entity) throws IOException {
		return doRequest(url, RequestMethod.POST, contentType, entity);
	}

	public static String doRequest(String url, RequestMethod method, String contentType, byte[] entity) throws IOException {
		HttpURLConnection conn = null;
		InputStream inStream = null;
		String responseBody = null;
		try {
			conn = getConnection(new URL(url), method, contentType);
			if (method == RequestMethod.POST && entity != null) {
				OutputStream outStream = conn.getOutputStream();
				outStream.write(entity);
				outStream.close();
			}
			inStream = conn.getInputStream();
			responseBody = getResponseAsString(inStream, getResponseCharset(conn.getContentType()));
		} finally {
			if (inStream != null) {
				inStream.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return responseBody;
	}

	private static HttpURLConnection getConnection(URL url, RequestMethod method, String contentType) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(10 * 1000);
		conn.setReadTimeout(5 * 1000);
		conn.setRequestMethod(method.name());
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
		conn.setRequestProperty("User-Agent", "WebJava");
		conn.setRequestProperty("Content-Type", contentType);
		return conn;
	}

	private static String buildGetUrl(String strUrl, String query) throws IOException {
		if (StringUtils.isNotEmpty(query)) {
			URL url = new URL(strUrl);
			if (StringUtils.isEmpty(url.getQuery())) {
				strUrl += "?" + query;
			} else {
				strUrl += "&" + query;
			}
		}

		return strUrl;
	}

	private static String buildQuery(Map<String, String> params, String charset) throws IOException {
		StringBuilder query = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			boolean hasParam = false;
			Set<Entry<String, String>> entries = params.entrySet();
			for (Entry<String, String> entry : entries) {
				String name = entry.getKey();
				String value = entry.getValue();
				// 忽略参数名或参数值为空的参数
				if (StringUtils.isNotEmpty(name, value)) {
					if (hasParam) {
						query.append("&");
					}

					query.append(name);
					query.append("=");
					query.append(URLEncoder.encode(value, charset));
					hasParam = true;
				}
			}
		}

		return query.toString();
	}

	private static String getResponseCharset(String ctype) {
		String charset = DEFAULT_CHARSET;
		if (!StringUtils.isEmpty(ctype)) {
			String[] params = ctype.split(";");
			for (String param : params) {
				param = param.trim();
				if (param.startsWith("charset")) {
					String[] pair = param.split("=", 2);
					if (pair.length == 2) {
						if (!StringUtils.isEmpty(pair[1])) {
							charset = pair[1].trim();
						}
					}
					break;
				}
			}
		}

		return charset;
	}

	private static String getResponseAsString(InputStream in, String charset) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
		StringWriter writer = new StringWriter();
		char[] chars = new char[256];
		int count = 0;
		while ((count = reader.read(chars)) > 0) {
			writer.write(chars, 0, count);
		}

		return writer.toString();
	}

}
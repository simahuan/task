package android.volley;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class RequestParams extends HashMap<String, String> {

	private static final long serialVersionUID = -5338351418079076260L;

	public final static String APPLICATION_JSON = "application/json";
	public final static String APPLICATION_XML = "application/xml";

	protected final static String LOG_TAG = "RequestParams";
	protected String contentEncoding = HTTP.UTF_8;

	/**
	 * Sets content encoding for return value of {@link #getParamString()} and
	 * {@link #createFormEntity()}
	 * <p>
	 * &nbsp;
	 * </p>
	 * Default encoding is "UTF-8"
	 *
	 * @param encoding
	 *            String constant from {@link org.apache.http.protocol.HTTP}
	 */
	public void setContentEncoding(final String encoding) {
		if (encoding != null) {
			this.contentEncoding = encoding;
		} else {
			Log.d(LOG_TAG, "setContentEncoding called with null attribute");
		}
	}

	/**
	 * Constructs a new empty {@code RequestParams} instance.
	 */
	public RequestParams() {
		this((Map<String, String>) null);
	}

	/**
	 * Constructs a new RequestParams instance containing the key/value string
	 * params from the specified map.
	 *
	 * @param source
	 *            the source key/value string map to add.
	 */
	public RequestParams(Map<String, String> source) {
		if (source != null) {
			for (Map.Entry<String, String> entry : source.entrySet()) {
				put(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Constructs a new RequestParams instance and populate it with a single
	 * initial key/value string param.
	 *
	 * @param key
	 *            the key name for the intial param.
	 * @param value
	 *            the value string for the initial param.
	 */
	public RequestParams(final String key, final String value) {
		put(key, value);
	}

	/**
	 * Constructs a new RequestParams instance and populate it with multiple
	 * initial key/value string param.
	 *
	 * @param keysAndValues
	 *            a sequence of keys and values. Objects are automatically
	 *            converted to Strings (including the value {@code null}).
	 * @throws IllegalArgumentException
	 *             if the number of arguments isn't even.
	 */
	public RequestParams(Object... keysAndValues) {
		int len = keysAndValues.length;
		if (len % 2 != 0)
			throw new IllegalArgumentException("Supplied arguments must be even");
		for (int i = 0; i < len; i += 2) {
			String key = String.valueOf(keysAndValues[i]);
			String val = String.valueOf(keysAndValues[i + 1]);
			put(key, val);
		}
	}

	/**
	 * Adds a int value to the request.
	 *
	 * @param key
	 *            the key name for the new param.
	 * @param value
	 *            the value int for the new param.
	 */
	public void put(String key, int value) {
		put(key, String.valueOf(value));
	}

	/**
	 * Adds a long value to the request.
	 *
	 * @param key
	 *            the key name for the new param.
	 * @param value
	 *            the value long for the new param.
	 */
	public void put(String key, long value) {
		put(key, String.valueOf(value));
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (HashMap.Entry<String, String> entry : entrySet()) {
			if (result.length() > 0)
				result.append("&");

			result.append(entry.getKey());
			result.append("=");
			result.append(entry.getValue());
		}
		return result.toString();
	}

	/**
	 * Returns an HttpEntity containing all request parameters.
	 *
	 * @return HttpEntity resulting HttpEntity to be included along with
	 *         {@link org.apache.http.client.methods.HttpEntityEnclosingRequestBase}
	 * @throws IOException
	 *             if one of the streams cannot be read
	 */
	public HttpEntity getEntity() throws IOException {
		try {
			return new UrlEncodedFormEntity(getParamsList(), contentEncoding);
		} catch (UnsupportedEncodingException e) {
			Log.e(LOG_TAG, "createFormEntity failed", e);
			return null;
		}
	}

	protected List<BasicNameValuePair> getParamsList() {
		List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();
		for (HashMap.Entry<String, String> entry : entrySet()) {
			lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		return lparams;
	}

	protected String getParamString() {
		return URLEncodedUtils.format(getParamsList(), contentEncoding);
	}

	/**
	 * Will encode url, if not disabled, and adds params on the end of it
	 *
	 * @param url
	 *            String with URL, should be valid URL without params
	 * @param params
	 *            RequestParams to be appended on the end of URL
	 * @param shouldEncodeUrl
	 *            whether url should be encoded (replaces spaces with %20)
	 * @return encoded url if requested with params appended if any available
	 */
	public static String getUrlWithQueryString(String url, RequestParams params) {
		if (url == null)
			return null;

		try {
			url = URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// Should not really happen, added just for sake of validity
			Log.e(LOG_TAG, "getUrlWithQueryString encoding URL", e);
		}

		if (params != null) {
			String paramString = params.getParamString().trim();

			// Only add the query string if it isn't empty and it
			// isn't equal to '?'.
			if (!paramString.equals("") && !paramString.equals("?")) {
				url += url.contains("?") ? "&" : "?";
				url += paramString;
			}
		}

		return url;
	}
}
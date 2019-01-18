package android.izy.net;

import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public abstract class HttpClientUtils {

	private static final int SOCKET_OPERATION_TIMEOUT = 10 * 1000;

	/**
	 * 创建HttpClient
	 * 
	 * @param userAgent
	 * @param timeout
	 *            连接读取超时
	 * @param retryCount
	 *            请求出错重试次数
	 * @return
	 */
	public static DefaultHttpClient newHttpClient(String userAgent, int timeout, int retryCount) {
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setStaleCheckingEnabled(params, false);

		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpClientParams.setRedirecting(params, false);
		HttpProtocolParams.setUserAgent(params, userAgent);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		// SSLSessionCache sessionCache = context == null ? null : new
		// SSLSessionCache(context);
		// schemeRegistry.register(new Scheme("https",
		// SSLCertificateSocketFactory.getHttpSocketFactory(SOCKET_OPERATION_TIMEOUT,
		// sessionCache), 443));

		ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);

		DefaultHttpClient client = new DefaultHttpClient(manager, params);
		client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(retryCount, true));
		return client;
	}

	public static DefaultHttpClient newHttpClient() {
		return newHttpClient("WebAndroid", SOCKET_OPERATION_TIMEOUT, 0);
	}

}

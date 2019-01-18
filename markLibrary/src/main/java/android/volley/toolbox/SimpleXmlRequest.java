package android.volley.toolbox;

import java.util.Map;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.volley.RequestListener;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

public abstract class SimpleXmlRequest<T> extends Request<T> implements RequestListener<T> {

	private final Serializer mSerializer = new Persister();
	private final Class<T> mClazz;
	private final Map<String, String> mHeaders;

	public SimpleXmlRequest(String url, Class<T> clazz) {
		this(Method.GET, url, clazz, null);
	}

	public SimpleXmlRequest(int method, String url, Class<T> clazz) {
		this(method, url, clazz, null);
	}

	public SimpleXmlRequest(int method, String url, Class<T> clazz, Map<String, String> headers) {
		super(method, url, null);
		this.mClazz = clazz;
		this.mHeaders = headers;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeaders != null ? mHeaders : super.getHeaders();
	}

	@Override
	protected void deliverResponse(T response) {
		onResponse(response);
	}

	@Override
	public void deliverError(VolleyError error) {
		onErrorResponse(error);
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String xml = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(mSerializer.read(mClazz, xml), HttpHeaderParser.parseCacheHeaders(response));
		} catch (Exception e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {

	}
}

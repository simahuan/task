package android.volley.toolbox;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import android.volley.RequestListener;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public abstract class GsonRequest<T> extends Request<T> implements RequestListener<T> {

	private final Gson mGson = new Gson();
	private final Class<T> mClazz;
	private final Map<String, String> mHeaders;

	public GsonRequest(String url, Class<T> clazz) {
		this(Method.GET, url, clazz, null);
	}

	public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> headers) {
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
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(mGson.fromJson(json, mClazz), HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {

	}
}

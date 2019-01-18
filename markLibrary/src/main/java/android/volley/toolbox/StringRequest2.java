package android.volley.toolbox;

import android.volley.RequestListener;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public abstract class StringRequest2 extends StringRequest implements RequestListener<String> {

	public StringRequest2(String url) {
		this(Method.GET, url);
	}

	public StringRequest2(int method, String url) {
		super(method, url, null, null);
	}

	@Override
	protected void deliverResponse(String response) {
		onResponse(response);
	}

	@Override
	public void deliverError(VolleyError error) {
		deliverError(error);
	}

	@Override
	public void onErrorResponse(VolleyError error) {

	}
}

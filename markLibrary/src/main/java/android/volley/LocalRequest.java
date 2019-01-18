package android.volley;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;

public abstract class LocalRequest<T> extends Request<T> {

	public LocalRequest(String url, ErrorListener listener) {
		super(url, listener);
	}

}

package android.volley.toolbox;

import android.graphics.Bitmap;
import android.volley.RequestListener;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;

public abstract class VideoRequest extends Request<Bitmap> implements RequestListener<Bitmap> {

	public VideoRequest(int method, String url, ErrorListener listener) {
		super(method, url, listener);
	}

	@Override
	protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void deliverResponse(Bitmap response) {
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

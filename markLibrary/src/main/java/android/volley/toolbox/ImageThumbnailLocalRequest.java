package android.volley.toolbox;

import android.graphics.Bitmap;
import android.volley.LocalRequest;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;

/**
 * 本地缩略图
 * 
 * @author yangyp
 * @version 1.0, 2015年1月15日 上午10:04:26
 */
public class ImageThumbnailLocalRequest extends LocalRequest<Bitmap> {

	public ImageThumbnailLocalRequest(String url, ErrorListener listener) {
		super(url, listener);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void deliverResponse(Bitmap response) {
		// TODO Auto-generated method stub

	}

}

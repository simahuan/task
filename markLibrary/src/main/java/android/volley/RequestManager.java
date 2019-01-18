package android.volley;

import android.content.Context;
import android.izy.os.EnvironmentUtils;
import android.util.DisplayMetrics;
import android.volley.toolbox.RequestLoader;
import android.volley.toolbox.RequestLoader.RequestContainer;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

public class RequestManager {

	private static RequestQueue mRequestQueue;
	private static ImageLoader mImageLoader;
	private static RequestLoader mRequestLoader;

	private RequestManager() {
		// no instances
	}

	public static void init(Context context) {
		// mRequestQueue = Volley.newRequestQueue(context);
		mRequestQueue = VolleyUtils.newRequestQueueInDisk(context);
		DisplayMetrics metrics = EnvironmentUtils.getResolution(context);
		mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(context), metrics.widthPixels, metrics.heightPixels);
		mRequestLoader = new RequestLoader(mRequestQueue);
	}

	public static void sendRequest(Request<?> request, Object tag) {
		addRequest(request, tag);
	}

	public static void addRequest(Request<?> request, Object tag) {
		if (tag != null) {
			request.setTag(tag);
		}
		mRequestQueue.add(request);
	}

	public static void cancelAll(Object tag) {
		mRequestQueue.cancelAll(tag);
	}

	public static ImageContainer imageLoader(ImageView view, String url, int defaultImageResId) {
		return imageLoader(view, url, defaultImageResId, defaultImageResId);
	}

	public static ImageContainer imageLoader(ImageView view, String url, int defaultImageResId, int errorImageResId) {
		return imageLoader(view, url, defaultImageResId, errorImageResId, 0, 0);
	}

	public static ImageContainer imageLoader(ImageView view, String url, int defaultImageResId, int errorImageResId, int maxWidth, int maxHeight) {
		ImageListener listener = ImageLoader.getImageListener(view, defaultImageResId, errorImageResId);
		return imageLoader(url, listener, maxWidth, maxHeight);
	}

	public static ImageContainer imageLoader(String url, ImageListener listener) {
		return imageLoader(url, listener, 0, 0);
	}

	public static ImageContainer imageLoader(String url, ImageListener listener, int maxWidth, int maxHeight) {
		return mImageLoader.get(url, listener, maxWidth, maxHeight);
	}

	/**
	 * GET发送方式
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 * @return
	 */
	public static RequestContainer get(String url, RequestParams params, Request<?> request) {
		String newUrl = RequestParams.getUrlWithQueryString(url, params);
		return sendRequest(newUrl, params, null, request, Method.GET);
	}

	/**
	 * POST发送方式
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 * @return
	 */
	public static RequestContainer post(String url, RequestParams params, Request<?> request) {
		return post(url, params, null, request);
	}

	public static RequestContainer post(String url, RequestParams params, String contentType, Request<?> request) {
		return sendRequest(url, params, contentType, request, Method.POST);
	}

	public static RequestContainer sendRequest(String url, RequestParams params, String contentType, Request<?> request, int method) {
		request.setMethod(method);
		switch (method) {
		case Method.GET:
		case Method.DELETE:
		case Method.HEAD:
		case Method.OPTIONS:
		case Method.TRACE:
			String newUrl = RequestParams.getUrlWithQueryString(url, params);
			request.setUrl(newUrl);
			break;
		case Method.POST:
		case Method.PUT:
		case Method.PATCH:
			request.setUrl(url);
			request.setParams(params);
			break;
		case Method.DEPRECATED_GET_OR_POST:
		default:
			throw new IllegalStateException("Unknown request method.");
		}

		if (contentType != null) {
			// request.setContentType(contentType);
		}

		return mRequestLoader.sendRequest(request, url);
	}

}

package android.volley.toolbox;

import android.os.Looper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

public class RequestLoader {

	private final RequestQueue mRequestQueue;

	public RequestLoader(RequestQueue queue) {
		mRequestQueue = queue;
	}

	public <T> RequestContainer sendRequest(Request<?> request, String requestUrl) {
		// only fulfill requests that were initiated from the main thread.
		throwIfNotOnMainThread();

		// request.setTag(requestUrl);
		mRequestQueue.add(request);
		return new RequestContainer(request, requestUrl);
	}

	private void throwIfNotOnMainThread() {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new IllegalStateException("ImageLoader must be invoked from the main thread.");
		}
	}

	public class RequestContainer {

		private Request<?> mRequest;
		private final String mRequestUrl;

		/**
		 * Constructs a BitmapContainer object.
		 * 
		 * @param bitmap
		 *            The final bitmap (if it exists).
		 * @param requestUrl
		 *            The requested URL for this container.
		 */
		public RequestContainer(Request<?> request, String requestUrl) {
			mRequest = request;
			mRequestUrl = requestUrl;
		}

		/**
		 * Releases interest in the in-flight request (and cancels it if no one
		 * else is listening).
		 */
		public void cancelRequest() {
			// mRequestQueue.cancelAll(mRequestUrl);
			mRequest.cancel();
		}

		/**
		 * Returns the requested URL for this container.
		 */
		public String getRequestUrl() {
			return mRequestUrl;
		}
	}
}

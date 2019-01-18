package android.volley.toolbox;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.cookie.DateUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.volley.VolleyUtils;
import android.webkit.URLUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.ByteArrayPool;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.PoolingByteArrayOutputStream;

/**
 * A network performing Volley requests over an {@link HttpStack}.
 */
public class BasicNetwork2 extends BasicNetwork {

	private static int SLOW_REQUEST_THRESHOLD_MS = 3000;

	private static int DEFAULT_POOL_SIZE = 4096;

	/**
	 * @param httpStack
	 *            HTTP stack to be used
	 */
	public BasicNetwork2(HttpStack httpStack) {
		super(httpStack, new ByteArrayPool(DEFAULT_POOL_SIZE));
	}

	@Override
	public NetworkResponse performRequest(Request<?> request) throws VolleyError {
		if (URLUtil.isNetworkUrl(request.getUrl())) {
			return performRequestNetwork(request);
		} else {
			if (request instanceof ImageRequest2) {
				File file = new File(request.getUrl());
				if (file.exists()) {
					return new NetworkResponse(createImageThumbnail((ImageRequest2) request, file));
				}
			}
		}

		throw new NoConnectionError();
	}

	public NetworkResponse performRequestNetwork(Request<?> request) throws VolleyError {
		long requestStart = SystemClock.elapsedRealtime();
		while (true) {
			HttpResponse httpResponse = null;
			byte[] responseContents = null;
			Map<String, String> responseHeaders = new HashMap<String, String>();
			try {
				// Gather headers.
				Map<String, String> headers = new HashMap<String, String>();
				addCacheHeaders(headers, request.getCacheEntry());
				httpResponse = mHttpStack.performRequest(request, headers);
				StatusLine statusLine = httpResponse.getStatusLine();
				int statusCode = statusLine.getStatusCode();

				responseHeaders = convertHeaders(httpResponse.getAllHeaders());
				// Handle cache validation.
				if (statusCode == HttpStatus.SC_NOT_MODIFIED) {
					return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED, request.getCacheEntry() == null ? null : request.getCacheEntry().data,
							responseHeaders, true);
				}

				// Some responses such as 204s do not have content. We must
				// check.
				if (httpResponse.getEntity() != null) {
					responseContents = entityToBytes(request, httpResponse.getEntity());
				} else {
					// Add 0 byte response as a way of honestly representing a
					// no-content request.
					responseContents = new byte[0];
				}

				// if the request is slow, log it.
				long requestLifetime = SystemClock.elapsedRealtime() - requestStart;
				logSlowRequests(requestLifetime, request, responseContents, statusLine);

				if (statusCode < 200 || statusCode > 299) {
					throw new IOException();
				}
				return new NetworkResponse(statusCode, responseContents, responseHeaders, false);
			} catch (SocketTimeoutException e) {
				attemptRetryOnException("socket", request, new TimeoutError());
			} catch (ConnectTimeoutException e) {
				attemptRetryOnException("connection", request, new TimeoutError());
			} catch (MalformedURLException e) {
				throw new RuntimeException("Bad URL " + request.getUrl(), e);
			} catch (IOException e) {
				int statusCode = 0;
				NetworkResponse networkResponse = null;
				if (httpResponse != null) {
					statusCode = httpResponse.getStatusLine().getStatusCode();
				} else {
					throw new NoConnectionError(e);
				}
				VolleyLog.e("Unexpected response code %d for %s", statusCode, request.getUrl());
				if (responseContents != null) {
					networkResponse = new NetworkResponse(statusCode, responseContents, responseHeaders, false);
					if (statusCode == HttpStatus.SC_UNAUTHORIZED || statusCode == HttpStatus.SC_FORBIDDEN) {
						attemptRetryOnException("auth", request, new AuthFailureError(networkResponse));
					} else {
						// TODO: Only throw ServerError for 5xx status codes.
						throw new ServerError(networkResponse);
					}
				} else {
					throw new NetworkError(networkResponse);
				}
			}
		}
	}

	/**
	 * Logs requests that took over SLOW_REQUEST_THRESHOLD_MS to complete.
	 */
	private void logSlowRequests(long requestLifetime, Request<?> request, byte[] responseContents, StatusLine statusLine) {
		if (DEBUG || requestLifetime > SLOW_REQUEST_THRESHOLD_MS) {
			VolleyLog.d("HTTP response for request=<%s> [lifetime=%d], [size=%s], " + "[rc=%d], [retryCount=%s]", request, requestLifetime,
					responseContents != null ? responseContents.length : "null", statusLine.getStatusCode(), request.getRetryPolicy().getCurrentRetryCount());
		}
	}

	/**
	 * Attempts to prepare the request for a retry. If there are no more
	 * attempts remaining in the request's retry policy, a timeout exception is
	 * thrown.
	 * 
	 * @param request
	 *            The request to use.
	 */
	private static void attemptRetryOnException(String logPrefix, Request<?> request, VolleyError exception) throws VolleyError {
		RetryPolicy retryPolicy = request.getRetryPolicy();
		int oldTimeout = request.getTimeoutMs();

		try {
			retryPolicy.retry(exception);
		} catch (VolleyError e) {
			request.addMarker(String.format("%s-timeout-giveup [timeout=%s]", logPrefix, oldTimeout));
			throw e;
		}
		request.addMarker(String.format("%s-retry [timeout=%s]", logPrefix, oldTimeout));
	}

	private void addCacheHeaders(Map<String, String> headers, Cache.Entry entry) {
		// If there's no cache entry, we're done.
		if (entry == null) {
			return;
		}

		if (entry.etag != null) {
			headers.put("If-None-Match", entry.etag);
		}

		if (entry.serverDate > 0) {
			Date refTime = new Date(entry.serverDate);
			headers.put("If-Modified-Since", DateUtils.formatDate(refTime));
		}
	}

	/**
	 * Reads the contents of HttpEntity into a byte[].
	 * 
	 * @param request
	 */
	private byte[] entityToBytes(Request<?> request, HttpEntity entity) throws IOException, ServerError {
		if (request instanceof ImageRequest2) {
			// 当网络图片数据大于1M时使用硬盘缓存
			if (entity.getContentLength() > ImageRequest2.IMAGE_MAX_CACHE_DATA) {
				File cacheTempFile = writeToTempFile(request, entity);
				return createImageThumbnail((ImageRequest2) request, cacheTempFile);
			}
		}

		PoolingByteArrayOutputStream bytes = new PoolingByteArrayOutputStream(mPool, (int) entity.getContentLength());
		byte[] buffer = null;
		try {
			InputStream in = entity.getContent();
			if (in == null) {
				throw new ServerError();
			}

			// 主要判断getContentLength为-1的情况
			if (request instanceof ImageRequest2 && entity.getContentLength() == -1) {
				buffer = mPool.getBuf(DEFAULT_POOL_SIZE);
				int count;
				while ((count = in.read(buffer)) != -1) {
					bytes.write(buffer, 0, count);

					// 当网络图片数据大于1M时使用硬盘缓存
					if (buffer.length > ImageRequest2.IMAGE_MAX_CACHE_DATA) {
						File cacheTempFile = writeToTempFile(request, bytes.toByteArray(), in);
						return createImageThumbnail((ImageRequest2) request, cacheTempFile);
					}
				}
			} else {
				buffer = mPool.getBuf(DEFAULT_POOL_SIZE);
				int count;
				while ((count = in.read(buffer)) != -1) {
					bytes.write(buffer, 0, count);
				}
			}

			return bytes.toByteArray();
		} finally {
			try {
				// Close the InputStream and release the resources by
				// "consuming the content".
				entity.consumeContent();
			} catch (IOException e) {
				// This can happen if there was an exception above that left the
				// entity in
				// an invalid state.
				VolleyLog.v("Error occured when calling consumingContent");
			}
			mPool.returnBuf(buffer);
			bytes.close();
		}
	}

	private byte[] createImageThumbnail(ImageRequest2 request, File cacheTempFile) {
		ByteArrayOutputStream bytesOut = null;
		try {
			// Bitmap bitmap =
			// MediaThumbnailUtils.createImageThumbnail(cacheTempFile.getPath(),
			// Images.Thumbnails.MINI_KIND);
			Bitmap bitmap = null;
			BitmapFactory.Options decodeOptions = new BitmapFactory.Options();

			if (request.hasBounds()) {
				decodeOptions.inPreferredConfig = Config.RGB_565;
				bitmap = BitmapFactory.decodeFile(cacheTempFile.getPath(), decodeOptions);
			} else {
				// If we have to resize this image, first get the natural
				// bounds.
				decodeOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(cacheTempFile.getPath(), decodeOptions);
				int actualWidth = decodeOptions.outWidth;
				int actualHeight = decodeOptions.outHeight;

				// Then compute the dimensions we would ideally like to decode
				// to.
				int desiredWidth = ImageRequest2.getResizedDimension(request.getMaxWidth(), request.getMaxHeight(), actualWidth, actualHeight);
				int desiredHeight = ImageRequest2.getResizedDimension(request.getMaxHeight(), request.getMaxWidth(), actualHeight, actualWidth);

				// Decode to the nearest power of two scaling factor.
				decodeOptions.inJustDecodeBounds = false;
				// TODO(ficus): Do we need this or is it okay since API 8
				// doesn't support it?
				// decodeOptions.inPreferQualityOverSpeed =
				// PREFER_QUALITY_OVER_SPEED;
				decodeOptions.inSampleSize = ImageRequest2.findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
				Bitmap tempBitmap = BitmapFactory.decodeFile(cacheTempFile.getPath(), decodeOptions);

				// If necessary, scale down to the maximal acceptable size.
				if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {
					bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
					tempBitmap.recycle();
				} else {
					bitmap = tempBitmap;
				}
			}

			bytesOut = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, bytesOut);
			cacheTempFile.delete();
			return bytesOut.toByteArray();
		} finally {
			try {
				bytesOut.close();
			} catch (IOException ex) {
			}
		}

	}

	/**
	 * 将大数据写入文件
	 * 
	 * @param request
	 * @param entity
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ServerError
	 */
	private File writeToTempFile(Request<?> request, HttpEntity entity) throws FileNotFoundException, IOException, ServerError {
		InputStream in = entity.getContent();
		if (in == null) {
			throw new ServerError();
		}
		return writeToTempFile(request, null, in);
	}

	/**
	 * 将数据写入临时文件
	 * 
	 * @param request
	 * @param bytesDate
	 * @param in
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private File writeToTempFile(Request<?> request, byte[] bytesDate, InputStream in) throws FileNotFoundException, IOException {
		File file = new File(VolleyUtils.getCacheDir(), getFilenameForKey(request.getUrl()));
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
			if (bytesDate != null) {
				out.write(bytesDate);
			}
			byte[] buffer = new byte[DEFAULT_POOL_SIZE];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.flush();
		} finally {
			try {
				out.close();
			} catch (IOException ex) {
			}
		}
		return file;
	}

	private static String getFilenameForKey(String url) {
		int firstHalfLength = url.length() / 2;
		String localFilename = String.valueOf(url.substring(0, firstHalfLength).hashCode());
		localFilename += String.valueOf(url.substring(firstHalfLength).hashCode());
		return localFilename + "~temp";
	}

	/**
	 * Converts Headers[] to Map<String, String>.
	 */
	private static Map<String, String> convertHeaders(Header[] headers) {
		Map<String, String> result = new HashMap<String, String>();
		for (int i = 0; i < headers.length; i++) {
			result.put(headers[i].getName(), headers[i].getValue());
		}
		return result;
	}
}

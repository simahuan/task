/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.volley.toolbox;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.volley.RequestListener;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

/**
 * A canned request for getting an image at a given URL and calling back with a
 * decoded Bitmap.
 */
public abstract class ImageRequest2 extends ImageRequest implements RequestListener<Bitmap> {

	/** 支持的最大内存缓存数据长度 */
	public static final int IMAGE_MAX_CACHE_DATA = 300 * 1024;

	private final Config mDecodeConfig;
	private final int mMaxWidth;
	private final int mMaxHeight;

	public ImageRequest2(String url) {
		this(url, 0, 0, Config.RGB_565);
	}

	public ImageRequest2(String url, int maxWidth, int maxHeight, Config decodeConfig) {
		super(url, null, maxWidth, maxHeight, decodeConfig, null);
		mDecodeConfig = decodeConfig;
		mMaxWidth = maxWidth;
		mMaxHeight = maxHeight;
	}

	public Config getDecodeConfig() {
		return mDecodeConfig;
	}

	public int getMaxWidth() {
		return mMaxWidth;
	}

	public int getMaxHeight() {
		return mMaxHeight;
	}

	public boolean hasBounds() {
		return !(mMaxWidth == 0 && mMaxHeight == 0);
	}

	@Override
	protected void deliverResponse(Bitmap response) {
		onResponse(response);
	}

	@Override
	public void deliverError(VolleyError error) {
		onErrorResponse(error);
	}

	@Override
	public void onErrorResponse(VolleyError error) {

	}

	static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary, int actualSecondary) {
		// If no dominant value at all, just return the actual.
		if (maxPrimary == 0 && maxSecondary == 0) {
			return actualPrimary;
		}

		// If primary is unspecified, scale primary to match secondary's scaling
		// ratio.
		if (maxPrimary == 0) {
			double ratio = (double) maxSecondary / (double) actualSecondary;
			return (int) (actualPrimary * ratio);
		}

		if (maxSecondary == 0) {
			return maxPrimary;
		}

		double ratio = (double) actualSecondary / (double) actualPrimary;
		int resized = maxPrimary;
		if (resized * ratio > maxSecondary) {
			resized = (int) (maxSecondary / ratio);
		}
		return resized;
	}

	static int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
		double wr = (double) actualWidth / desiredWidth;
		double hr = (double) actualHeight / desiredHeight;
		double ratio = Math.min(wr, hr);
		float n = 1.0f;
		while ((n * 2) <= ratio) {
			n *= 2;
		}

		return (int) n;
	}
}

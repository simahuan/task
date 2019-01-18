package android.izy.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * WebView Activity基类
 * 
 * @author yangyp
 * @version 1.0, 2014-7-8 下午1:03:30
 */
public abstract class WebViewActivity extends ActivitySupport {

	protected static final String TAG = WebViewActivity.class.getSimpleName();

	private Activity activity;
	private WebView webView;

	// ***************************************
	// Activity methods
	// ***************************************
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
		webView = new WebView(this);
		setContentView(webView);

		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				// activity.setTitle("Loading...");
				activity.setProgress(progress * 100);
				if (progress == 100) {
					// activity.setTitle(R.string.app_name);
				}
			}
		});
	}

	// ***************************************
	// Protected methods
	// ***************************************
	protected WebView getWebView() {
		return webView;
	}

}

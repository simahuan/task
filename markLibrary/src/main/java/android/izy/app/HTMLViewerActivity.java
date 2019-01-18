package android.izy.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class HTMLViewerActivity extends ActivitySupport {

	public static final String BASE_URI = "content://com.android.htmlfileprovider";
	private WebView mWebView;
	static final int MAXFILESIZE = 8096;
	static final String LOGTAG = "HTMLViewerActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Call createInstance() explicitly. createInstance() is called in
		// BrowserFrame by WebView. As it is called in WebCore thread, it can
		// happen after onResume() is called. To use getInstance() in onResume,
		// createInstance() needs to be called first.
		CookieSyncManager.createInstance(this);

		requestWindowFeature(Window.FEATURE_PROGRESS);

		mWebView = new WebView(this);
		setContentView(mWebView);

		// Setup callback support for title and progress bar
		mWebView.setWebChromeClient(new WebChrome());

		// Configure the webview
		WebSettings s = mWebView.getSettings();
		s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		s.setUseWideViewPort(true);
		s.setSavePassword(false);
		s.setSaveFormData(false);
		s.setBlockNetworkLoads(true);

		// Javascript is purposely disabled, so that nothing can be
		// automatically run.
		s.setJavaScriptEnabled(false);

		// Restore a webview if we are meant to restore
		if (savedInstanceState != null) {
			mWebView.restoreState(savedInstanceState);
		} else {
			// Check the intent for the content to view
			Intent intent = getIntent();
			if (intent.getData() != null) {
				Uri uri = intent.getData();
				String contentUri = "file".equals(uri.getScheme()) ? BASE_URI + uri.getEncodedPath() : uri.toString();
				String intentType = intent.getType();
				if (intentType != null) {
					contentUri += "?" + intentType;
				}
				mWebView.loadUrl(contentUri);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		CookieSyncManager.getInstance().startSync();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// the default implementation requires each view to have an id. As the
		// browser handles the state itself and it doesn't use id for the views,
		// don't call the default implementation. Otherwise it will trigger the
		// warning like this, "couldn't save which view has focus because the
		// focused view XXX has no id".
		mWebView.saveState(outState);
	}

	@Override
	protected void onStop() {
		super.onStop();

		CookieSyncManager.getInstance().stopSync();
		mWebView.stopLoading();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWebView.destroy();
	}

	class WebChrome extends WebChromeClient {

		@Override
		public void onReceivedTitle(WebView view, String title) {
			HTMLViewerActivity.this.setTitle(title);
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			getWindow().setFeatureInt(Window.FEATURE_PROGRESS, newProgress * 100);
			if (newProgress == 100) {
				CookieSyncManager.getInstance().sync();
			}
		}
	}

}

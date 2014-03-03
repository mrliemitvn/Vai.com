package org.vai.com.activity;

import org.vai.com.R;
import org.vai.com.VaiApplication;
import org.vai.com.utils.Consts;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * This class display all comment of content.
 * Include showing advertising.
 */
public class CommentPageActivity extends SherlockActivity {

	/* Display AdView (Google admob). */
	private AdView adView;

	/* Display progress bar when WebView is loading. */
	private ProgressBar mPrgLoading;

	/* WebView display conference comment. */
	private WebView mWebView;

	/**
	 * Initialize view.
	 */
	private void initialize() {
		/* Get conference id is passed in intent. */
		String id = "";
		if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(Consts.JSON_ID)) {
			id = getIntent().getExtras().getString(Consts.JSON_ID);
		}

		/* Create comment link. */
		String linkLoadComment = Consts.URLConstants.URL_LOAD_COMMENT.replace(Consts.ID_PLACE_HOLDER, id);

		/* Initialize view. */
		mPrgLoading = (ProgressBar) findViewById(R.id.prgLoading);
		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setDomStorageEnabled(true);

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				/*
				 * Show progress bar when webview is loading.
				 * Hide it when complete.
				 */
				if (newProgress < 100 && mPrgLoading.getVisibility() == View.GONE) {
					mPrgLoading.setVisibility(View.VISIBLE);
				}
				mPrgLoading.setProgress(newProgress);
				if (newProgress >= 100) mPrgLoading.setVisibility(View.GONE);
			}
		});
		// Set webview client, web page will not call browser to show.
		mWebView.setWebViewClient(new WebViewClient());
		/* Load comment link. */
		mWebView.loadUrl(linkLoadComment);

		// For admob.
		adView = (AdView) this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest();
		adView.loadAd(adRequest);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_comment_page);

		/* Enable back button on action bar and set back icon. */
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(R.drawable.icon_back);

		initialize(); // Initialize view.
	}

	@Override
	protected void onStart() {
		super.onStart();
		// For google anlytics.
		EasyTracker.getInstance(this).activityStart(this);
		VaiApplication.getGaTracker().set(Fields.SCREEN_NAME, this.getClass().getName());
		VaiApplication.getGaTracker().send(MapBuilder.createAppView().build());
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Stop google analytics for this activity.
	}

	@Override
	public void onDestroy() {
		if (adView != null) adView.destroy(); // Destroy AdView.
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) { // Click back button, finish activity.
		case android.R.id.home:
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}

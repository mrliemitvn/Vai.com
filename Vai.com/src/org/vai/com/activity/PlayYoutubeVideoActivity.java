package org.vai.com.activity;

import org.vai.com.R;
import org.vai.com.VaiApplication;
import org.vai.com.utils.Consts;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * This class play youtube video.
 */
public class PlayYoutubeVideoActivity extends Activity {

	/* Display AdView (Google admob). */
	private AdView adView;

	/* Display progress bar when WebView is loading. */
	private ProgressBar mPrgLoading;

	/* WebView play youtube video. */
	private WebView mWebView;

	/* Youtube video url. */
	private String linkYoutubeVideo;

	/**
	 * Initialize view.
	 */
	private void initialize() {
		// For admob.
		adView = (AdView) this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest();
		adView.loadAd(adRequest);

		mPrgLoading = (ProgressBar) findViewById(R.id.prgLoading);
		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setPluginState(PluginState.ON);
		mWebView.getSettings().setDomStorageEnabled(true);

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				/*
				 * Show progress bar when WebView is loading.
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

		/* Get youtube video id and set youtube video url. */
		String id = "";
		if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(Consts.JSON_ID)) {
			id = getIntent().getExtras().getString(Consts.JSON_ID);
		}
		linkYoutubeVideo = "http://www.youtube.com/embed/" + id + "?autoplay=1&vq=small";

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_youtube_video);

		initialize(); // Initialize view.
	}

	@Override
	protected void onResume() {
		super.onResume();
		mWebView.loadUrl(linkYoutubeVideo); // Load youtube video.
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
	protected void onPause() {
		mWebView.loadUrl(""); // Load blank page when finish this activity.
		super.onPause();
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
}

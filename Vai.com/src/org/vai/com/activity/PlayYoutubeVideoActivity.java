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

public class PlayYoutubeVideoActivity extends Activity {

	private AdView adView;

	private ProgressBar mPrgLoading;
	private WebView mWebView;
	private String linkLoadComment;

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
				if (newProgress < 100 && mPrgLoading.getVisibility() == View.GONE) {
					mPrgLoading.setVisibility(View.VISIBLE);
				}
				mPrgLoading.setProgress(newProgress);
				if (newProgress >= 100) mPrgLoading.setVisibility(View.GONE);
			}
		});
		// Set webview client, web page will not call browser to show.
		mWebView.setWebViewClient(new WebViewClient());

		String id = "";
		if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(Consts.JSON_ID)) {
			id = getIntent().getExtras().getString(Consts.JSON_ID);
		}
		linkLoadComment = "http://www.youtube.com/embed/" + id + "?autoplay=1&vq=small";

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_youtube_video);

		initialize();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mWebView.loadUrl(linkLoadComment);
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
		mWebView.loadUrl("");
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	public void onDestroy() {
		if (adView != null) adView.destroy();
		super.onDestroy();
	}
}

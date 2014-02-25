package org.vai.com.activity;

import org.vai.com.R;
import org.vai.com.VaiApplication;
import org.vai.com.utils.Consts;
import org.vai.com.views.TouchImageView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ImageViewDetailActivity extends SherlockActivity {

	private AdView adView;

	private String urlImage = "";
	private TouchImageView mImgSaveImage;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions imgSquareOptions = new DisplayImageOptions.Builder()
			.showStubImage(R.color.image_loading).showImageForEmptyUri(R.color.image_loading)
			.showImageOnFail(R.color.image_loading).cacheInMemory(true).cacheOnDisc(true)
			.displayer(new FadeInBitmapDisplayer(300)).bitmapConfig(Bitmap.Config.RGB_565).build();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_view_detail);

		mImgSaveImage = (TouchImageView) findViewById(R.id.imgSaveImage);

		urlImage = getIntent().getExtras().getString(Consts.IMAGE_URL);
		imageLoader.displayImage(urlImage, mImgSaveImage, imgSquareOptions);

		mImgSaveImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// For admob.
		adView = (AdView) this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
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
		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	protected void onPause() {
		adView.pause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		adView.resume();
	}

	@Override
	protected void onDestroy() {
		adView.destroy();
		super.onDestroy();
	}
}

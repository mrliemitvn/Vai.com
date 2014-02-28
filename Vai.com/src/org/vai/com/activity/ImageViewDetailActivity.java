package org.vai.com.activity;

import org.vai.com.R;
import org.vai.com.VaiApplication;
import org.vai.com.utils.Consts;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.imagezoom.ImageAttacher;
import com.imagezoom.ImageAttacher.OnMatrixChangedListener;
import com.imagezoom.ImageAttacher.OnPhotoTapListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageViewDetailActivity extends SherlockActivity {

	private AdView adView;

	private String urlImage = "";
	private int mScreenWidth;
	private boolean isScale = false;
	private boolean isLoaded = false;
	private ImageAttacher mAttacher;
	private ImageView mImgSaveImage;
	private ImageView mImgSaveImage1;
	private ProgressBar mPbLoadingImage;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions imgSquareOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.color.transparent).showImageForEmptyUri(R.color.image_loading)
			.showImageOnFail(R.color.image_loading).cacheInMemory(true).cacheOnDisc(true)
			.displayer(new FadeInBitmapDisplayer(300)).bitmapConfig(Bitmap.Config.RGB_565).build();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_view_detail);

		mScreenWidth = getResources().getDisplayMetrics().widthPixels;

		mImgSaveImage = (ImageView) findViewById(R.id.imgSaveImage);
		mImgSaveImage1 = (ImageView) findViewById(R.id.imgSaveImage1);
		mPbLoadingImage = (ProgressBar) findViewById(R.id.pbLoadingImage);

		// TODO: working with zoom image.
		// usingSimpleImage(mImgSaveImage);

		urlImage = getIntent().getExtras().getString(Consts.IMAGE_URL);
		imageLoader.displayImage(urlImage, mImgSaveImage, imgSquareOptions, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				mPbLoadingImage.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				isLoaded = true;
				mPbLoadingImage.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				isLoaded = true;
				mPbLoadingImage.setVisibility(View.GONE);

				int imgHeight = mScreenWidth;
				if (loadedImage.getWidth() > 0) {
					imgHeight = mScreenWidth * loadedImage.getHeight() / loadedImage.getWidth();
				}

				Bitmap bitmap1 = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(),
						loadedImage.getHeight() / 2);
				Bitmap bitmap2 = Bitmap.createBitmap(loadedImage, 0, loadedImage.getHeight() / 2,
						loadedImage.getWidth(), loadedImage.getHeight() / 2);

				mImgSaveImage.getLayoutParams().height = imgHeight / 2;
				mImgSaveImage.setImageBitmap(bitmap1);
				mImgSaveImage1.getLayoutParams().height = imgHeight / 2;
				mImgSaveImage1.setImageBitmap(bitmap2);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				isLoaded = true;
				mPbLoadingImage.setVisibility(View.GONE);
			}
		});

		mImgSaveImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mImgSaveImage1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// For admob.
		adView = (AdView) this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest();
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
	protected void onDestroy() {
		if (adView != null) adView.destroy();
		super.onDestroy();
	}

	public void usingSimpleImage(ImageView imageView) {
		mAttacher = new ImageAttacher(imageView);
		ImageAttacher.MAX_ZOOM = 2.0f; // Double the current Size
		ImageAttacher.MIN_ZOOM = 0.5f; // Half the current Size
		MatrixChangeListener mMaListener = new MatrixChangeListener();
		mAttacher.setOnMatrixChangeListener(mMaListener);
		PhotoTapListener mPhotoTap = new PhotoTapListener();
		mAttacher.setOnPhotoTapListener(mPhotoTap);
	}

	private class PhotoTapListener implements OnPhotoTapListener {
		@Override
		public void onPhotoTap(View view, float x, float y) {
		}
	}

	private class MatrixChangeListener implements OnMatrixChangedListener {

		@Override
		public void onMatrixChanged(RectF rect) {
			if (!isLoaded) return;
			if (!isScale) {
				if ((mScreenWidth - (int) rect.width()) > 10) {
					float scale = mAttacher.getScale();
					scale = (float) (scale + 0.3);
					mAttacher.zoomTo(scale, 0, 0);
				} else {
					isScale = true;
					mAttacher.MAX_ZOOM = mAttacher.getScale() + 1;
				}
			}
		}
	}
}

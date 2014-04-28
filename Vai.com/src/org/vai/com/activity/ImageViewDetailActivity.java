package org.vai.com.activity;

import org.vai.com.R;
import org.vai.com.VaiApplication;
import org.vai.com.utils.Consts;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * This class display full image detail.
 */
public class ImageViewDetailActivity extends SherlockActivity implements OnClickListener {

	/* Display AdView (Google admob). */
	private AdView adView;

	/* Image url. */
	private String urlImage = "";

	/* Screen width to display image. */
	private int mScreenWidth;

	/* Flag to scale image fill screen width. */
	private boolean isScale = false;

	/* Flag to loaded image state. */
	private boolean isLoaded = false;

	/* ImageAttacher to zoom image. */
	private ImageAttacher mAttacher;

	/* ImageView to display image. */
	private ImageView mImgSaveImage;
	private ImageView mImgSaveImage1;
	private ImageView mImgSaveImage2;
	private ImageView mImgSaveImage3;
	private ImageView mImgGif;

	/* Show progress bar when loading image. */
	private ProgressBar mPbLoadingImage;

	/* ImageLoader and DisplayImageOptions to display image. */
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions imgSquareOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.color.transparent).showImageForEmptyUri(R.color.image_loading)
			.showImageOnFail(R.color.image_loading).cacheInMemory(true).cacheOnDisc(true)
			.displayer(new FadeInBitmapDisplayer(300)).bitmapConfig(Bitmap.Config.RGB_565).build();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_view_detail);

		/* Get screen width. */
		mScreenWidth = getResources().getDisplayMetrics().widthPixels;

		/* Initialize view. */
		mImgSaveImage = (ImageView) findViewById(R.id.imgSaveImage);
		mImgSaveImage1 = (ImageView) findViewById(R.id.imgSaveImage1);
		mImgSaveImage2 = (ImageView) findViewById(R.id.imgSaveImage2);
		mImgSaveImage3 = (ImageView) findViewById(R.id.imgSaveImage3);
		mImgGif = (ImageView) findViewById(R.id.imgGif);
		mPbLoadingImage = (ProgressBar) findViewById(R.id.pbLoadingImage);

		// TODO: working with zoom image.
		// usingSimpleImage(mImgSaveImage);

		/* Get image url. */
		urlImage = getIntent().getExtras().getString(Consts.IMAGE_URL);

		/* If image is gif image, show and play it. */
		if (!TextUtils.isEmpty(urlImage) && urlImage.endsWith(Consts.IMAGE_FILE_GIF_TYPE)) {
			mPbLoadingImage.setVisibility(View.VISIBLE);
			Ion.with(mImgGif).load(urlImage).setCallback(new FutureCallback<ImageView>() {
				@Override
				public void onCompleted(Exception arg0, ImageView arg1) {
					mPbLoadingImage.setVisibility(View.GONE);
				}
			});
		} else {
			/* Display image with listener. */
			imageLoader.displayImage(urlImage, mImgSaveImage, imgSquareOptions, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					/* Display progress bar. */
					mPbLoadingImage.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					isLoaded = true; // Set loaded flag.
					mPbLoadingImage.setVisibility(View.GONE); // Hide progress bar.
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					isLoaded = true; // Set loaded flag.
					mPbLoadingImage.setVisibility(View.GONE); // Hide progress bar.

					/* Calculate full image height when display on device. */
					int imgHeight = mScreenWidth;
					if (loadedImage.getWidth() > 0) {
						imgHeight = mScreenWidth * loadedImage.getHeight() / loadedImage.getWidth();
					}

					/*
					 * After complete loading image, split image in 4 pieces then display on 4 ImageView.
					 * Because some device with some operating system do not display large image, so use this handler
					 * when display it.
					 */
					/* Split image in 4 pieces. */
					Bitmap bitmap1 = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(),
							loadedImage.getHeight() / 4);
					Bitmap bitmap2 = Bitmap.createBitmap(loadedImage, 0, loadedImage.getHeight() / 4,
							loadedImage.getWidth(), loadedImage.getHeight() / 4);
					Bitmap bitmap3 = Bitmap.createBitmap(loadedImage, 0, loadedImage.getHeight() / 2,
							loadedImage.getWidth(), loadedImage.getHeight() / 4);
					Bitmap bitmap4 = Bitmap.createBitmap(loadedImage, 0, 3 * loadedImage.getHeight() / 4,
							loadedImage.getWidth(), loadedImage.getHeight() / 4);

					/* Set each image height and display each pieces. */
					mImgSaveImage.getLayoutParams().height = imgHeight / 4;
					mImgSaveImage.setImageBitmap(bitmap1);
					mImgSaveImage1.getLayoutParams().height = imgHeight / 4;
					mImgSaveImage1.setImageBitmap(bitmap2);
					mImgSaveImage2.getLayoutParams().height = imgHeight / 4;
					mImgSaveImage2.setImageBitmap(bitmap3);
					mImgSaveImage3.getLayoutParams().height = imgHeight / 4;
					mImgSaveImage3.setImageBitmap(bitmap4);
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					isLoaded = true; // Set loaded flag.
					mPbLoadingImage.setVisibility(View.GONE); // Hide progress bar.
				}
			});
		}

		/* Finish this activity when click on image. */
		mImgSaveImage.setOnClickListener(this);
		mImgSaveImage1.setOnClickListener(this);
		mImgSaveImage2.setOnClickListener(this);
		mImgSaveImage3.setOnClickListener(this);
		mImgGif.setOnClickListener(this);

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
		EasyTracker.getInstance(this).activityStop(this); // Stop google analytics for this activity.
	}

	@Override
	protected void onDestroy() {
		if (adView != null) adView.destroy(); // Destroy AdView.
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		/* Finish this activity when click on image. */
		if (v == mImgSaveImage || v == mImgSaveImage1 || v == mImgSaveImage2 || v == mImgSaveImage3 || v == mImgGif) {
			finish();
		}
	}

	/**
	 * Set zoomable on ImageView.
	 * 
	 * @param imageView
	 *            image view to zoom.
	 */
	private void usingSimpleImage(ImageView imageView) {
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

	/**
	 * Listener when image scaling.
	 */
	private class MatrixChangeListener implements OnMatrixChangedListener {
		@Override
		public void onMatrixChanged(RectF rect) {
			/* Scale image fill screen. */
			if (!isLoaded) return; // Has not loaded image yet, do nothing.
			if (!isScale) { // Only scale image fill screen one time.
				/*
				 * If image not fill screen, scale it.
				 * Else set scale flag and MAX_ZOOM value.
				 */
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

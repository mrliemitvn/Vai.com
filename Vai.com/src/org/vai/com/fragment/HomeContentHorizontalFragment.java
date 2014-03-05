package org.vai.com.fragment;

import org.vai.com.R;
import org.vai.com.activity.CommentPageActivity;
import org.vai.com.activity.ImageViewDetailActivity;
import org.vai.com.activity.PlayYoutubeVideoActivity;
import org.vai.com.appinterface.IAdapterCallBack;
import org.vai.com.provider.DbContract.Conference;
import org.vai.com.resource.home.ConferenceResource;
import org.vai.com.utils.Consts;
import org.vai.com.utils.DownloadImageUtils;
import org.vai.com.utils.EmotionsUtils;
import org.vai.com.utils.Logger;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * This class show one conference.<br>
 * Use this class when show horizontal content.
 */
public class HomeContentHorizontalFragment extends BaseFragment implements OnClickListener {
	private static final String TAG = HomeContentHorizontalFragment.class.getSimpleName();

	/* Root layout of this fragment. */
	private View mParentView;

	/* ImageView to show image. */
	private ImageView mImgContent;
	private ImageView mImgContent1;
	private ImageView mImgContent2;
	private ImageView mImgContent3;

	/* ImageView display play button if conference include youtube video. */
	private ImageView mImgPlayYoutube;

	/* ImageView display play gif button if conference image is gif image. */
	private ImageView mImgPlayGif;

	/* For download image. */
	private ImageView mImgDownload;

	/* For share conference to facebook. */
	private ImageView mImgShare;

	/* TextView display conference title. */
	private TextView mTvTitle;

	/* TextView display conference content. */
	private TextView mTvContent;

	/* TextView display like number. */
	private TextView mTvLike;

	/* TextView display comment number. */
	private TextView mTvComment;

	/* Use this progress bar when loading image. */
	private ProgressBar mPbLoadingImage;

	/* Conference data. */
	private ConferenceResource mConferenceResource;

	/* For parse emotion icon. */
	private EmotionsUtils mEmotionsUtils;

	/* For download image. */
	private DownloadImageUtils mDownloadImage;

	/* Use this interface when like or share conference to facebook. */
	private IAdapterCallBack mAdapterCallBack;

	/* Calculate content width will be displayed. */
	private int mContentWidth;

	/* Use this variable if want to reload image. */
	private int mCountTryDisplay = 0;

	/* ImageLoader and DisplayImageOptions to display image. */
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions mSquareOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.color.transparent).showImageForEmptyUri(R.color.image_loading)
			.showImageOnFail(R.color.image_loading).cacheInMemory(true).cacheOnDisc(true)
			.displayer(new FadeInBitmapDisplayer(300)).resetViewBeforeLoading(true).bitmapConfig(Bitmap.Config.RGB_565)
			.build();

	/* Listener when loading image. */
	private ImageLoadingListener mImageLoadingListener;

	/**
	 * Define view.
	 */
	private void init() {
		/* Initialize all view will be used. */
		mImgContent = (ImageView) mParentView.findViewById(R.id.imgContent);
		mImgContent1 = (ImageView) mParentView.findViewById(R.id.imgContent1);
		mImgContent2 = (ImageView) mParentView.findViewById(R.id.imgContent2);
		mImgContent3 = (ImageView) mParentView.findViewById(R.id.imgContent3);
		mImgPlayYoutube = (ImageView) mParentView.findViewById(R.id.imgPlayYoutube);
		mImgPlayGif = (ImageView) mParentView.findViewById(R.id.imgPlayGif);
		mImgDownload = (ImageView) mParentView.findViewById(R.id.imgDownload);
		mImgShare = (ImageView) mParentView.findViewById(R.id.imgShare);
		mTvTitle = (TextView) mParentView.findViewById(R.id.tvTitle);
		mTvContent = (TextView) mParentView.findViewById(R.id.tvContent);
		mTvLike = (TextView) mParentView.findViewById(R.id.tvLike);
		mTvComment = (TextView) mParentView.findViewById(R.id.tvComment);
		mPbLoadingImage = (ProgressBar) mParentView.findViewById(R.id.pbLoadingImage);

		/* Initialize listener for load image. */
		if (mImageLoadingListener == null) {
			mImageLoadingListener = new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String imageUri, View view) {
					/* Display loading progress. */
					mPbLoadingImage.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					/* Hide loading progress. */
					mPbLoadingImage.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					/*
					 * After complete loading image, split image in 4 pieces then display on 4 ImageView.
					 * Because some device with some operating system do not display large image, so use this handler
					 * when display it.
					 */

					/* Image width is screen width, calculate image height. */
					int imgHeight = mContentWidth;
					if (loadedImage.getWidth() > 0) {
						imgHeight = mContentWidth * loadedImage.getHeight() / loadedImage.getWidth();
					}

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
					mImgContent.getLayoutParams().height = imgHeight / 4;
					mImgContent.setImageBitmap(bitmap1);
					mImgContent1.getLayoutParams().height = imgHeight / 4;
					mImgContent1.setImageBitmap(bitmap2);
					mImgContent2.getLayoutParams().height = imgHeight / 4;
					mImgContent2.setImageBitmap(bitmap3);
					mImgContent3.getLayoutParams().height = imgHeight / 4;
					mImgContent3.setImageBitmap(bitmap4);

					/* Hide loading progress bar. */
					mPbLoadingImage.setVisibility(View.GONE);

					/* Show play button if conference is youtube video. */
					if (!TextUtils.isEmpty(mConferenceResource.videoId)) {
						mImgPlayYoutube.setVisibility(View.VISIBLE);
					} else {
						mImgPlayYoutube.setVisibility(View.GONE);
					}

					/*
					 * If image is a gif image, show play gif button.
					 * else, hide it.
					 */
					if (!TextUtils.isEmpty(mConferenceResource.image)
							&& mConferenceResource.image.endsWith(Consts.IMAGE_FILE_GIF_TYPE)) {
						mImgPlayGif.setVisibility(View.VISIBLE);
					} else {
						mImgPlayGif.setVisibility(View.GONE);
					}
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					/* When loading process is cancelled, try to load image again. */
					if (mCountTryDisplay > 0) { // Already tried, hide loading progress bar.
						mPbLoadingImage.setVisibility(View.GONE);
					} else { // Try to load image again.
						mImageLoader.loadImage(mConferenceResource.image, mSquareOptions, mImageLoadingListener);
						mCountTryDisplay++;
					}
				}
			};
		}
	}

	/**
	 * Save image to device if exist.<br>
	 * Generate file name to save by common file name and current time and save image to it.
	 */
	private void downloadImage() {
		if (mConferenceResource == null) return;
		long currentTime = System.currentTimeMillis();
		String fileName = Consts.IMAGE_FILE_NAME + currentTime + Consts.IMAGE_FILE_JPG_TYPE;
		mDownloadImage.downloadImage(mConferenceResource.image, fileName);
	}

	/**
	 * Handle like or unlike event.
	 */
	private void handleLikeAction() {
		Bundle bundle = new Bundle();
		/* Change like state of tvLike. */
		mTvLike.setSelected(!mTvLike.isSelected());

		/* Prepare data to call like or unlike action. */
		long likeNumber = mConferenceResource.like;
		bundle.putString(Consts.JSON_ID, mConferenceResource.id);
		bundle.putLong(Consts.JSON_LIKE_NUMBER, mConferenceResource.like);
		if (mTvLike.isSelected()) { // Call like action.
			bundle.putInt(Consts.JSON_LIKE, Consts.STATE_ON);
			likeNumber++;
		} else { // Call unlike action.
			bundle.putInt(Consts.JSON_LIKE, Consts.STATE_OFF);
			if (likeNumber > 0) likeNumber--;
		}
		if (mAdapterCallBack != null) mAdapterCallBack.adapterCallBack(bundle);

		/* Update like number of conference and view. */
		mTvLike.setText(likeNumber + "");
		String where = new StringBuilder().append(Conference._ID).append("='").append(mConferenceResource.id)
				.append("'").toString();
		ContentValues values = new ContentValues();
		values.put(Conference.LIKE, likeNumber);
		int resultUpdate = getActivity().getContentResolver().update(Conference.CONTENT_URI, values, where, null);
		Logger.debug(TAG, "number conference is updated = " + resultUpdate);
	}

	/**
	 * Show full image detail or play youtube video.
	 */
	private void handleWhenClickOnImage() {
		Intent intent;
		if (TextUtils.isEmpty(mConferenceResource.videoId)) { // Show full image.
			intent = new Intent(getActivity(), ImageViewDetailActivity.class);
			intent.putExtra(Consts.IMAGE_URL, mConferenceResource.image);
		} else { // Play youtube video.
			intent = new Intent(getActivity(), PlayYoutubeVideoActivity.class);
			intent.putExtra(Consts.JSON_ID, mConferenceResource.videoId);
		}
		getActivity().startActivity(intent);
	}

	/**
	 * Go to {@link CommentPageActivity} to see all comment.
	 */
	private void goToCommentPage() {
		Intent intentComment = new Intent(getActivity(), CommentPageActivity.class);
		intentComment.putExtra(Consts.JSON_ID, mConferenceResource.id);
		startActivity(intentComment);
	}

	/**
	 * Prepare data and share to facebook.
	 */
	private void shareToFacebook() {
		Bundle bundleShare = new Bundle();
		bundleShare.putInt(Consts.SHARE_CONFERENCE, Consts.STATE_ON);
		bundleShare.putString(Consts.JSON_ID, mConferenceResource.id);
		bundleShare.putString(Consts.JSON_TITLE, mConferenceResource.title);
		bundleShare.putString(Consts.IMAGE_URL, mConferenceResource.image);
		mAdapterCallBack.adapterCallBack(bundleShare);
	}

	/**
	 * Set conference data for this fragment.
	 * 
	 * @param conferenceResource
	 *            conference to set.
	 */
	public void setConference(ConferenceResource conferenceResource) {
		mConferenceResource = conferenceResource;
	}

	/**
	 * Set call back interface to login or share to facebook.
	 * 
	 * @param adapterCallBack
	 *            adapterCallBack to set.
	 */
	public void setAdapterCallBack(IAdapterCallBack adapterCallBack) {
		mAdapterCallBack = adapterCallBack;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/* Inflater root layout. */
		mParentView = inflater.inflate(R.layout.fragment_home_content_horizontal, container, false);

		init(); // Initialize all view.

		return mParentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mEmotionsUtils = new EmotionsUtils(getActivity()); // Initialize emotion object to parse emotion icon.
	}

	/**
	 * Display conference.
	 */
	public void updateData() {
		/* Not inflated layout yet or no data, do nothing. */
		if (mParentView == null) return;
		if (mConferenceResource == null) {
			mPbLoadingImage.setVisibility(View.VISIBLE);
			return;
		}

		mCountTryDisplay = 0; // Reset try display flag.
		/* Initialize variable for download image. */
		if (mDownloadImage == null) mDownloadImage = new DownloadImageUtils(getActivity());
		/* Display conference title with emotion icon. */
		mEmotionsUtils.setSpannableText(new SpannableStringBuilder(mConferenceResource.title));
		mTvTitle.setText(mEmotionsUtils.getSmileText());
		/*
		 * Hide text content if not exist or conference include youtube video id.
		 * Only show it when conference do not include youtube video id and has text content.
		 */
		if (!TextUtils.isEmpty(mConferenceResource.videoId)) {
			mTvContent.setVisibility(View.GONE);
		} else {
			if (TextUtils.isEmpty(mConferenceResource.content)) {
				mTvContent.setVisibility(View.GONE);
			} else {
				mTvContent.setText(mConferenceResource.content);
				mTvContent.setVisibility(View.VISIBLE);
			}
		}
		/* Display like number and comment number. */
		mTvLike.setText(mConferenceResource.like + "");
		mTvComment.setText(mConferenceResource.comment + "");
		/* If user liked conference, turn on like state, else turn it off. */
		if (Consts.STATE_ON == mConferenceResource.likeState) {
			mTvLike.setSelected(true);
		} else {
			mTvLike.setSelected(false);
		}
		/* Set click event on some view. */
		mTvLike.setOnClickListener(this);
		mTvComment.setOnClickListener(this);
		mImgContent.setOnClickListener(this);
		mImgContent1.setOnClickListener(this);
		mImgContent2.setOnClickListener(this);
		mImgContent3.setOnClickListener(this);
		mImgDownload.setOnClickListener(this);
		mImgShare.setOnClickListener(this);

		/* Get screen width and display image. */
		mContentWidth = getResources().getDisplayMetrics().widthPixels;
		mImageLoader.loadImage(mConferenceResource.image, mSquareOptions, mImageLoadingListener);
	}

	@Override
	public void onClick(View v) {
		if (v == mImgDownload) { // User click download button.
			downloadImage(); // Download image and save to device.
		} else if (v == mTvLike) {
			handleLikeAction(); // Handle like action.
		} else if (v == mImgContent || v == mImgContent1 || v == mImgContent2 || v == mImgContent3) {
			handleWhenClickOnImage(); // Handle when click on image.
		} else if (v == mTvComment) {
			goToCommentPage(); // Go to comment page.
		} else if (v == mImgShare) {
			shareToFacebook(); // Share to facebook.
		}
	}
}

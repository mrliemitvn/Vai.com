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
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class HomeContentHorizontalFragment extends BaseFragment implements OnClickListener {
	private static final String TAG = HomeContentHorizontalFragment.class.getSimpleName();

	private View mParentView;
	private ImageView mImgContent;
	private ImageView mImgContent1;
	private ImageView mImgPlayYoutube;
	private ImageView mImgDownload;
	private ImageView mImgShare;
	private TextView mTvTitle;
	private TextView mTvContent;
	private TextView mTvLike;
	private TextView mTvComment;
	private ProgressBar mPbLoadingImage;

	private ConferenceResource mConferenceResource;
	private EmotionsUtils mEmotionsUtils;
	private DownloadImageUtils mDownloadImage;
	private IAdapterCallBack mAdapterCallBack;
	private int mContentWidth;
	private int mCountTryDisplay = 0;

	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions mSquareOptions = new DisplayImageOptions.Builder().showStubImage(R.color.transparent)
			.showImageForEmptyUri(R.color.image_loading).showImageOnFail(R.color.image_loading).cacheInMemory(true)
			.cacheOnDisc(true).displayer(new FadeInBitmapDisplayer(300)).resetViewBeforeLoading(true)
			.bitmapConfig(Bitmap.Config.RGB_565).build();
	private ImageLoadingListener mImageLoadingListener;

	/**
	 * Define view.
	 */
	private void init() {
		mImgContent = (ImageView) mParentView.findViewById(R.id.imgContent);
		mImgContent1 = (ImageView) mParentView.findViewById(R.id.imgContent1);
		mImgPlayYoutube = (ImageView) mParentView.findViewById(R.id.imgPlayYoutube);
		mImgDownload = (ImageView) mParentView.findViewById(R.id.imgDownload);
		mImgShare = (ImageView) mParentView.findViewById(R.id.imgShare);
		mTvTitle = (TextView) mParentView.findViewById(R.id.tvTitle);
		mTvContent = (TextView) mParentView.findViewById(R.id.tvContent);
		mTvLike = (TextView) mParentView.findViewById(R.id.tvLike);
		mTvComment = (TextView) mParentView.findViewById(R.id.tvComment);
		mPbLoadingImage = (ProgressBar) mParentView.findViewById(R.id.pbLoadingImage);

		if (mImageLoadingListener == null) {
			mImageLoadingListener = new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String imageUri, View view) {
					mPbLoadingImage.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					mPbLoadingImage.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					int imgHeight = mContentWidth;
					if (loadedImage.getWidth() > 0) {
						imgHeight = mContentWidth * loadedImage.getHeight() / loadedImage.getWidth();
					}

					Bitmap bitmap1 = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(),
							loadedImage.getHeight() / 2);
					Bitmap bitmap2 = Bitmap.createBitmap(loadedImage, 0, loadedImage.getHeight() / 2,
							loadedImage.getWidth(), loadedImage.getHeight() / 2);

					mImgContent.getLayoutParams().height = imgHeight / 2;
					mImgContent.setImageBitmap(bitmap1);
					mImgContent1.getLayoutParams().height = imgHeight / 2;
					mImgContent1.setImageBitmap(bitmap2);
					mPbLoadingImage.setVisibility(View.GONE);
					if (!TextUtils.isEmpty(mConferenceResource.videoId)) {
						mImgPlayYoutube.setVisibility(View.VISIBLE);
					} else {
						mImgPlayYoutube.setVisibility(View.GONE);
					}
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					if (mCountTryDisplay > 0) {
						mPbLoadingImage.setVisibility(View.GONE);
					} else {
						mImageLoader.loadImage(mConferenceResource.image, mSquareOptions, mImageLoadingListener);
						mCountTryDisplay++;
					}
				}

				@Override
				public void onDownloadComplete(String downloadedFile, String url) {
					Logger.debug(TAG, "image url: " + url + " downloaded file path = " + downloadedFile);
				}
			};
		}
	}

	public void setConference(ConferenceResource conferenceResource) {
		mConferenceResource = conferenceResource;
	}

	/**
	 * @param adapterCallBack
	 *            adapterCallBack to set.
	 */
	public void setAdapterCallBack(IAdapterCallBack adapterCallBack) {
		mAdapterCallBack = adapterCallBack;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_home_content_horizontal, container, false);

		init();

		return mParentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mEmotionsUtils = new EmotionsUtils(getActivity());
	}

	public void updateData() {
		if (mParentView == null) return;
		if (mConferenceResource == null) {
			mPbLoadingImage.setVisibility(View.VISIBLE);
			return;
		}
		mCountTryDisplay = 0;
		if (mDownloadImage == null) mDownloadImage = new DownloadImageUtils(getActivity());
		mEmotionsUtils.setSpannableText(new SpannableStringBuilder(mConferenceResource.title));
		mTvTitle.setText(mEmotionsUtils.getSmileText());
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
		mTvLike.setText(mConferenceResource.like + "");
		mTvComment.setText(mConferenceResource.comment + "");
		if (Consts.STATE_ON == mConferenceResource.likeState) {
			mTvLike.setSelected(true);
		} else {
			mTvLike.setSelected(false);
		}
		mTvLike.setOnClickListener(this);
		mTvComment.setOnClickListener(this);
		mImgContent.setOnClickListener(this);
		mImgContent1.setOnClickListener(this);
		mImgDownload.setOnClickListener(this);
		mImgShare.setOnClickListener(this);
		mContentWidth = getResources().getDisplayMetrics().widthPixels;
		mImageLoader.loadImage(mConferenceResource.image, mSquareOptions, mImageLoadingListener);
	}

	@Override
	public void onClick(View v) {
		if (v == mImgDownload) {
			if (mConferenceResource == null) return;
			long currentTime = System.currentTimeMillis();
			String fileName = Consts.IMAGE_FILE_NAME + currentTime + Consts.IMAGE_FILE_JPG_TYPE;
			mDownloadImage.downloadImage(mConferenceResource.image, fileName);
		} else if (v == mTvLike) {
			// Handler like event.
			Bundle bundle = new Bundle();
			// Change like state of tvLike.
			mTvLike.setSelected(!mTvLike.isSelected());
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
			mTvLike.setText(likeNumber + "");
			// Update like number of conference.
			String where = new StringBuilder().append(Conference._ID).append("='").append(mConferenceResource.id)
					.append("'").toString();
			ContentValues values = new ContentValues();
			values.put(Conference.LIKE, likeNumber);
			int resultUpdate = getActivity().getContentResolver().update(Conference.CONTENT_URI, values, where, null);
			Logger.debug(TAG, "number conference is updated = " + resultUpdate);
		} else if (v == mImgContent || v == mImgContent1) {
			Intent intent;
			if (TextUtils.isEmpty(mConferenceResource.videoId)) {
				intent = new Intent(getActivity(), ImageViewDetailActivity.class);
				intent.putExtra(Consts.IMAGE_URL, mConferenceResource.image);
			} else {
				intent = new Intent(getActivity(), PlayYoutubeVideoActivity.class);
				intent.putExtra(Consts.JSON_ID, mConferenceResource.videoId);
			}
			getActivity().startActivity(intent);
		} else if (v == mTvComment) {
			Intent intentComment = new Intent(getActivity(), CommentPageActivity.class);
			intentComment.putExtra(Consts.JSON_ID, mConferenceResource.id);
			startActivity(intentComment);
		} else if (v == mImgShare) {
			Bundle bundleShare = new Bundle();
			bundleShare.putInt(Consts.SHARE_CONFERENCE, Consts.STATE_ON);
			bundleShare.putString(Consts.JSON_ID, mConferenceResource.id);
			bundleShare.putString(Consts.JSON_TITLE, mConferenceResource.title);
			bundleShare.putString(Consts.IMAGE_URL, mConferenceResource.image);
			mAdapterCallBack.adapterCallBack(bundleShare);
		}
	}
}

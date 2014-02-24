package org.vai.com.fragment;

import org.vai.com.R;
import org.vai.com.activity.CommentPageActivity;
import org.vai.com.activity.ImageViewDetailActivity;
import org.vai.com.activity.YouTubePlayerActivity;
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
import android.net.Uri;
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

public class HomeContentHorizontalFragment extends BaseFragment implements OnClickListener {
	private static final String TAG = HomeContentHorizontalFragment.class.getSimpleName();

	private View mParentView;
	private ImageView mImgContent;
	private ImageView mImgPlayYoutube;
	private ImageView mImgDownload;
	private ImageView mImgShare;
	private TextView mTvTitle;
	private TextView mTvLike;
	private TextView mTvComment;
	private ProgressBar mPbLoadingImage;

	private ConferenceResource mConferenceResource;
	private EmotionsUtils mEmotionsUtils;
	private DownloadImageUtils mDownloadImage;
	private IAdapterCallBack mAdapterCallBack;
	private int mContentWidth;

	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions mSquareOptions = new DisplayImageOptions.Builder().showStubImage(R.color.image_loading)
			.showImageForEmptyUri(R.color.image_loading).showImageOnFail(R.color.image_loading).cacheInMemory(true)
			.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();

	/**
	 * Define view.
	 */
	private void init() {
		mImgContent = (ImageView) mParentView.findViewById(R.id.imgContent);
		mImgPlayYoutube = (ImageView) mParentView.findViewById(R.id.imgPlayYoutube);
		mImgDownload = (ImageView) mParentView.findViewById(R.id.imgDownload);
		mImgShare = (ImageView) mParentView.findViewById(R.id.imgShare);
		mTvTitle = (TextView) mParentView.findViewById(R.id.tvTitle);
		mTvLike = (TextView) mParentView.findViewById(R.id.tvLike);
		mTvComment = (TextView) mParentView.findViewById(R.id.tvComment);
		mPbLoadingImage = (ProgressBar) mParentView.findViewById(R.id.pbLoadingImage);
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
		if (mDownloadImage == null) mDownloadImage = new DownloadImageUtils(getActivity());
		mEmotionsUtils.setSpannableText(new SpannableStringBuilder(mConferenceResource.title));
		mTvTitle.setText(mEmotionsUtils.getSmileText());
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
		mImgDownload.setOnClickListener(this);
		mImgShare.setOnClickListener(this);
		mContentWidth = getResources().getDisplayMetrics().widthPixels;
		mImageLoader.loadImage(mConferenceResource.image, mSquareOptions, new ImageLoadingListener() {

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
				mImgContent.getLayoutParams().height = imgHeight;
				mImageLoader.displayImage(mConferenceResource.image, mImgContent, mSquareOptions);
				mPbLoadingImage.setVisibility(View.GONE);
				if (!TextUtils.isEmpty(mConferenceResource.videoId)) {
					mImgPlayYoutube.setVisibility(View.VISIBLE);
				} else {
					mImgPlayYoutube.setVisibility(View.GONE);
				}
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				mPbLoadingImage.setVisibility(View.GONE);
			}

			@Override
			public void onDownloadComplete(String downloadedFile, String url) {
				Logger.debug(TAG, "image url: " + url + " downloaded file path = " + downloadedFile);
			}
		});
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
			if (mTvLike.isSelected()) { // Call like action.
				bundle.putInt(Consts.JSON_LIKE, Consts.STATE_ON);
				bundle.putString(Consts.JSON_ID, mConferenceResource.id);
				likeNumber++;
			} else { // Call unlike action.
				bundle.putInt(Consts.JSON_LIKE, Consts.STATE_OFF);
				bundle.putString(Consts.JSON_ID, mConferenceResource.id);
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
		} else if (v == mImgContent) {
			Intent intent;
			if (TextUtils.isEmpty(mConferenceResource.videoId)) {
				intent = new Intent(getActivity(), ImageViewDetailActivity.class);
				intent.putExtra(Consts.IMAGE_URL, mConferenceResource.image);
			} else if (getActivity().getPackageManager().getLaunchIntentForPackage(Consts.YOUTUBE_PACKAGE) != null) {
				intent = new Intent(getActivity(), YouTubePlayerActivity.class);
				intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, mConferenceResource.videoId);
			} else {
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + mConferenceResource.videoId));
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

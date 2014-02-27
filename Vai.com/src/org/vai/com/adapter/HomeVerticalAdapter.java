package org.vai.com.adapter;

import java.util.ArrayList;

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
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class HomeVerticalAdapter extends ArrayAdapter<ConferenceResource> {
	private static final String TAG = HomeVerticalAdapter.class.getSimpleName();

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private IAdapterCallBack mAdapterCallBack;
	private DownloadImageUtils mDownloadImage;
	private OnClickListener mOnClickListener;
	private ArrayList<ConferenceResource> mListConference = new ArrayList<ConferenceResource>();
	private int mContentWidth;
	private int mMaskHeight;
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions mSquareOptions = new DisplayImageOptions.Builder().showStubImage(R.color.transparent)
			.showImageForEmptyUri(R.color.image_loading).showImageOnFail(R.color.image_loading).cacheInMemory(true)
			.cacheOnDisc(true).displayer(new FadeInBitmapDisplayer(300)).resetViewBeforeLoading(true)
			.bitmapConfig(Bitmap.Config.RGB_565).build();

	public HomeVerticalAdapter(Context context, ArrayList<ConferenceResource> listConference,
			IAdapterCallBack adapterCallBack) {
		super(context, 0, listConference);
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mAdapterCallBack = adapterCallBack;
		mDownloadImage = new DownloadImageUtils(context);
		mListConference = listConference;

		// Calculate content width.
		int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
		mContentWidth = screenWidth - 4 * mContext.getResources().getDimensionPixelSize(R.dimen.common_margin_small);
		mMaskHeight = mContext.getResources().getDimensionPixelSize(R.dimen.default_mask_height);

		mOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				int id = v.getId();
				ViewHolder viewHolder = (ViewHolder) v.getTag();
				switch (id) {
				case R.id.imgContent:
				case R.id.imgContent1:
					Intent intent;
					if (TextUtils.isEmpty(viewHolder.conference.videoId)) {
						intent = new Intent(mContext, ImageViewDetailActivity.class);
						intent.putExtra(Consts.IMAGE_URL, viewHolder.conference.image);
					} else if (mContext.getPackageManager().getLaunchIntentForPackage(Consts.YOUTUBE_PACKAGE) != null) {
						intent = new Intent(mContext, YouTubePlayerActivity.class);
						intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, viewHolder.conference.videoId);
					} else {
						intent = new Intent(Intent.ACTION_VIEW,
								Uri.parse(Consts.URLConstants.YOUTUBE_VIDEO_WATCHING_URL
										+ viewHolder.conference.videoId));
					}
					mContext.startActivity(intent);
					break;
				case R.id.imgDownload:
					long currentTime = System.currentTimeMillis();
					String fileName = Consts.IMAGE_FILE_NAME + currentTime + Consts.IMAGE_FILE_JPG_TYPE;
					mDownloadImage.downloadImage(viewHolder.conference.image, fileName);
					break;
				case R.id.tvLike:
					Bundle bundle = new Bundle();
					// Change like state of tvLike.
					viewHolder.tvLike.setSelected(!viewHolder.tvLike.isSelected());
					long likeNumber = viewHolder.conference.like;
					bundle.putString(Consts.JSON_ID, viewHolder.conference.id);
					bundle.putLong(Consts.JSON_LIKE_NUMBER, viewHolder.conference.like);
					if (viewHolder.tvLike.isSelected()) { // Call like action.
						bundle.putInt(Consts.JSON_LIKE, Consts.STATE_ON);
						likeNumber++;
					} else { // Call unlike action.
						bundle.putInt(Consts.JSON_LIKE, Consts.STATE_OFF);
						if (likeNumber > 0) likeNumber--;
					}
					if (mAdapterCallBack != null) mAdapterCallBack.adapterCallBack(bundle);
					viewHolder.tvLike.setText(likeNumber + "");
					// Update like number of conference.
					String where = new StringBuilder().append(Conference._ID).append("='")
							.append(viewHolder.conference.id).append("'").toString();
					ContentValues values = new ContentValues();
					values.put(Conference.LIKE, likeNumber);
					int resultUpdate = mContext.getContentResolver()
							.update(Conference.CONTENT_URI, values, where, null);
					Logger.debug(TAG, "number conference is updated = " + resultUpdate);
					break;
				case R.id.tvComment:
					Intent intentComment = new Intent(mContext, CommentPageActivity.class);
					intentComment.putExtra(Consts.JSON_ID, viewHolder.conference.id);
					mContext.startActivity(intentComment);
					break;
				case R.id.imgShare:
					if (mAdapterCallBack == null) return;
					Bundle bundleShare = new Bundle();
					bundleShare.putInt(Consts.SHARE_CONFERENCE, Consts.STATE_ON);
					bundleShare.putString(Consts.JSON_ID, viewHolder.conference.id);
					bundleShare.putString(Consts.JSON_TITLE, viewHolder.conference.title);
					bundleShare.putString(Consts.IMAGE_URL, viewHolder.conference.image);
					mAdapterCallBack.adapterCallBack(bundleShare);
					break;
				default:
					break;
				}
			}
		};
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_list_conference, null);
			viewHolder = new ViewHolder();
			viewHolder.emotionsUtils = new EmotionsUtils(mContext);
			viewHolder.rlContent = (RelativeLayout) convertView.findViewById(R.id.rlContent);
			viewHolder.viewDivider = (View) convertView.findViewById(R.id.viewDivider);
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
			viewHolder.pbLoadingImage = (ProgressBar) convertView.findViewById(R.id.pbLoadingImage);
			viewHolder.tvLike = (TextView) convertView.findViewById(R.id.tvLike);
			viewHolder.tvComment = (TextView) convertView.findViewById(R.id.tvComment);
			viewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
			viewHolder.imgContent = (ImageView) convertView.findViewById(R.id.imgContent);
			viewHolder.imgContent1 = (ImageView) convertView.findViewById(R.id.imgContent1);
			viewHolder.imgPlayYoutube = (ImageView) convertView.findViewById(R.id.imgPlayYoutube);
			viewHolder.imgDownload = (ImageView) convertView.findViewById(R.id.imgDownload);
			viewHolder.imgShare = (ImageView) convertView.findViewById(R.id.imgShare);
			viewHolder.imageLoadingListener = new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					viewHolder.pbLoadingImage.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					viewHolder.pbLoadingImage.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					viewHolder.pbLoadingImage.setVisibility(View.GONE);

					int imgHeight = mContentWidth;
					if (loadedImage.getWidth() > 0) {
						imgHeight = mContentWidth * loadedImage.getHeight() / loadedImage.getWidth();
					}
					viewHolder.rlContent.getLayoutParams().height = imgHeight;

					Bitmap bitmap1 = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(),
							loadedImage.getHeight() / 2);
					Bitmap bitmap2 = Bitmap.createBitmap(loadedImage, 0, loadedImage.getHeight() / 2,
							loadedImage.getWidth(), loadedImage.getHeight() / 2);

					viewHolder.imgContent.getLayoutParams().height = imgHeight / 2;
					viewHolder.imgContent.setImageBitmap(bitmap1);
					viewHolder.imgContent1.getLayoutParams().height = imgHeight / 2;
					viewHolder.imgContent1.setImageBitmap(bitmap2);

					int imgHeightContent = mContentWidth;
					if (viewHolder.conference.imgWidth > 0) {
						imgHeightContent = mContentWidth * viewHolder.conference.imgHeight
								/ viewHolder.conference.imgWidth;
					}
					int marginTop = imgHeightContent - imgHeight;
					((LayoutParams) viewHolder.viewDivider.getLayoutParams()).setMargins(0, marginTop, 0, 0);
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					Logger.debug(TAG, "image url: " + imageUri);
					if (viewHolder.countTryDisplay <= 0) {
						mImageLoader.loadImage(viewHolder.conference.image, mSquareOptions,
								viewHolder.imageLoadingListener);
						return;
					}
					viewHolder.pbLoadingImage.setVisibility(View.GONE);
				}

				@Override
				public void onDownloadComplete(String downloadedFile, String url) {
					Logger.debug(TAG, "image url: " + url + " downloaded file path = " + downloadedFile);
					viewHolder.pbLoadingImage.setVisibility(View.GONE);
				}
			};

			convertView.setTag(viewHolder);
			viewHolder.imgContent.setTag(viewHolder);
			viewHolder.imgContent1.setTag(viewHolder);
			viewHolder.imgDownload.setTag(viewHolder);
			viewHolder.tvLike.setTag(viewHolder);
			viewHolder.tvComment.setTag(viewHolder);
			viewHolder.imgShare.setTag(viewHolder);
			viewHolder.imgContent.setOnClickListener(mOnClickListener);
			viewHolder.imgContent1.setOnClickListener(mOnClickListener);
			viewHolder.imgDownload.setOnClickListener(mOnClickListener);
			viewHolder.tvLike.setOnClickListener(mOnClickListener);
			viewHolder.tvComment.setOnClickListener(mOnClickListener);
			viewHolder.imgShare.setOnClickListener(mOnClickListener);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// Show content.
		ConferenceResource conferenceResource = mListConference.get(position);
		viewHolder.conference = conferenceResource;
		viewHolder.emotionsUtils.setSpannableText(new SpannableStringBuilder(conferenceResource.title));
		viewHolder.tvTitle.setText(viewHolder.emotionsUtils.getSmileText());
		viewHolder.tvLike.setText(conferenceResource.like + "");
		viewHolder.tvComment.setText(conferenceResource.comment + "");
		if (!TextUtils.isEmpty(conferenceResource.videoId)) {
			viewHolder.imgIcon.setImageResource(R.drawable.video_icon);
			viewHolder.imgPlayYoutube.setVisibility(View.VISIBLE);
			viewHolder.tvContent.setVisibility(View.GONE);
		} else {
			viewHolder.imgIcon.setImageResource(R.drawable.photo_icon);
			viewHolder.imgPlayYoutube.setVisibility(View.GONE);
			if (TextUtils.isEmpty(conferenceResource.content)) {
				viewHolder.tvContent.setVisibility(View.GONE);
			} else {
				viewHolder.tvContent.setText(conferenceResource.content);
				viewHolder.tvContent.setVisibility(View.VISIBLE);
			}
		}
		if (Consts.STATE_ON == conferenceResource.likeState) {
			viewHolder.tvLike.setSelected(true);
		} else {
			viewHolder.tvLike.setSelected(false);
		}
		viewHolder.imgContent.setImageResource(R.color.transparent);
		viewHolder.imgContent1.setImageResource(R.color.transparent);
		int imgHeight = mContentWidth;
		if (conferenceResource.imgWidth > 0) {
			imgHeight = mContentWidth * conferenceResource.imgHeight / conferenceResource.imgWidth + mMaskHeight;
		}
		viewHolder.rlContent.getLayoutParams().height = imgHeight;
		viewHolder.countTryDisplay = 0;
		mImageLoader.loadImage(conferenceResource.image, mSquareOptions, viewHolder.imageLoadingListener);
		return convertView;
	}

	private class ViewHolder {
		int countTryDisplay;
		ConferenceResource conference;
		EmotionsUtils emotionsUtils;
		ImageLoadingListener imageLoadingListener;
		RelativeLayout rlContent;
		ProgressBar pbLoadingImage;
		View viewDivider;
		TextView tvTitle;
		TextView tvContent;
		TextView tvLike;
		TextView tvComment;
		ImageView imgIcon;
		ImageView imgContent;
		ImageView imgContent1;
		ImageView imgPlayYoutube;
		ImageView imgDownload;
		ImageView imgShare;
	}
}

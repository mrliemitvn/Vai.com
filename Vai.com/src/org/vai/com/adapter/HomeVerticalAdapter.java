package org.vai.com.adapter;

import java.util.ArrayList;

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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * This adapter display conference on {@link ListView}.
 */
public class HomeVerticalAdapter extends ArrayAdapter<ConferenceResource> {
	private static final String TAG = HomeVerticalAdapter.class.getSimpleName();

	/* Application context. */
	private Context mContext;

	/* LayoutInflater to inflate conference item layout. */
	private LayoutInflater mLayoutInflater;

	/* Call back interface for like or share to facebook. */
	private IAdapterCallBack mAdapterCallBack;

	/* For download image. */
	private DownloadImageUtils mDownloadImage;

	/* Listener to handle click event. */
	private OnClickListener mOnClickListener;

	/* List conference data. */
	private ArrayList<ConferenceResource> mListConference = new ArrayList<ConferenceResource>();

	/* Width to display image. */
	private int mContentWidth;

	/* Height of mask overlap on image. */
	private int mMaskHeight;

	/* ImageLoader and DisplayOptions to display image. */
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions mSquareOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.color.transparent).showImageForEmptyUri(R.color.image_loading)
			.showImageOnFail(R.color.image_loading).cacheInMemory(true).cacheOnDisc(true)
			.displayer(new FadeInBitmapDisplayer(300)).resetViewBeforeLoading(true).bitmapConfig(Bitmap.Config.RGB_565)
			.build();

	/**
	 * Handle when click on image.
	 * 
	 * @param viewHolder
	 *            viewHolder contain view and conference data.
	 */
	private void handleClickOnImage(ViewHolder viewHolder) {
		/*
		 * If conference is not a youtube video, show full image detail by go to {@link ImageViewDetailActivity}.
		 * Else play youtube video by go to {@link PlayYoutubeVideoActivity}.
		 */
		Intent intent;
		if (TextUtils.isEmpty(viewHolder.conference.videoId)) { // Show full image detail.
			intent = new Intent(mContext, ImageViewDetailActivity.class);
			intent.putExtra(Consts.IMAGE_URL, viewHolder.conference.image);
		} else { // Play youtube video.
			intent = new Intent(mContext, PlayYoutubeVideoActivity.class);
			intent.putExtra(Consts.JSON_ID, viewHolder.conference.videoId);
		}
		mContext.startActivity(intent);
	}

	/**
	 * Save image to device if exist.<br>
	 * Generate file name to save by common file name and current time and save image to it.
	 * 
	 * @param viewHolder
	 *            viewHolder contain view and conference data.
	 */
	private void downloadImage(ViewHolder viewHolder) {
		long currentTime = System.currentTimeMillis();
		String fileName = Consts.IMAGE_FILE_NAME + currentTime + Consts.IMAGE_FILE_JPG_TYPE;
		mDownloadImage.downloadImage(viewHolder.conference.image, fileName);
	}

	/**
	 * Handle like or unlike event.
	 * 
	 * @param viewHolder
	 *            viewHolder contain view and conference data.
	 */
	private void handleLikeAction(ViewHolder viewHolder) {
		Bundle bundle = new Bundle();
		// Change like state of tvLike.
		viewHolder.tvLike.setSelected(!viewHolder.tvLike.isSelected());

		/* Prepare data to call like or unlike action. */
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

		/* Update like number of conference and view. */
		viewHolder.tvLike.setText(likeNumber + "");
		String where = new StringBuilder().append(Conference._ID).append("='").append(viewHolder.conference.id)
				.append("'").toString();
		ContentValues values = new ContentValues();
		values.put(Conference.LIKE, likeNumber);
		int resultUpdate = mContext.getContentResolver().update(Conference.CONTENT_URI, values, where, null);
		Logger.debug(TAG, "number conference is updated = " + resultUpdate);
	}

	/**
	 * Go to {@link CommentPageActivity} to see all comment.
	 */
	private void goToCommentPage(ViewHolder viewHolder) {
		Intent intentComment = new Intent(mContext, CommentPageActivity.class);
		intentComment.putExtra(Consts.JSON_ID, viewHolder.conference.id);
		mContext.startActivity(intentComment);
	}

	/**
	 * Prepare data and share to facebook.
	 */
	private void shareToFacebook(ViewHolder viewHolder) {
		Bundle bundleShare = new Bundle();
		bundleShare.putInt(Consts.SHARE_CONFERENCE, Consts.STATE_ON);
		bundleShare.putString(Consts.JSON_ID, viewHolder.conference.id);
		bundleShare.putString(Consts.JSON_TITLE, viewHolder.conference.title);
		bundleShare.putString(Consts.IMAGE_URL, viewHolder.conference.image);
		mAdapterCallBack.adapterCallBack(bundleShare);
	}

	/**
	 * Constructor create {@link HomeVerticalAdapter} object.
	 * 
	 * @param context
	 *            context to set.
	 * @param listConference
	 *            list conference data.
	 * @param adapterCallBack
	 *            call back interface to set.
	 */
	public HomeVerticalAdapter(Context context, ArrayList<ConferenceResource> listConference,
			IAdapterCallBack adapterCallBack) {
		super(context, 0, listConference);
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mAdapterCallBack = adapterCallBack;
		mDownloadImage = new DownloadImageUtils(context);
		mListConference = listConference;

		// Calculate content width and mask height.
		int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
		mContentWidth = screenWidth - 4 * mContext.getResources().getDimensionPixelSize(R.dimen.common_margin_small);
		mMaskHeight = mContext.getResources().getDimensionPixelSize(R.dimen.default_mask_height);

		/* Initialize click listener. */
		mOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				int id = v.getId();
				ViewHolder viewHolder = (ViewHolder) v.getTag();
				switch (id) {
				case R.id.imgContent:
				case R.id.imgContent1:
				case R.id.imgContent2:
				case R.id.imgContent3:
					handleClickOnImage(viewHolder); // Handle when click on image.
					break;
				case R.id.imgDownload:
					downloadImage(viewHolder); // Download image.
					break;
				case R.id.tvLike:
					handleLikeAction(viewHolder); // Handle like or unlike action.
					break;
				case R.id.tvComment:
					goToCommentPage(viewHolder); // Go to comment page.
					break;
				case R.id.imgShare:
					if (mAdapterCallBack == null) return;
					shareToFacebook(viewHolder); // Share to facebook.
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
			/* Inflate conference item layout and initialize view. */
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
			viewHolder.imgContent2 = (ImageView) convertView.findViewById(R.id.imgContent2);
			viewHolder.imgContent3 = (ImageView) convertView.findViewById(R.id.imgContent3);
			viewHolder.imgPlayYoutube = (ImageView) convertView.findViewById(R.id.imgPlayYoutube);
			viewHolder.imgPlayGif = (ImageView) convertView.findViewById(R.id.imgPlayGif);
			viewHolder.imgDownload = (ImageView) convertView.findViewById(R.id.imgDownload);
			viewHolder.imgShare = (ImageView) convertView.findViewById(R.id.imgShare);

			/* Initialize listener when load image. */
			viewHolder.imageLoadingListener = new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					/* Show progress bar loading. */
					viewHolder.pbLoadingImage.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					/* Hide progress bar loading. */
					viewHolder.pbLoadingImage.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					/* Hide progress bar loading. */
					viewHolder.pbLoadingImage.setVisibility(View.GONE);

					/*
					 * Calculate content width and height.
					 * Set layout content height.
					 */
					int imgHeight = mContentWidth;
					if (loadedImage.getWidth() > 0) {
						imgHeight = mContentWidth * loadedImage.getHeight() / loadedImage.getWidth();
					}
					viewHolder.rlContent.getLayoutParams().height = imgHeight;

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
					viewHolder.imgContent.getLayoutParams().height = imgHeight / 4;
					viewHolder.imgContent.setImageBitmap(bitmap1);
					viewHolder.imgContent1.getLayoutParams().height = imgHeight / 4;
					viewHolder.imgContent1.setImageBitmap(bitmap2);
					viewHolder.imgContent2.getLayoutParams().height = imgHeight / 4;
					viewHolder.imgContent2.setImageBitmap(bitmap3);
					viewHolder.imgContent3.getLayoutParams().height = imgHeight / 4;
					viewHolder.imgContent3.setImageBitmap(bitmap4);

					/* Calculate mask height. */
					int imgHeightContent = mContentWidth;
					if (viewHolder.conference.imgWidth > 0) {
						imgHeightContent = mContentWidth * viewHolder.conference.imgHeight
								/ viewHolder.conference.imgWidth;
					}
					/* Set mask overlap on image. */
					int marginTop = imgHeightContent - imgHeight;
					((LayoutParams) viewHolder.viewDivider.getLayoutParams()).setMargins(0, marginTop, 0, 0);
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					Logger.debug(TAG, "image url: " + imageUri);
					/* Try load image one time again. */
					if (viewHolder.countTryDisplay <= 0) {
						mImageLoader.loadImage(viewHolder.conference.image, mSquareOptions,
								viewHolder.imageLoadingListener);
						return;
					}
					/* Hide progress bar loading. */
					viewHolder.pbLoadingImage.setVisibility(View.GONE);
				}
			};

			convertView.setTag(viewHolder);

			/* Set click event. */
			viewHolder.imgContent.setTag(viewHolder);
			viewHolder.imgContent1.setTag(viewHolder);
			viewHolder.imgContent2.setTag(viewHolder);
			viewHolder.imgContent3.setTag(viewHolder);
			viewHolder.imgDownload.setTag(viewHolder);
			viewHolder.tvLike.setTag(viewHolder);
			viewHolder.tvComment.setTag(viewHolder);
			viewHolder.imgShare.setTag(viewHolder);
			viewHolder.imgContent.setOnClickListener(mOnClickListener);
			viewHolder.imgContent1.setOnClickListener(mOnClickListener);
			viewHolder.imgContent2.setOnClickListener(mOnClickListener);
			viewHolder.imgContent3.setOnClickListener(mOnClickListener);
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
		/* Show conference title with emotion icon. */
		viewHolder.emotionsUtils.setSpannableText(new SpannableStringBuilder(conferenceResource.title));
		viewHolder.tvTitle.setText(viewHolder.emotionsUtils.getSmileText());
		/* Show like number and comment number. */
		viewHolder.tvLike.setText(conferenceResource.like + "");
		viewHolder.tvComment.setText(conferenceResource.comment + "");

		/*
		 * Hide text content if not exist or conference include youtube video id.
		 * Only show it when conference do not include youtube video id and has text content.
		 * Same with play video button.
		 */
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

		/*
		 * If image is a gif image, show play gif button.
		 * else, hide it.
		 */
		if (!TextUtils.isEmpty(conferenceResource.image)
				&& conferenceResource.image.endsWith(Consts.IMAGE_FILE_GIF_TYPE)) {
			viewHolder.imgPlayGif.setVisibility(View.VISIBLE);
		} else {
			viewHolder.imgPlayGif.setVisibility(View.GONE);
		}

		/* If user liked conference, turn on like state, else turn it off. */
		if (Consts.STATE_ON == conferenceResource.likeState) {
			viewHolder.tvLike.setSelected(true);
		} else {
			viewHolder.tvLike.setSelected(false);
		}

		/* Reset image before load image. */
		viewHolder.imgContent.setImageResource(R.color.transparent);
		viewHolder.imgContent1.setImageResource(R.color.transparent);
		viewHolder.imgContent2.setImageResource(R.color.transparent);
		viewHolder.imgContent3.setImageResource(R.color.transparent);

		/* Set layout image height before load image. */
		int imgHeight = mContentWidth;
		if (conferenceResource.imgWidth > 0) {
			imgHeight = mContentWidth * conferenceResource.imgHeight / conferenceResource.imgWidth + mMaskHeight;
		}
		viewHolder.rlContent.getLayoutParams().height = imgHeight;

		/* Reset flag reload image and begin load image. */
		viewHolder.countTryDisplay = 0;
		mImageLoader.loadImage(conferenceResource.image, mSquareOptions, viewHolder.imageLoadingListener);
		return convertView;
	}

	/**
	 * View holder of this adapter.
	 */
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
		ImageView imgContent2;
		ImageView imgContent3;
		ImageView imgPlayYoutube;
		ImageView imgPlayGif;
		ImageView imgDownload;
		ImageView imgShare;
	}
}

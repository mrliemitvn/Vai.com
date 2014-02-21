package org.vai.com.adapter;

import java.util.ArrayList;

import org.vai.com.R;
import org.vai.com.activity.ImageViewDetailActivity;
import org.vai.com.activity.YouTubePlayerActivity;
import org.vai.com.appinterface.IAdapterCallBack;
import org.vai.com.resource.home.ConferenceResource;
import org.vai.com.utils.Consts;
import org.vai.com.utils.DownloadImageUtils;
import org.vai.com.utils.EmotionsUtils;

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
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeVerticalAdapter extends ArrayAdapter<ConferenceResource> {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private IAdapterCallBack mAdapterCallBack;
	private DownloadImageUtils mDownloadImage;
	private OnClickListener mOnClickListener;
	private ArrayList<ConferenceResource> mListConference = new ArrayList<ConferenceResource>();
	private int mContentWidth;
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions mSquareOptions = new DisplayImageOptions.Builder().showStubImage(R.color.image_loading)
			.showImageForEmptyUri(R.color.image_loading).showImageOnFail(R.color.image_loading).cacheInMemory(true)
			.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();

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

		mOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				int id = v.getId();
				ViewHolder viewHolder = (ViewHolder) v.getTag();
				switch (id) {
				case R.id.imgContent:
					Intent intent;
					if (TextUtils.isEmpty(viewHolder.conference.videoId)) {
						intent = new Intent(mContext, ImageViewDetailActivity.class);
						intent.putExtra(Consts.IMAGE_URL, viewHolder.conference.image);
					} else if (mContext.getPackageManager().getLaunchIntentForPackage(Consts.YOUTUBE_PACKAGE) != null) {
						intent = new Intent(mContext, YouTubePlayerActivity.class);
						intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, viewHolder.conference.videoId);
					} else {
						intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://"
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
					if (viewHolder.tvLike.isSelected()) { // Current state is liked.
						bundle.putInt(Consts.JSON_LIKE, Consts.STATE_OFF);
						bundle.putString(Consts.IMAGE_URL, viewHolder.conference.image);
					} else { // Current state is not liked.
						// TODO: call like.
						bundle.putInt(Consts.JSON_LIKE, Consts.STATE_ON);
						bundle.putString(Consts.IMAGE_URL, viewHolder.conference.image);
					}
					if (mAdapterCallBack != null) mAdapterCallBack.adapterCallBack(bundle);
					break;
				default:
					break;
				}
			}
		};
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_list_conference, null);
			viewHolder = new ViewHolder();
			viewHolder.emotionsUtils = new EmotionsUtils(mContext);
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			viewHolder.tvLike = (TextView) convertView.findViewById(R.id.tvLike);
			viewHolder.tvComment = (TextView) convertView.findViewById(R.id.tvComment);
			viewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
			viewHolder.imgContent = (ImageView) convertView.findViewById(R.id.imgContent);
			viewHolder.imgPlayYoutube = (ImageView) convertView.findViewById(R.id.imgPlayYoutube);
			viewHolder.imgDownload = (ImageView) convertView.findViewById(R.id.imgDownload);
			viewHolder.imgShare = (ImageView) convertView.findViewById(R.id.imgShare);

			convertView.setTag(viewHolder);
			viewHolder.imgContent.setTag(viewHolder);
			viewHolder.imgDownload.setTag(viewHolder);
			viewHolder.tvLike.setTag(viewHolder);
			viewHolder.imgContent.setOnClickListener(mOnClickListener);
			viewHolder.imgDownload.setOnClickListener(mOnClickListener);
			viewHolder.tvLike.setOnClickListener(mOnClickListener);
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
		} else {
			viewHolder.imgIcon.setImageResource(R.drawable.photo_icon);
			viewHolder.imgPlayYoutube.setVisibility(View.GONE);
		}
		if (Consts.STATE_ON == conferenceResource.likeState) {
			viewHolder.tvLike.setSelected(true);
		} else {
			viewHolder.tvLike.setSelected(false);
		}
		int imgHeight = mContentWidth;
		if (conferenceResource.imgWidth > 0) {
			imgHeight = mContentWidth * conferenceResource.imgHeight / conferenceResource.imgWidth;
		}
		viewHolder.imgContent.getLayoutParams().height = imgHeight;
		mImageLoader.displayImage(conferenceResource.image, viewHolder.imgContent, mSquareOptions);
		return convertView;
	}

	private class ViewHolder {
		ConferenceResource conference;
		EmotionsUtils emotionsUtils;
		TextView tvTitle;
		TextView tvLike;
		TextView tvComment;
		ImageView imgIcon;
		ImageView imgContent;
		ImageView imgPlayYoutube;
		ImageView imgDownload;
		ImageView imgShare;
	}
}

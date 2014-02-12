package org.vai.com.adapter;

import java.util.ArrayList;

import org.vai.com.R;
import org.vai.com.appinterface.IAdapterCallBack;
import org.vai.com.resource.home.ConferenceResource;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeContentAdapter extends ArrayAdapter<ConferenceResource> {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private IAdapterCallBack mAdapterCallBack;
	private ArrayList<ConferenceResource> mListConference = new ArrayList<ConferenceResource>();
	private int mContentWidth;
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions mSquareOptions = new DisplayImageOptions.Builder().showStubImage(R.color.image_loading)
			.showImageForEmptyUri(R.color.image_loading).showImageOnFail(R.color.image_loading).cacheInMemory(true)
			.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();

	public HomeContentAdapter(Context context, ArrayList<ConferenceResource> listConference,
			IAdapterCallBack adapterCallBack) {
		super(context, 0, listConference);
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mAdapterCallBack = adapterCallBack;
		mListConference = listConference;

		// Calculate content width.
		int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
		mContentWidth = screenWidth - 4 * mContext.getResources().getDimensionPixelSize(R.dimen.common_margin_small);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_list_conference, null);
			viewHolder = new ViewHolder();
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			viewHolder.tvLike = (TextView) convertView.findViewById(R.id.tvLike);
			viewHolder.tvComment = (TextView) convertView.findViewById(R.id.tvComment);
			viewHolder.imgContent = (ImageView) convertView.findViewById(R.id.imgContent);
			viewHolder.imgDownload = (ImageView) convertView.findViewById(R.id.imgDownload);
			viewHolder.imgShare = (ImageView) convertView.findViewById(R.id.imgShare);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// Show content.
		ConferenceResource conferenceResource = mListConference.get(position);
		viewHolder.tvTitle.setText(conferenceResource.title);
		viewHolder.tvLike.setText(conferenceResource.like + "");
		viewHolder.tvComment.setText(conferenceResource.comment + "");
		if (!TextUtils.isEmpty(conferenceResource.videoId)) {
			viewHolder.imgIcon.setImageResource(R.drawable.video_icon);
		} else {
			viewHolder.imgIcon.setImageResource(R.drawable.photo_icon);
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
		TextView tvTitle;
		TextView tvLike;
		TextView tvComment;
		ImageView imgIcon;
		ImageView imgContent;
		ImageView imgDownload;
		ImageView imgShare;
	}
}

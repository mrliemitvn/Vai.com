package org.vai.com.fragment;

import org.vai.com.R;
import org.vai.com.resource.home.ConferenceResource;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeContentHorizontalFragment extends BaseFragment {

	private View mParentView;
	private ImageView mImgContent;
	private ImageView mImgDownload;
	private ImageView mImgShare;
	private TextView mTvTitle;
	private TextView mTvLike;
	private TextView mTvComment;

	private ConferenceResource mConferenceResource;

	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions mSquareOptions = new DisplayImageOptions.Builder().showStubImage(R.color.image_loading)
			.showImageForEmptyUri(R.color.image_loading).showImageOnFail(R.color.image_loading).cacheInMemory(true)
			.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();

	/**
	 * Define view.
	 */
	private void init() {
		mImgContent = (ImageView) mParentView.findViewById(R.id.imgContent);
		mImgDownload = (ImageView) mParentView.findViewById(R.id.imgDownload);
		mImgShare = (ImageView) mParentView.findViewById(R.id.imgShare);
		mTvTitle = (TextView) mParentView.findViewById(R.id.tvTitle);
		mTvLike = (TextView) mParentView.findViewById(R.id.tvLike);
		mTvComment = (TextView) mParentView.findViewById(R.id.tvComment);
	}

	public void setConference(ConferenceResource conferenceResource) {
		mConferenceResource = conferenceResource;
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
		mTvTitle.setText(mConferenceResource.title);
		mTvLike.setText(mConferenceResource.like + "");
		mTvComment.setText(mConferenceResource.comment + "");
		int mContentWidth = getResources().getDisplayMetrics().widthPixels;
		int imgHeight = mContentWidth;
		if (mConferenceResource.imgWidth > 0) {
			imgHeight = mContentWidth * mConferenceResource.imgHeight / mConferenceResource.imgWidth;
		}
		mImgContent.getLayoutParams().height = imgHeight;
		mImageLoader.displayImage(mConferenceResource.image, mImgContent, mSquareOptions);
	}
}

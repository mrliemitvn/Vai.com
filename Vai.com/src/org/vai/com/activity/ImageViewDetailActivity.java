package org.vai.com.activity;

import org.vai.com.R;
import org.vai.com.utils.Consts;
import org.vai.com.views.TouchImageView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageViewDetailActivity extends SherlockActivity {
	private String urlImage = "";
	private TouchImageView mImgSaveImage;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions imgSquareOptions = new DisplayImageOptions.Builder()
			.showStubImage(R.color.image_loading).showImageForEmptyUri(R.color.image_loading)
			.showImageOnFail(R.color.image_loading).cacheInMemory(true).cacheOnDisc(true)
			.bitmapConfig(Bitmap.Config.RGB_565).build();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_view_detail);

		mImgSaveImage = (TouchImageView) findViewById(R.id.imgSaveImage);

		urlImage = getIntent().getExtras().getString(Consts.IMAGE_URL);
		imageLoader.displayImage(urlImage, mImgSaveImage, imgSquareOptions);

		mImgSaveImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}

package org.vai.com.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.vai.com.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class DownloadImageUtils {

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private Context context;

	public DownloadImageUtils(Context context) {
		this.context = context;
	}

	public void downloadImage(String url, final String fileName) {
		imageLoader.loadImage(url, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				File storagePath;
				if (isExternalStorageWritable()) {
					storagePath = new File(Environment.getExternalStorageDirectory(), Consts.APP_FOLDER);
				} else {
					storagePath = new File(context.getFilesDir(), Consts.APP_FOLDER);
				}
				if (!storagePath.exists()) storagePath.mkdirs();
				File path = new File(storagePath, fileName);
				saveBitmap(loadedImage, path);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
			}

			@Override
			public void onDownloadComplete(String downloadedFile, String url) {
			}
		});
	}

	/* Checks if external storage is available for read and write */
	private boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 * Save bitmap to file.
	 * 
	 * @param bm
	 *            Bitmap image.
	 * @param file
	 *            File to write bitmap to.
	 * @return true if write successfully
	 *         false otherwise.
	 */
	private boolean saveBitmap(Bitmap bm, File file) {
		boolean ret = false;
		try {
			FileOutputStream out = new FileOutputStream(file);
			ret = bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			ret = true;
			Toast.makeText(context, R.string.msg_info_download_complete, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}
}

package org.vai.com.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.vai.com.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * This class is used to download a image.
 */
public class DownloadImageUtils {

	/* Use {@link ImageLoader} to download image. */
	private ImageLoader imageLoader = ImageLoader.getInstance();

	/* Context to create save file. */
	private Context context;

	/**
	 * Constructor create {@link DownloadImageUtils} object.
	 * 
	 * @param context
	 *            context to set.
	 */
	public DownloadImageUtils(Context context) {
		this.context = context;
	}

	/**
	 * Method download a image from url and save to a file.
	 * 
	 * @param url
	 *            url of image.
	 * @param fileName
	 *            file image name want to save.
	 */
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
				// Loading complete, save bitmap loaded to a file.
				File storagePath;
				/*
				 * If has external storage, save image to it.
				 * Else save image to internal storage.
				 */
				if (isExternalStorageWritable()) { // File path is created from external storage.
					storagePath = new File(Environment.getExternalStorageDirectory(), Consts.APP_FOLDER);
				} else { // File path is created from internal storage.
					storagePath = new File(context.getFilesDir(), Consts.APP_FOLDER);
				}
				if (!storagePath.exists()) storagePath.mkdirs();
				File path = new File(storagePath, fileName);
				saveBitmap(loadedImage, path); // Save image from loaded bitmap.
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
			}
		});
	}

	/**
	 * Checks external storage is available for read and write or not.
	 * 
	 * @return true if external storage is available and false otherwise.
	 */
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
	 * @return true if write successfully and false otherwise.
	 */
	private boolean saveBitmap(Bitmap bm, File file) {
		boolean ret = false;
		try {
			FileOutputStream out = new FileOutputStream(file);
			ret = bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			ret = true;
			Toast toast = Toast.makeText(context, R.string.msg_info_download_complete, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}
}

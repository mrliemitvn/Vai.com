package org.vai.com;

import java.io.File;

import org.vai.com.provider.SharePrefs;
import org.vai.com.utils.Consts;
import org.vai.com.utils.LruBitmapCache;
import org.vai.com.utils.NetworkUtils;
import org.vai.com.utils.VaiUtils;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class VaiApplication extends Application {

	private static final int DISK_CACHE_SIZE = 200 * 1024 * 1024; // 200 MB

	private static Context context = null;

	private void InitImageLoaderConfiguration() {
		int size[] = VaiUtils.getScreenSize(this);
		// Neu do ngang lon hon do dai thi swap
		if (size[0] > size[1]) {
			int temp = size[0];
			size[0] = size[1];
			size[1] = temp;
		}
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory());

		// Use 1/4th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 4 / 1024;

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.threadPoolSize(3).memoryCache(new LruBitmapCache(cacheSize)).discCacheSize(DISK_CACHE_SIZE)
				.threadPriority(Thread.NORM_PRIORITY).discCacheFileNameGenerator(new HashCodeFileNameGenerator())
				.memoryCacheExtraOptions(size[0], size[1]).tasksProcessingOrder(QueueProcessingType.LIFO).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		// set slow downloader for 3G or slow network
		if (NetworkUtils.getConnectivityStatus(getApplicationContext()) != NetworkUtils.TYPE_WIFI) {
			ImageLoader.getInstance().handleSlowNetwork(true);
		} else {
			ImageLoader.getInstance().handleSlowNetwork(false);
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		context = getApplicationContext();

		InitImageLoaderConfiguration();

		SharePrefs.getInstance().init(this);

		// create temp path
		File file = new File(Consts.TEMP_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}

		NetworkUtils.enableHttpResponseCache(this);
	}

	public static Context getAppContext() {
		return context;
	}
}

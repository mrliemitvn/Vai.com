package org.vai.com;

import org.vai.com.provider.SharePrefs;
import org.vai.com.utils.LruBitmapCache;
import org.vai.com.utils.NetworkUtils;
import org.vai.com.utils.VaiUtils;

import android.app.Application;
import android.content.Context;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.google.analytics.tracking.android.Logger.LogLevel;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class VaiApplication extends Application {

	private static final int DISK_CACHE_SIZE = 200 * 1024 * 1024; // 200 MB

	private static Context context = null;

	private static GoogleAnalytics mGa;
	private static Tracker mTracker;

	/*
	 * Google Analytics configuration values.
	 */
	// Tracking ID Google analytics official.
	private static final String GA_PROPERTY_ID = "UA-4462457-41";

	// Dispatch period in seconds.
	private static final int GA_DISPATCH_PERIOD = 30;

	// Prevent hits from being sent to reports, i.e. during testing.
	private static final boolean GA_IS_DRY_RUN = false;

	// GA opt out flag.
	private static final boolean GA_OPT_OUT_FLAG = false; // If want to disable GA, set 'true'.

	// GA Logger verbosity.
	private static final LogLevel GA_LOG_VERBOSITY = LogLevel.INFO;

	/*
	 * Method to handle basic Google Analytics initialization. This call will not
	 * block as all Google Analytics work occurs off the main thread.
	 */
	private void initializeGa() {
		mGa = GoogleAnalytics.getInstance(this);
		mTracker = mGa.getTracker(GA_PROPERTY_ID);

		// Set dispatch period.
		GAServiceManager.getInstance().setLocalDispatchPeriod(GA_DISPATCH_PERIOD);

		// Set dryRun flag.
		mGa.setDryRun(GA_IS_DRY_RUN);

		// Set Logger verbosity.
		mGa.getLogger().setLogLevel(GA_LOG_VERBOSITY);

		// Set the opt out flag.
		GoogleAnalytics.getInstance(this).setAppOptOut(GA_OPT_OUT_FLAG);

		// Send a single hit with session control to start the new session.
		mTracker.send(MapBuilder.createEvent("UX", "appstart", null, null).set(Fields.SESSION_CONTROL, "start").build());
	}

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

		initializeGa(); // Init google anlytics.

		context = getApplicationContext();

		InitImageLoaderConfiguration();

		SharePrefs.getInstance().init(this);

		NetworkUtils.enableHttpResponseCache(this);
	}

	public static Context getAppContext() {
		return context;
	}

	/*
	 * Returns the Google Analytics tracker.
	 */
	public static Tracker getGaTracker() {
		return mTracker;
	}

	/*
	 * Returns the Google Analytics instance.
	 */
	public static GoogleAnalytics getGaInstance() {
		return mGa;
	}
}

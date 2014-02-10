package org.vai.com.utils;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * A collection of network related utilities
 */
public class NetworkUtils {
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    /**
     * Get connectivity status
     * 
     * @param context
     * @return TYPE_WIFI, TYPE_MOBILE, TYPE_NOT_CONNECTED
     */
    public static int getConnectivityStatus(final Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork && activeNetwork.isConnectedOrConnecting()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    /**
     * Disables HTTP connection reuse in Donut and below, as it was buggy From:
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     */
    public static void disableConnectionReuseIfNecessary() {
        if (Integer.parseInt(Build.VERSION.SDK) <= Build.VERSION_CODES.DONUT) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    /**
     * Enables built-in http cache beginning in ICS From:
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     * 
     * @param application
     */
    public static void enableHttpResponseCache(final Application application) {
        try {
            final long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            final File httpCacheDir = new File(application.getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache").getMethod("install", File.class, long.class)
                .invoke(null, httpCacheDir, httpCacheSize);
        } catch (final Exception httpResponseCacheNotAvailable) {
        }
    }
}
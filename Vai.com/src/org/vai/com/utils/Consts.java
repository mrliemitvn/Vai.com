package org.vai.com.utils;

import java.io.File;

import android.net.Uri;
import android.os.Environment;

public class Consts {

	// Callback upload progress
	public static final String EXTRA_UPLOAD_PROGRESS = "org.vai.com.rest.EXTRA_UPLOAD_PROGRESS";
	public static final String EXTRA_UPLOAD_DATA = "org.vai.com.rest.EXTRA_UPLOAD_DATA";

	public static final String BASE_PATH = Environment.getExternalStorageDirectory().toString() + File.separator
			+ "org.vai.com" + File.separator;
	public static final String TEMP_PATH = BASE_PATH + "temp" + File.separator;

	/* Json string */
	public static final String JSON_ID = "id";
	public static final String JSON_NAME = "name";
	public static final String JSON_CATEGORY = "category";

	/* CONST URI */
	public static final class UriConsts {
		public static final String AUTHORITY = "org.vai.com.uriauthority";

		private static Uri createUri(String path) {
			return Uri.parse("content://" + AUTHORITY + "/" + path);
		}

		// For get category api.
		public static final String PATH_GET_CATEGORY_API = "GET_CATEGORY_API";
		public static final Uri CONTENT_URI_GET_CATEGORY_API = createUri(PATH_GET_CATEGORY_API);
		public static final int RESOURCE_TYPE_GET_CATEGORY_API = 1;
	}

	// class URL
	public class URLConstants {

		/** Youtube thumbnail url */
		public static final String YOUTUBE_VIDEO_THUMB_URL = "http://img.youtube.com/vi/{id}/0.jpg";

		public static final String BASE_URL = "http://xn--vi-sia.com/";
		public static final String CLIENT_KEY = "vnvmcnjhbjch-mmqadtyiocdefb";

		// Get category url.
		public static final String GET_CATEGORY_URL = BASE_URL + "api/app/mobile/vai.com/config.php?key=" + CLIENT_KEY;
	}
}

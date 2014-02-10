package org.vai.com.utils;

import android.net.Uri;

public class Consts {
	/* CONST URI */
	public static final class UriConsts {
		public static final String AUTHORITY = "com.cnc.ubox.uriauthority";

		private static Uri createUri(String path) {
			return Uri.parse("content://" + AUTHORITY + "/" + path);
		}

		// verification
		public static final String PATH_VERIFICATION = "VERIFICATION";
		public static final Uri CONTENT_URI_VERIFICATION = createUri(PATH_VERIFICATION);
		public static final int RESOURCE_TYPE_VERIFICATION = 1;
	}

	// class URL
	public class URLConstants {

		/** Youtube thumbnail url */
		public static final String YOUTUBE_VIDEO_THUMB_URL = "http://img.youtube.com/vi/{id}/0.jpg";

		public static final String BASE_URL = "http://123.30.235.152/";
	}

	// Callback upload progress
	public static final String EXTRA_UPLOAD_PROGRESS = "com.cnc.ubox.rest.EXTRA_UPLOAD_PROGRESS";
	public static final String EXTRA_UPLOAD_DATA = "com.cnc.ubox.rest.EXTRA_UPLOAD_DATA";
}

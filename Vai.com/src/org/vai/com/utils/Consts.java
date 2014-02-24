package org.vai.com.utils;

import android.net.Uri;

public class Consts {

	/*
	 * For GA.
	 */
	public static final String MENU_SELECTED = "MENU_SELECTED";

	public static final String YOUTUBE_PACKAGE = "com.google.android.youtube";
	public static final String APP_FOLDER = "Vai.com";
	public static final String IMAGE_FILE_NAME = "vai_com_";
	public static final String IMAGE_FILE_JPG_TYPE = ".jpg";
	public static final String IMAGE_URL = "image_url";
	public static final String SHARE_CONFERENCE = "share_conference";
	public static final int STATE_ON = 1;
	public static final int STATE_OFF = 0;
	public static final int STATE_UNKNOWN = -1;
	public static final int MAX_SCROLL_DIFF = 5;

	// Callback upload progress
	public static final String EXTRA_UPLOAD_PROGRESS = "org.vai.com.rest.EXTRA_UPLOAD_PROGRESS";
	public static final String EXTRA_UPLOAD_DATA = "org.vai.com.rest.EXTRA_UPLOAD_DATA";

	/* Json string */
	public static final String JSON_ID = "id";
	public static final String JSON_NAME = "name";
	public static final String JSON_CATEGORY = "category";
	public static final String JSON_TITLE = "title";
	public static final String JSON_TITLE_ASCII = "title_ascii";
	public static final String JSON_ALIAS = "alias";
	public static final String JSON_INTRO = "intro";
	public static final String JSON_CONTENT = "content";
	public static final String JSON_AUTHOR = "author";
	public static final String JSON_TIME_CREATED = "time_created";
	public static final String JSON_TIME_MODIFIED = "time_modified";
	public static final String JSON_OPTIONS = "options";
	public static final String JSON_VIEWED = "viewed";
	public static final String JSON_STATUS = "status";
	public static final String JSON_CATEGORY_ID = "category_id";
	public static final String JSON_POST_ID = "post_id";
	public static final String JSON_LIKE = "like";
	public static final String JSON_COMMENT = "comment";
	public static final String JSON_PAGE = "page";
	public static final String JSON_MOREWEB = "moreweb";
	public static final String JSON_ADS = "ads";
	public static final String JSON_FBAPPID = "fbappid";

	/* Place holder string */
	public static final String CATEGORY_PLACE_HOLDER = "{category}";
	public static final String ID_PLACE_HOLDER = "{id}";

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
		// For get conference api.
		public static final String PATH_GET_CONFERENCE_API = "GET_CONFERENCE_API";
		public static final Uri CONTENT_URI_GET_CONFERENCE_API = createUri(PATH_GET_CONFERENCE_API);
		public static final int RESOURCE_TYPE_GET_CONFERENCE_API = 2;
	}

	// class URL
	public class URLConstants {

		/** Youtube thumbnail url */
		public static final String YOUTUBE_VIDEO_THUMB_URL = "http://img.youtube.com/vi/{id}/0.jpg";

		public static final String BASE_URL = "http://xn--vi-sia.com/";
		public static final String CLIENT_KEY = "vnvmcnjhbjch-mmqadtyiocdefb";

		// Url load comment on webview.
		public static final String URL_LOAD_COMMENT = "http://xn--vi-sia.com/api/app/mobile/vai.com/fbcomment.php?id={id}&w=750&appid=137092776349374";

		// Get category url.
		public static final String GET_CATEGORY_URL = BASE_URL + "api/app/mobile/vai.com/config.php?key=" + CLIENT_KEY;
		// Get conference url.
		public static final String GET_CONFERENCE_URL = BASE_URL + "api/app/mobile/vai.com/content.php?key="
				+ CLIENT_KEY + "&cate={category}&paged=";
	}
}

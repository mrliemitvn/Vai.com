package org.vai.com.utils;

import android.net.Uri;

/**
 * This class declare all constant variables.
 */
public class Consts {

	/* For GA tracking. */
	public static final String MENU_SELECTED = "MENU_SELECTED";

	/* For GCM, push notification. */
	public static final String PUSH_NOTIFICATION_SENDER_ID = "";
	public static final String GCM_BROADCAST_RECEIVER = "GCM_Broadcast_Receiver";
	public static final String GCM_INTENT_REGISTER_ID = "GCMRegisterId";

	public static final String YOUTUBE_PACKAGE = "com.google.android.youtube";
	public static final String APP_FOLDER = "Vai.com";
	public static final String IMAGE_FILE_NAME = "vai_com_";
	public static final String IMAGE_FILE_JPG_TYPE = ".jpg";
	public static final String IMAGE_FILE_GIF_TYPE = ".gif";
	public static final String IMAGE_URL = "image_url";
	public static final String SHARE_CONFERENCE = "share_conference";
	public static final int STATE_ON = 1;
	public static final int STATE_OFF = 0;
	public static final int STATE_UNKNOWN = -1;
	public static final int MAX_SCROLL_DIFF = 5;

	// Callback upload progress
	public static final String EXTRA_UPLOAD_PROGRESS = "org.vai.com.rest.EXTRA_UPLOAD_PROGRESS";
	public static final String EXTRA_UPLOAD_DATA = "org.vai.com.rest.EXTRA_UPLOAD_DATA";

	/* Json string data */
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
	public static final String JSON_LIKE_NUMBER = "like_number";
	public static final String JSON_COMMENT = "comment";
	public static final String JSON_PAGE = "page";
	public static final String JSON_MOREWEB = "moreweb";
	public static final String JSON_ADS = "ads";
	public static final String JSON_FBAPPID = "fbappid";
	public static final String JSON_DEVICE = "device";
	public static final String JSON_OS = "os";

	/* Place holder string */
	public static final String CATEGORY_PLACE_HOLDER = "{category}";
	public static final String ID_PLACE_HOLDER = "{id}";

	/**
	 * This class handle push notification.
	 */
	public static class GCM_RECEIVE {
		public static final String PUSH_NOTIFICATION_SENDER_ID = "469929446565";
		public static final String GCM_INTENT_REGISTER_ID = "GCMRegisterId";
		public static final String GCM_BROADCAST_RECEIVER = "GCM_Broadcast_Receiver";

		public static final String MESSAGE = "message";
		public static final String ID = "object_id";
		public static final String TYPE = "type";

		public static final String NEW_VERSION = "new_version";
	}

	/**
	 * This class declare all constant uri will be used in rest client process.
	 */
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
		// For post GCM token api.
		public static final String PATH_POST_GCM_TOKEN_API = "POST_GCM_TOKEN_API";
		public static final Uri CONTENT_URI_POST_GCM_TOKEN_API = createUri(PATH_POST_GCM_TOKEN_API);
		public static final int RESOURCE_TYPE_POST_GCM_TOKEN__API = 3;
	}

	/**
	 * This class declare all constant url will be used in application.
	 */
	public class URLConstants {

		/* Youtube video watching */
		public static final String YOUTUBE_VIDEO_WATCHING_URL = "http://www.youtube.com/watch?v=";

		/* Server url and client key */
		public static final String BASE_URL = "http://xn--vi-sia.com/";
		public static final String CLIENT_KEY = "vnvmcnjhbjch-mmqadtyiocdefb";

		// Url load comment on webview.
		public static final String URL_LOAD_COMMENT = "http://xn--vi-sia.com/api/app/mobile/vai.com/fbcomment.php?id={id}&w=750&appid=137092776349374";

		// Post GCM token.
		public static final String POST_GCM_TOKEN_URL = BASE_URL + "api/app/mobile/vai.com/push.php?key=" + CLIENT_KEY;
		// Get category url.
		public static final String GET_CATEGORY_URL = BASE_URL + "api/app/mobile/vai.com/config.php?key=" + CLIENT_KEY;
		// Get conference url.
		public static final String GET_CONFERENCE_URL = BASE_URL + "api/app/mobile/vai.com/content.php?key="
				+ CLIENT_KEY + "&cate={category}&paged=";
	}
}

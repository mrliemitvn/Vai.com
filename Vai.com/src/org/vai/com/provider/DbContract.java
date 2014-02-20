package org.vai.com.provider;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * This class describes all the tables' columns and content type for each table.
 * 
 */
public class DbContract {
	public static final String CONTENT_AUTHORITY = "org.vai.com";

	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	/**
	 * Concatenate table name to column name
	 * 
	 * @param tableName
	 * @param column
	 *            column name
	 * @return
	 */
	public static String getQualifiedColumnName(String tableName, String column) {
		return tableName + "." + column;
	}

	/**
	 * Get the alias of the column, use in sql query to distinguish columns.
	 * 
	 * @param table
	 *            table name
	 * @param column
	 *            column name
	 * @return
	 */
	public static String getAlias(String table, String column) {
		return table + "_" + column;
	}

	/**
	 * path to construct uri in content provider (DbProvider)
	 */
	static final String PATH_CATEGORY = "category";
	static final String PATH_CONFERENCE = "conference";
	static final String PATH_MORE_WEB = "more_web";
	static final String PATH_LIKE_STATE = "like_state";
	static final String PATH_CONFERENCE_JOIN_LIKE_STATE = "conference_join_like_state";

	public static class Category implements ResourceTable {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.vai.category";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.vai.category";
		/** Category name */
		public static final String NAME = "name";
	}

	public static class Conference implements ResourceTable {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONFERENCE).build();
		public static final Uri CONTENT_URI_CONFERENCE_JOIN_LIKE_STATE = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_CONFERENCE_JOIN_LIKE_STATE).build();
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.vai.conference";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.vai.conference";
		/** Post id */
		public static final String POST_ID = "post_id";
		/** Category id */
		public static final String CATEGORY_ID = "category_id";
		/** Title */
		public static final String TITLE = "title";
		/** Title ascii */
		public static final String TITLE_ASCII = "title_ascii";
		/** Alias */
		public static final String ALIAS = "alias";
		/** Intro */
		public static final String INTRO = "intro";
		/** Youtube video id */
		public static final String VIDEO_ID = "video_id";
		/** image URL */
		public static final String IMAGE = "image";
		/** Author */
		public static final String AUTHOR = "author";
		/** image width */
		public static final String IMAGE_WIDTH = "image_width";
		/** image height */
		public static final String IMAGE_HEIGHT = "image_height";
		/** time created */
		public static final String TIME_CREATED = "time_created";
		/** time modified */
		public static final String TIME_MODIFIED = "time_modified";
		/** viewed */
		public static final String VIEWED = "viewed";
		/** like */
		public static final String LIKE = "like";
		/** comment */
		public static final String COMMENT = "comment";
	}

	public static class MoreWeb implements ResourceTable {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MORE_WEB).build();
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.vai.more_web";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.vai.more_web";
		/** Web name */
		public static final String NAME = "name";
		/** Web link */
		public static final String LINK = "link";
	}

	public static class LikeState implements ResourceTable {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LIKE_STATE).build();
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.vai.like_state";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.vai.like_state";
		/** Facebook user id */
		public static final String FACEBOOK_USER_ID = "facebook_user_id";
		/** Like state */
		public static final String LIKE_STATE = "like_state";
	}
}

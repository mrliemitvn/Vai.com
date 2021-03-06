package org.vai.com.resource.home;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.vai.com.provider.DbContract;
import org.vai.com.provider.DbContract.Conference;
import org.vai.com.provider.DbContract.LikeState;
import org.vai.com.provider.DbHelper.Tables;
import org.vai.com.resource.BaseResource;
import org.vai.com.resource.Resource;
import org.vai.com.utils.Consts;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

public class ConferenceResource implements BaseResource, Resource {

	/* Pattern string to get youtube video id. */
	private static final String strPatternVideoId = "\\[youtube\\](.*?)\\[/youtube\\]";

	/* Pattern string to get image url, image width and image height. */
	private static final String strPatternImageInfo = "img=(.*?);width=(.*?);height=(.*?);";

	/* Conference id. */
	public String id;

	/* Post id of conference. */
	public String postId;

	/* Category id of conference. */
	public String categoryId;

	/* Conference title. */
	public String title;

	/* Conference title in ASCII encode. */
	public String titleAscii;

	/* Conference alias. */
	public String alias;

	/* Conference intro. */
	public String intro;

	/* Conference content. */
	public String content;

	/* Conference video id (youtube video id). */
	public String videoId;

	/* Conference image (image url). */
	public String image;

	/* Conference status. */
	public String status;

	/* Conference author. */
	public int author;

	/* Image width. */
	public int imgWidth;

	/* Image height. */
	public int imgHeight;

	/* Conference like state. */
	public int likeState;

	/* Conference time created. */
	public long timeCreated;

	/* Conference time modified. */
	public long timeModified;

	/* View number of conference. */
	public long viewed;

	/* Like number of conference. */
	public long like;

	/* Comment number of conference. */
	public long comment;

	/**
	 * Create {@link ConferenceResource} object from json data received from server.
	 * 
	 * @param json
	 *            json data received from server.
	 */
	public ConferenceResource(JSONObject json) {
		if (json != null) {
			try {
				/* Parse conference id data. */
				if (!json.isNull(Consts.JSON_ID)) {
					id = json.getString(Consts.JSON_ID);
				}
				/* Parse post id of conference data. */
				if (!json.isNull(Consts.JSON_POST_ID)) {
					postId = json.getString(Consts.JSON_POST_ID);
				}
				/* Parse category id of conference data. */
				if (!json.isNull(Consts.JSON_CATEGORY_ID)) {
					categoryId = json.getString(Consts.JSON_CATEGORY_ID);
				}
				/* Parse category title. */
				if (!json.isNull(Consts.JSON_TITLE)) {
					title = json.getString(Consts.JSON_TITLE).replace("\\", ""); // Remove "\" character.
				}
				/* Parse category title in ASCII encode. */
				if (!json.isNull(Consts.JSON_TITLE_ASCII)) {
					titleAscii = json.getString(Consts.JSON_TITLE_ASCII).replace("\\", ""); // Remove "\" character.
				}
				/* Parse category alias. */
				if (!json.isNull(Consts.JSON_ALIAS)) {
					alias = json.getString(Consts.JSON_ALIAS);
				}
				/* Parse category intro. */
				if (!json.isNull(Consts.JSON_INTRO)) {
					intro = json.getString(Consts.JSON_INTRO);
				}
				/* Parse category content or youtube video id. */
				if (!json.isNull(Consts.JSON_CONTENT)) {
					/* Parse category content. */
					String content = json.getString(Consts.JSON_CONTENT);
					/* Use pattern to get youtube video id. */
					Pattern pattern = Pattern.compile(strPatternVideoId, Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(content);
					while (matcher.find()) {
						videoId = matcher.group(1);
					}
					/* If not got youtube video id, save conference content. */
					if (TextUtils.isEmpty(videoId)) this.content = content.replace("\\", ""); // Remove "\" character.
				}
				/* Parse image url, image width and image height. */
				if (!json.isNull(Consts.JSON_OPTIONS)) {
					String imgInfo = json.getString(Consts.JSON_OPTIONS) + ";";
					Pattern pattern = Pattern.compile(strPatternImageInfo, Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(imgInfo);
					while (matcher.find()) {
						/* Parse image url. */
						if (!TextUtils.isEmpty(matcher.group(1))) image = matcher.group(1);
						/* Parse image width. */
						if (!TextUtils.isEmpty(matcher.group(2))) imgWidth = Integer.parseInt(matcher.group(2));
						/* Parse image height. */
						if (!TextUtils.isEmpty(matcher.group(3))) imgHeight = Integer.parseInt(matcher.group(3));
					}
				}
				/* Parse conference status. */
				if (!json.isNull(Consts.JSON_STATUS)) {
					status = json.getString(Consts.JSON_STATUS);
				}
				/* Parse conference author. */
				if (!json.isNull(Consts.JSON_AUTHOR)) {
					author = json.getInt(Consts.JSON_AUTHOR);
				}
				/* Parse conference time created. */
				if (!json.isNull(Consts.JSON_TIME_CREATED)) {
					timeCreated = json.getLong(Consts.JSON_TIME_CREATED);
				}
				/* Parse conference time modified. */
				if (!json.isNull(Consts.JSON_TIME_MODIFIED)) {
					timeModified = json.getLong(Consts.JSON_TIME_MODIFIED);
				}
				/* Parse view number of conference. */
				if (!json.isNull(Consts.JSON_VIEWED)) {
					viewed = json.getLong(Consts.JSON_VIEWED);
				}
				/* Parse like number of conference. */
				if (!json.isNull(Consts.JSON_LIKE)) {
					like = json.getLong(Consts.JSON_LIKE);
				}
				/* Parse comment number of conference. */
				if (!json.isNull(Consts.JSON_COMMENT)) {
					comment = json.getLong(Consts.JSON_COMMENT);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Create {@link ConferenceResource} from database.
	 * 
	 * @param cursor
	 *            cursor from database.
	 */
	public ConferenceResource(Cursor cursor) {
		int idIndex = cursor.getColumnIndex(Conference._ID);
		int postIdIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.POST_ID));
		int categoryIdIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.CATEGORY_ID));
		int titleIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.TITLE));
		int titleAsciiIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.TITLE_ASCII));
		int aliasIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.ALIAS));
		int introIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.INTRO));
		int contentIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.CONTENT));
		int videoIdIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.VIDEO_ID));
		int imageIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.IMAGE));
		int authorIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.AUTHOR));
		int imageWidthIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.IMAGE_WIDTH));
		int imageHeightIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.IMAGE_HEIGHT));
		int timeCreatedIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.TIME_CREATED));
		int timeModifiedIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.TIME_MODIFIED));
		int viewedIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.VIEWED));
		int likeIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.LIKE));
		int commentIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.CONFERENCE, Conference.COMMENT));
		int likeStateIndex = cursor.getColumnIndex(DbContract.getAlias(Tables.LIKE_STATE, LikeState.LIKE_STATE));

		if (idIndex > -1) id = cursor.getString(idIndex);
		if (postIdIndex > -1) postId = cursor.getString(postIdIndex);
		if (categoryIdIndex > -1) categoryId = cursor.getString(categoryIdIndex);
		if (titleIndex > -1) title = cursor.getString(titleIndex);
		if (titleAsciiIndex > -1) titleAscii = cursor.getString(titleAsciiIndex);
		if (aliasIndex > -1) alias = cursor.getString(aliasIndex);
		if (introIndex > -1) intro = cursor.getString(introIndex);
		if (contentIndex > -1) content = cursor.getString(contentIndex);
		if (videoIdIndex > -1) videoId = cursor.getString(videoIdIndex);
		if (imageIndex > -1) image = cursor.getString(imageIndex);
		if (authorIndex > -1) author = cursor.getInt(authorIndex);
		if (imageWidthIndex > -1) imgWidth = cursor.getInt(imageWidthIndex);
		if (imageHeightIndex > -1) imgHeight = cursor.getInt(imageHeightIndex);
		if (timeCreatedIndex > -1) timeCreated = cursor.getLong(timeCreatedIndex);
		if (timeModifiedIndex > -1) timeModified = cursor.getLong(timeModifiedIndex);
		if (viewedIndex > -1) viewed = cursor.getLong(viewedIndex);
		if (likeIndex > -1) like = cursor.getLong(likeIndex);
		if (commentIndex > -1) comment = cursor.getLong(commentIndex);
		if (likeStateIndex > -1) likeState = cursor.getInt(likeStateIndex);
	}

	/**
	 * Prepare {@link ContentValues} to insert to database.
	 */
	@Override
	public ContentValues prepareContentValues() {
		ContentValues values = new ContentValues();
		values.put(Conference._ID, id);
		values.put(Conference.POST_ID, postId);
		values.put(Conference.CATEGORY_ID, categoryId);
		values.put(Conference.TITLE, title);
		values.put(Conference.TITLE_ASCII, titleAscii);
		values.put(Conference.ALIAS, alias);
		values.put(Conference.INTRO, intro);
		values.put(Conference.CONTENT, content);
		values.put(Conference.VIDEO_ID, videoId);
		values.put(Conference.IMAGE, image);
		values.put(Conference._STATUS, status);
		values.put(Conference.AUTHOR, author);
		values.put(Conference.IMAGE_WIDTH, imgWidth);
		values.put(Conference.IMAGE_HEIGHT, imgHeight);
		values.put(Conference.TIME_CREATED, timeCreated);
		values.put(Conference.TIME_MODIFIED, timeModified);
		values.put(Conference.VIEWED, viewed);
		values.put(Conference.LIKE, like);
		values.put(Conference.COMMENT, comment);
		return values;
	}

	@Override
	public String getRestRequestStatus() {
		return null;
	}

}

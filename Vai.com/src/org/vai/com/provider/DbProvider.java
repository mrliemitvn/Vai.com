package org.vai.com.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.vai.com.provider.DbContract.Category;
import org.vai.com.provider.DbContract.Conference;
import org.vai.com.provider.DbHelper.Tables;
import org.vai.com.utils.Logger;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DbProvider extends ContentProvider {
	private static final String TAG = DbProvider.class.getSimpleName();
	private DbHelper mDbHelper;
	private UriMatcher mUriMatcher = buildUriMatcher();

	private static final int CATEGORY = 0;
	private static final int CONFERENCE = 1;

	/**
	 * Build and return a {@link UriMatcher} that catches all {@link Uri} variations supported by this
	 * {@link ContentProvider}.
	 */
	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = DbContract.CONTENT_AUTHORITY;
		/** Uri for category. */
		matcher.addURI(authority, DbContract.PATH_CATEGORY, CATEGORY);
		matcher.addURI(authority, DbContract.PATH_CONFERENCE, CONFERENCE);
		return matcher;
	}

	@Override
	public boolean onCreate() {
		this.mDbHelper = new DbHelper(this.getContext());
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String getType(Uri uri) {
		final int match = mUriMatcher.match(uri);
		switch (match) {
		case CATEGORY:
			return Category.CONTENT_TYPE;
		case CONFERENCE:
			return Conference.CONTENT_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	/**
	 * Build a simple {@link SelectionBuilder} to match the requested {@link Uri}. This is usually enough to support
	 * {@link #insert}, {@link #update}, and {@link #delete} operations.
	 */
	private SQLiteQueryBuilder buildSimpleSelection(Uri uri) {
		final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		final int match = mUriMatcher.match(uri);
		switch (match) {
		case CATEGORY: {
			builder.setTables(Tables.CATEGORY);
			return builder;
		}
		case CONFERENCE: {
			builder.setTables(Tables.CONFERENCE);
			return builder;
		}
		default: {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		}
	}

	/**
	 * Build an advanced {@link SQLiteQueryBuilder} to match the requested {@link Uri}. This is usually only used by
	 * {@link #query}, since it performs table joins useful for {@link Cursor} data.
	 */
	private SQLiteQueryBuilder buildExpandedSelection(Uri uri, int match) {
		final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		switch (match) {
		case CATEGORY: {
			builder.setTables(Tables.CATEGORY);
			return builder;
		}
		case CONFERENCE: {
			builder.setTables(Tables.CONFERENCE);
			return builder;
		}
		// case FRIEND: {
		// builder.setTables(Tables.USER_JOIN_FRIENDSHIP);
		// HashMap<String, String> columnMap = buildFriendColumnMap();
		// builder.setProjectionMap(columnMap);
		// builder.setDistinct(true);
		// return builder;
		// }
		default: {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		}
	}

	/**
	 * Build the column map for query a friend from table users joined with table friendship
	 * 
	 * @return column map with key is the column name, value is the alias of the column
	 */
	private HashMap<String, String> buildFriendColumnMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		// String userProjection[] = { Users._ID, Users._STATUS, Users.AVATAR, Users.AVATAR_THUMB, Users.COVER,
		// Users.BIRTH_DAY, Users.FACEBOOK_ID, Users.LEVEL, Users.NAME, Users.NOTE, Users.ONLINE, Users.PHONE,
		// Users.SEX, Users.TOTAL_FRIENDS, Users.TOTAL_MUTUAL_FRIENDS, Users.TOTAL_TAG_TOPICS,
		// Users.TOTAL_TAG_LOCATIONS, Users.TOTAL_FOLLOWERS, Users.TWITTER_ID, Users.AREA_CODE };
		// for (String col : userProjection) {
		// String qualifiedCol = UboxContract.getQualifiedColumnName(Tables.USERS, col);
		// String alias = UboxContract.getAlias(Tables.USERS, col);
		// map.put(qualifiedCol, qualifiedCol + " AS " + alias);
		// }
		// String friendshipProjection[] = { Friendships.FID, Friendships.UID, Friendships.FRIENDSHIP_STATUS };
		// for (String col : friendshipProjection) {
		// String qualifiedCol = UboxContract.getQualifiedColumnName(Tables.FRIENDSHIPS, col);
		// String alias = UboxContract.getAlias(Tables.FRIENDSHIPS, col);
		// map.put(qualifiedCol, qualifiedCol + " AS " + alias);
		// }
		return map;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
		Logger.debug(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) + ")");
		final SQLiteDatabase db = mDbHelper.getReadableDatabase();
		int match = mUriMatcher.match(uri);
		switch (match) {
		default: {
			// Most cases are handled with simple SQLiteQueryBuilder
			final SQLiteQueryBuilder builder = buildExpandedSelection(uri, match);
			Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, orderBy);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		}
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Logger.debug(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
		final SQLiteDatabase db = mDbHelper.getWritableDatabase();
		final int match = mUriMatcher.match(uri);
		switch (match) {
		case CATEGORY: {
			long id = db.insertWithOnConflict(Tables.CATEGORY, null, values, SQLiteDatabase.CONFLICT_IGNORE);
			// Notify any watchers of the change
			Uri newUri = ContentUris.withAppendedId(uri, id);
			Logger.debug(TAG, "Insert to uri: " + newUri.toString());
			return newUri;
		}
		case CONFERENCE: {
			long id = db.insertWithOnConflict(Tables.CONFERENCE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
			// Notify any watchers of the change
			Uri newUri = ContentUris.withAppendedId(uri, id);
			Logger.debug(TAG, "Insert to uri: " + newUri.toString());
			return newUri;
		}
		default: {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		}
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] contentValues) {
		// ContentValues array is null or empty, do nothing.
		if (contentValues == null || contentValues.length <= 0) return 0;
		final SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.beginTransaction();
		final int match = mUriMatcher.match(uri);
		try {
			switch (match) {
			case CATEGORY:
				for (ContentValues valuesCategory : contentValues) {
					if (valuesCategory != null && valuesCategory.size() > 0) {
						long id = db.insertWithOnConflict(Tables.CATEGORY, null, valuesCategory,
								SQLiteDatabase.CONFLICT_REPLACE);
						Logger.debug(TAG, "bulkInsert database CATEGORY " + id);
					}
				}
				break;
			case CONFERENCE:
				for (ContentValues valuesConference : contentValues) {
					if (valuesConference != null && valuesConference.size() > 0) {
						long id = db.insertWithOnConflict(Tables.CONFERENCE, null, valuesConference,
								SQLiteDatabase.CONFLICT_REPLACE);
						Logger.debug(TAG, "bulkInsert database CONFERENCE " + id);
					}
				}
				break;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
			}

			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(TAG, "ERROR when insert to database: " + e.getMessage());
		} finally {
			db.endTransaction();
		}
		return contentValues.length;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Logger.debug(TAG, "delete(uri=" + uri + ")");

		if (uri == DbContract.BASE_CONTENT_URI) {
			// Handle whole database deletes (e.g. when signing out)
			deleteDatabase();
			return 1;
		}
		final SQLiteDatabase db = mDbHelper.getWritableDatabase();
		final SQLiteQueryBuilder builder = buildSimpleSelection(uri);
		int retVal = db.delete(builder.getTables(), selection, selectionArgs);
		return retVal;
	}

	/**
	 * Delete database
	 */
	private void deleteDatabase() {
		// TODO: wait for content provider operations to finish, then tear down
		mDbHelper.close();
		Context context = getContext();
		DbHelper.deleteDatabase(context);
		mDbHelper = new DbHelper(getContext());
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		Logger.debug(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
		final SQLiteDatabase db = mDbHelper.getWritableDatabase();
		final SQLiteQueryBuilder builder = buildSimpleSelection(uri);
		int retVal = 0;
		if (!db.inTransaction()) {
			retVal = db.update(builder.getTables(), values, selection, selectionArgs);
		}

		return retVal;

	}

	/**
	 * Apply the given set of {@link ContentProviderOperation}, executing inside a {@link SQLiteDatabase} transaction.
	 * All changes will be rolled back if any single one fails.
	 */
	@Override
	public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		final SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			final int numOperations = operations.size();
			final ContentProviderResult[] results = new ContentProviderResult[numOperations];
			for (int i = 0; i < numOperations; i++) {
				ContentProviderOperation op = operations.get(i);
				results[i] = op.apply(this, results, i);
				// getContext().getContentResolver().notifyChange(op.getUri(), null);
				db.yieldIfContendedSafely();
			}
			db.setTransactionSuccessful();
			return results;
		} finally {
			db.endTransaction();
		}
	}
}

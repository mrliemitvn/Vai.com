package org.vai.com.provider;

import org.vai.com.provider.DbContract.Category;
import org.vai.com.provider.DbContract.Conference;
import org.vai.com.provider.DbContract.LikeState;
import org.vai.com.provider.DbContract.MoreWeb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This creates, updates, and opens the database. Opening is handled by the superclass, we handle the create & upgrade
 * steps
 */
public class DbHelper extends SQLiteOpenHelper {

	public final String TAG = DbHelper.class.getSimpleName();

	public interface Tables {
		public static final String CATEGORY = "category";
		public static final String CONFERENCE = "conference";
		public static final String MORE_WEB = "more_web";
		public static final String LIKE_STATE = "like_state";

		// Join table.
		public static final String CONFERENCE_JOIN_LIKE_STATE = "conference INNER JOIN like_state ON conference._id = like_state._id";
	}

	// Name of the database file
	public static final String DATABASE_NAME = "vai.sqlite";
	private static final int DATABASE_VERSION = 1;
	public static final String ALTER_TABLE_SYNTAX = "ALTER TABLE ";
	public static final String ADD_COLUMN_SYNTAX = " ADD COLUMN ";
	public static final String TEXT_DATA = " TEXT";
	public static final String INTEGER_DATA = " INTEGER";
	public static final String REAL_DATA = " REAL";

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder sqlBuilder = new StringBuilder();
		String sql = null;

		// Create CATEGORY table.
		sqlBuilder = new StringBuilder();
		sqlBuilder.append("CREATE TABLE IF NOT EXISTS " + Tables.CATEGORY + " (");
		sqlBuilder.append(Category._ID + " TEXT PRIMARY KEY, ");
		sqlBuilder.append(Category.NAME + " TEXT ");
		sqlBuilder.append(")");
		sql = sqlBuilder.toString();
		db.execSQL(sql);

		// Create CONFERENCE table.
		sqlBuilder = new StringBuilder();
		sqlBuilder.append("CREATE TABLE IF NOT EXISTS " + Tables.CONFERENCE + " (");
		sqlBuilder.append(Conference._ID + " TEXT PRIMARY KEY, ");
		sqlBuilder.append(Conference.POST_ID + " TEXT, ");
		sqlBuilder.append(Conference.CATEGORY_ID + " TEXT, ");
		sqlBuilder.append(Conference.TITLE + " TEXT, ");
		sqlBuilder.append(Conference.TITLE_ASCII + " TEXT, ");
		sqlBuilder.append(Conference.ALIAS + " TEXT, ");
		sqlBuilder.append(Conference.INTRO + " TEXT, ");
		sqlBuilder.append(Conference.CONTENT + " TEXT, ");
		sqlBuilder.append(Conference.VIDEO_ID + " TEXT, ");
		sqlBuilder.append(Conference.IMAGE + " TEXT, ");
		sqlBuilder.append(Conference._STATUS + " TEXT, ");
		sqlBuilder.append(Conference.AUTHOR + " INTEGER, ");
		sqlBuilder.append(Conference.IMAGE_WIDTH + " INTEGER, ");
		sqlBuilder.append(Conference.IMAGE_HEIGHT + " INTEGER, ");
		sqlBuilder.append(Conference.TIME_CREATED + " INTEGER, ");
		sqlBuilder.append(Conference.TIME_MODIFIED + " INTEGER, ");
		sqlBuilder.append(Conference.VIEWED + " INTEGER, ");
		sqlBuilder.append(Conference.LIKE + " INTEGER, ");
		sqlBuilder.append(Conference.COMMENT + " INTEGER ");
		sqlBuilder.append(")");
		sql = sqlBuilder.toString();
		db.execSQL(sql);

		// Create MORE_WEB table.
		sqlBuilder = new StringBuilder();
		sqlBuilder.append("CREATE TABLE IF NOT EXISTS " + Tables.MORE_WEB + " (");
		sqlBuilder.append(MoreWeb._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sqlBuilder.append(MoreWeb.NAME + " TEXT, ");
		sqlBuilder.append(MoreWeb.LINK + " TEXT ");
		sqlBuilder.append(")");
		sql = sqlBuilder.toString();
		db.execSQL(sql);

		// Create LIKE_STATE table.
		sqlBuilder = new StringBuilder();
		sqlBuilder.append("CREATE TABLE IF NOT EXISTS " + Tables.LIKE_STATE + " (");
		sqlBuilder.append(LikeState._ID + " TEXT, ");
		sqlBuilder.append(LikeState.LIKE_STATE + " INTEGER, ");
		sqlBuilder.append(LikeState.FACEBOOK_CONTENT_LIKED_ID + " TEXT, ");
		sqlBuilder.append(LikeState.FACEBOOK_USER_ID + " TEXT ");
		sqlBuilder.append(")");
		sql = sqlBuilder.toString();
		db.execSQL(sql);
	}

	public static void deleteDatabase(Context context) {
		context.deleteDatabase(DATABASE_NAME);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Upgrade database.
		if (newVersion > oldVersion) {
			db.beginTransaction();
			for (int i = oldVersion; i < newVersion; ++i) {
				int nextVersion = i + 1;
				switch (nextVersion) {
				case 2:
					// Update database to version 2.
					break;
				default:
					break;
				}
			}

			db.setTransactionSuccessful();
			db.endTransaction();
		}
	}

	/**
	 * Add new column to table.
	 * 
	 * @param db
	 *            database.
	 * @param table
	 *            table name.
	 * @param column
	 *            column name.
	 * @param dataType
	 *            type of data.
	 */
	private void addColumn(SQLiteDatabase db, String table, String column, String dataType) {
		String query = ALTER_TABLE_SYNTAX + table + ADD_COLUMN_SYNTAX + column + dataType;
		db.execSQL(query);
	}
}
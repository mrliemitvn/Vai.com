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

	public static class Category implements ResourceTable {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.vai.category";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.vai.category";
		/** Category name */
		public static final String NAME = "name";
	}
}

package org.vai.com.resource.menu;

import org.json.JSONObject;
import org.vai.com.provider.DbContract.Category;
import org.vai.com.resource.BaseResource;
import org.vai.com.resource.Resource;
import org.vai.com.utils.Consts;
import org.vai.com.utils.Logger;

import android.content.ContentValues;
import android.database.Cursor;

public class CategoryResource implements BaseResource, Resource {
	private static final String TAG = CategoryResource.class.getSimpleName();

	/* Category id. */
	public String id;

	/* Category name. */
	public String name;

	/**
	 * Create {@link CategoryResource} object from json data.
	 * 
	 * @param json
	 *            json data received from server.
	 */
	public CategoryResource(JSONObject json) {
		if (json != null) {
			try {
				if (!json.isNull(Consts.JSON_ID)) {
					id = json.getString(Consts.JSON_ID);
				}
				if (!json.isNull(Consts.JSON_NAME)) {
					name = json.getString(Consts.JSON_NAME);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Logger.error(TAG, e.getMessage());
			}
		}
	}

	/**
	 * Create {@link CategoryResource} from database.
	 * 
	 * @param cursor
	 *            cursor from database.
	 */
	public CategoryResource(Cursor cursor) {
		if (cursor != null) {
			int idIndex = cursor.getColumnIndex(Category._ID);
			int nameIndex = cursor.getColumnIndex(Category.NAME);

			if (idIndex > -1) id = cursor.getString(idIndex);
			if (nameIndex > -1) name = cursor.getString(nameIndex);
		}
	}

	/**
	 * Prepare {@link ContentValues} to insert to database.
	 */
	@Override
	public ContentValues prepareContentValues() {
		ContentValues values = new ContentValues();
		values.put(Category._ID, id);
		values.put(Category.NAME, name);
		return values;
	}

	@Override
	public String getRestRequestStatus() {
		return null;
	}
}
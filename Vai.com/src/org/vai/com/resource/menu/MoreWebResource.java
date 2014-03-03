package org.vai.com.resource.menu;

import org.json.JSONArray;
import org.vai.com.provider.DbContract.MoreWeb;
import org.vai.com.resource.BaseResource;

import android.content.ContentValues;
import android.database.Cursor;

public class MoreWebResource implements BaseResource {
	/* Website name. */
	public String name;

	/* Website link. */
	public String link;

	/**
	 * Create {@link MoreWebResource} object from json array data received from server.
	 * 
	 * @param jsonArray
	 *            json array data received from server.
	 */
	public MoreWebResource(JSONArray jsonArray) {
		if (jsonArray != null) {
			try {
				for (int i = 0; i < jsonArray.length(); i++) {
					if (i == 0) name = jsonArray.getString(i);
					if (i == 1) link = jsonArray.getString(i);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Create {@link MoreWebResource} object from database.
	 * 
	 * @param cursor
	 *            cursor from database.
	 */
	public MoreWebResource(Cursor cursor) {
		if (cursor != null) {
			int nameIndex = cursor.getColumnIndex(MoreWeb.NAME);
			int linkIndex = cursor.getColumnIndex(MoreWeb.LINK);
			if (nameIndex > -1) name = cursor.getString(nameIndex);
			if (linkIndex > -1) link = cursor.getString(linkIndex);
		}
	}

	/**
	 * Prepare {@link ContentValues} to insert to database.
	 */
	@Override
	public ContentValues prepareContentValues() {
		ContentValues values = new ContentValues();
		values.put(MoreWeb.NAME, name);
		values.put(MoreWeb.LINK, link);
		return values;
	}

	@Override
	public String getRestRequestStatus() {
		return null;
	}
}

package org.vai.com.adapter;

import org.vai.com.R;
import org.vai.com.resource.menu.CategoryResource;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This adapter display list category.
 */
public class HomeMenuAdapter extends CursorAdapter {

	/* LayoutInflater to inflate item layout. */
	private LayoutInflater mLayoutInflater;

	/**
	 * Constructor create {@link HomeMenuAdapter} object.
	 * 
	 * @param context
	 *            context to set.
	 * @param cursor
	 *            category data.
	 */
	public HomeMenuAdapter(Context context, Cursor cursor) {
		super(context, cursor, true);
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();

		/* Display category name. */
		CategoryResource categoryResource = new CategoryResource(cursor);
		viewHolder.tvCategory.setText(categoryResource.name);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		/* Inflate category item layout and initialize view. */
		View convertView = mLayoutInflater.inflate(R.layout.item_list_home_menu, parent, false);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.tvCategory = (TextView) convertView.findViewById(R.id.tvCategory);
		convertView.setTag(viewHolder);
		return convertView;
	}

	/**
	 * View holder of this adapter.
	 */
	private class ViewHolder {
		TextView tvCategory;
	}
}

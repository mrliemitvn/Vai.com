package org.vai.com.adapter;

import org.vai.com.R;
import org.vai.com.resource.menu.MoreWebResource;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This adapter display list other websites.
 */
public class MoreWebAdapter extends CursorAdapter {

	/* LayoutInflater to inflate item layout. */
	private LayoutInflater mLayoutInflater;

	/**
	 * Constructor create {@link MoreWebAdapter} object.
	 * 
	 * @param context
	 *            context to set.
	 * @param cursor
	 *            other websites data.
	 */
	public MoreWebAdapter(Context context, Cursor cursor) {
		super(context, cursor, true);
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();

		/* Display more website on view, include name and link of website. */
		MoreWebResource moreWeb = new MoreWebResource(cursor);
		viewHolder.tvName.setText(moreWeb.name); // Display website name.
		viewHolder.tvLink.setText(moreWeb.link); // Display website link.
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		/* Inflate item layout and initialize view. */
		View convertView = mLayoutInflater.inflate(R.layout.item_list_more_web, parent, false);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
		viewHolder.tvLink = (TextView) convertView.findViewById(R.id.tvLink);
		convertView.setTag(viewHolder);
		return convertView;
	}

	/**
	 * View holder of this adapter.
	 */
	private class ViewHolder {
		TextView tvName;
		TextView tvLink;
	}
}

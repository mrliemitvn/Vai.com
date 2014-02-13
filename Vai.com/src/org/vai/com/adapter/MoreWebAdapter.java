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

public class MoreWebAdapter extends CursorAdapter {

	private LayoutInflater mLayoutInflater;

	public MoreWebAdapter(Context context, Cursor cursor) {
		super(context, cursor, true);
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();

		MoreWebResource moreWeb = new MoreWebResource(cursor);
		viewHolder.tvName.setText(moreWeb.name);
		viewHolder.tvLink.setText(moreWeb.link);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View convertView = mLayoutInflater.inflate(R.layout.item_list_more_web, parent, false);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
		viewHolder.tvLink = (TextView) convertView.findViewById(R.id.tvLink);
		convertView.setTag(viewHolder);
		return convertView;
	}

	private class ViewHolder {
		TextView tvName;
		TextView tvLink;
	}
}

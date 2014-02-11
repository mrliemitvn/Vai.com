package org.vai.com.adapter;

import org.vai.com.R;
import org.vai.com.appinterface.IAdapterCallBack;
import org.vai.com.resource.home.CategoryResource;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeMenuAdapter extends CursorAdapter {

	private LayoutInflater mLayoutInflater;
	private IAdapterCallBack mAdapterCallBack;

	public HomeMenuAdapter(Context context, Cursor cursor, IAdapterCallBack adapterCallBack) {
		super(context, cursor, true);
		mLayoutInflater = LayoutInflater.from(context);
		mAdapterCallBack = adapterCallBack;
	}

	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();

		CategoryResource categoryResource = new CategoryResource(cursor);
		viewHolder.tvCategory.setText(categoryResource.name);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View convertView = mLayoutInflater.inflate(R.layout.item_list_home_menu, parent, false);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.tvCategory = (TextView) convertView.findViewById(R.id.tvCategory);
		convertView.setTag(viewHolder);
		return convertView;
	}

	private class ViewHolder {
		TextView tvCategory;
	}
}

package org.vai.com.fragment;

import org.vai.com.R;
import org.vai.com.VaiApplication;
import org.vai.com.activity.MoreWebActivity;
import org.vai.com.activity.OptionsActivity;
import org.vai.com.adapter.HomeMenuAdapter;
import org.vai.com.appinterface.IAdapterCallBack;
import org.vai.com.provider.DbContract.Category;
import org.vai.com.resource.menu.CategoryResource;
import org.vai.com.utils.Consts;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.MapBuilder;

public class HomeMenuFragment extends SherlockFragment implements LoaderCallbacks<Cursor>, OnClickListener {

	private static final int LOADER_CATEGORY = 1;

	private View mParentView;
	private ListView mListView;
	private HomeMenuAdapter mAdapter;
	private TextView mTvOptions;
	private TextView mTvMore;

	private IAdapterCallBack mAdapterCallBack;

	private void init() {
		mListView = (ListView) mParentView.findViewById(R.id.listView);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
				if (cursor != null && mAdapterCallBack != null) {
					CategoryResource categoryResource = new CategoryResource(cursor);
					Bundle bundle = new Bundle();
					bundle.putString(Consts.JSON_CATEGORY_ID, categoryResource.id);
					bundle.putString(Consts.JSON_NAME, categoryResource.name);
					mAdapterCallBack.adapterCallBack(bundle);
					// Send GA tracker.
					VaiApplication.getGaTracker().send(MapBuilder.createEvent(Consts.MENU_SELECTED, // Category.
							categoryResource.name, // Event action.
							"", // Event label (not required).
							null) // Event value.
							.build());
				}
			}
		});
	}

	private void setFooterList() {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewFooterOptions = inflater.inflate(R.layout.layout_options_menu, null, true);
		mTvOptions = (TextView) viewFooterOptions.findViewById(R.id.tvOptions);
		mTvMore = (TextView) viewFooterOptions.findViewById(R.id.tvMore);
		mTvOptions.setOnClickListener(this);
		mTvMore.setOnClickListener(this);
		mListView.addFooterView(viewFooterOptions);
	}

	private void setAdapter() {
		mAdapter = new HomeMenuAdapter(getActivity(), null);
		mListView.setAdapter(mAdapter);
	}

	private void initLoaderCursor() {
		getLoaderManager().initLoader(LOADER_CATEGORY, null, this);
	}

	public void setAdapterCallBack(IAdapterCallBack adapterCallBack) {
		mAdapterCallBack = adapterCallBack;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_home_menu, null);
		// Init view.
		init();
		return mParentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setFooterList();
		setAdapter();
		initLoaderCursor();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		switch (id) {
		case LOADER_CATEGORY:
			return new CursorLoader(getActivity(), Category.CONTENT_URI, null, null, null, null);
		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		int id = loader.getId();
		switch (id) {
		case LOADER_CATEGORY:
			mAdapter.swapCursor(cursor);
			break;

		default:
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	@Override
	public void onClick(View v) {
		if (v == mTvOptions) {
			startActivity(new Intent(getActivity(), OptionsActivity.class));
		} else if (v == mTvMore) {
			startActivity(new Intent(getActivity(), MoreWebActivity.class));
		}
	}
}

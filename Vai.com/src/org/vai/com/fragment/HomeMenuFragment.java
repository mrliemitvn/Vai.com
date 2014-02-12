package org.vai.com.fragment;

import org.vai.com.R;
import org.vai.com.adapter.HomeMenuAdapter;
import org.vai.com.appinterface.IAdapterCallBack;
import org.vai.com.provider.DbContract.Category;
import org.vai.com.resource.home.CategoryResource;
import org.vai.com.utils.Consts;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class HomeMenuFragment extends SherlockFragment implements LoaderCallbacks<Cursor> {

	private static final int LOADER_CATEGORY = 1;

	private View mParentView;
	private ListView mListView;
	private HomeMenuAdapter mAdapter;

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
				}
			}
		});
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
		setAdapter();
		initLoaderCursor();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		switch (id) {
		case LOADER_CATEGORY:
			String order = new StringBuilder().append(Category._ID).append(" ASC").toString();
			return new CursorLoader(getActivity(), Category.CONTENT_URI, null, null, null, order);
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
}

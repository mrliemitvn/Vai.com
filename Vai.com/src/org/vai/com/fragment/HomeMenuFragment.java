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

/**
 * This fragment will show menu of application.<br>
 * Also include all category.
 */
public class HomeMenuFragment extends SherlockFragment implements LoaderCallbacks<Cursor>, OnClickListener {

	/* Use this loader to load category data. */
	private static final int LOADER_CATEGORY = 1;

	/* Root view. */
	private View mParentView;

	/* ListView and adapter to display all category. */
	private ListView mListView;
	private HomeMenuAdapter mAdapter;

	/* Options menu. */
	private TextView mTvOptions;

	/* More website menu. */
	private TextView mTvMore;

	/* Use this interface when choose one category. */
	private IAdapterCallBack mAdapterCallBack;

	/**
	 * Initialize all view will be used.
	 */
	private void init() {
		mListView = (ListView) mParentView.findViewById(R.id.listView);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				/*
				 * Catch choose one category event.
				 * Get category data, use call back interface to handle this event.
				 * Also tracking google analytics.
				 */
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

	/**
	 * Set footer for list category.
	 * Footer is 2 menu: options menu and more website menu.
	 */
	private void setFooterList() {
		/* Inflate footer layout. */
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewFooterOptions = inflater.inflate(R.layout.layout_options_menu, null, true);
		mTvOptions = (TextView) viewFooterOptions.findViewById(R.id.tvOptions); // Options menu.
		mTvMore = (TextView) viewFooterOptions.findViewById(R.id.tvMore); // More website menu.
		/* Set click event. */
		mTvOptions.setOnClickListener(this);
		mTvMore.setOnClickListener(this);
		/* Add footer view to list. */
		mListView.addFooterView(viewFooterOptions);
	}

	/**
	 * Set category adapter.
	 */
	private void setAdapter() {
		mAdapter = new HomeMenuAdapter(getActivity(), null);
		mListView.setAdapter(mAdapter);
	}

	/**
	 * Initialize loader for load category data from database.
	 */
	private void initLoaderCursor() {
		getLoaderManager().initLoader(LOADER_CATEGORY, null, this);
	}

	/**
	 * Set call back interface.
	 * 
	 * @param adapterCallBack
	 *            call back interface to set.
	 */
	public void setAdapterCallBack(IAdapterCallBack adapterCallBack) {
		mAdapterCallBack = adapterCallBack;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/* Inflate root view. */
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
			/* Load category data. */
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
			/* Update data on category adapter. */
			mAdapter.swapCursor(cursor);
			break;
		default:
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null); // Reset adapter.
	}

	@Override
	public void onClick(View v) {
		if (v == mTvOptions) { // Click on options menu, go to {@link OptionsActivity}.
			startActivity(new Intent(getActivity(), OptionsActivity.class));
		} else if (v == mTvMore) { // Click on more website menu, go to {@link MoreWebActivity}.
			startActivity(new Intent(getActivity(), MoreWebActivity.class));
		}
	}
}

package org.vai.com.fragment;

import java.util.ArrayList;

import org.vai.com.R;
import org.vai.com.adapter.HomeVerticalAdapter;
import org.vai.com.appinterface.IAdapterCallBack;
import org.vai.com.provider.DbContract.Conference;
import org.vai.com.resource.home.ConferenceResource;
import org.vai.com.service.ServiceHelper;

import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeVerticalFragment extends HomeFragment implements IAdapterCallBack {

	private View mHeaderLoadingContent;
	private ProgressBar mPbLoadingData;
	private TextView mTvNoData;
	private ListView mListView;
	private HomeVerticalAdapter mAdapter;
	private ArrayList<ConferenceResource> mListConference = new ArrayList<ConferenceResource>();

	private int mCurrentPage = 1;

	private void setAdapterAndGetData() {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mHeaderLoadingContent = inflater.inflate(R.layout.layout_list_loading, null, false);
		mPbLoadingData = (ProgressBar) mHeaderLoadingContent.findViewById(R.id.pbLoadingData);
		mTvNoData = (TextView) mHeaderLoadingContent.findViewById(R.id.tvNoData);
		mListView.addHeaderView(mHeaderLoadingContent);

		mAdapter = new HomeVerticalAdapter(getActivity(), mListConference, this);
		mListView.setAdapter(mAdapter);

		getDataFromDb();
		callApiGetConference(1);
	}

	@Override
	protected void init() {
		mListView = (ListView) mParentView.findViewById(R.id.listView);
	}

	@Override
	protected void getDataFromDb() {
		if (getActivity() == null) return;
		String where = new StringBuilder().append(Conference.CATEGORY_ID).append("='").append(mCategoryId).append("'")
				.toString();
		Cursor cursor = getActivity().getContentResolver().query(Conference.CONTENT_URI, null, where, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			mListConference.clear();
			do {
				ConferenceResource conference = new ConferenceResource(cursor);
				mListConference.add(conference);
			} while (cursor.moveToNext());

			mAdapter.notifyDataSetChanged();
		}
		if (cursor != null) cursor.close();

		if (mListConference.size() > 0) {
			mTvNoData.setVisibility(View.GONE);
			mHeaderLoadingContent.setVisibility(View.GONE);
		} else if (mIsLoaded) {
			mTvNoData.setVisibility(View.VISIBLE);
			mHeaderLoadingContent.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void showLoadingView() {
		mHeaderLoadingContent.setVisibility(View.VISIBLE);
		mPbLoadingData.setVisibility(View.VISIBLE);
	}

	@Override
	protected void hideLoadingView() {
		mPbLoadingData.setVisibility(View.GONE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_home_vertical, container, false);

		init();

		return mParentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setAdapterAndGetData();
	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter(ServiceHelper.ACTION_REQUEST_RESULT);
		getActivity().registerReceiver(mRequestReceiver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			getActivity().unregisterReceiver(mRequestReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void adapterCallBack(Bundle bundle) {
		// TODO Auto-generated method stub

	}
}
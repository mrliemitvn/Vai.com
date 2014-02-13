package org.vai.com.fragment;

import java.util.ArrayList;

import org.vai.com.R;
import org.vai.com.adapter.HomeContentAdapter;
import org.vai.com.appinterface.IAdapterCallBack;
import org.vai.com.broadcastreceiver.BroadcastReceiverCallback;
import org.vai.com.broadcastreceiver.RestBroadcastReceiver;
import org.vai.com.provider.DbContract.Conference;
import org.vai.com.resource.home.ConferenceResource;
import org.vai.com.service.Actions;
import org.vai.com.service.ServiceHelper;
import org.vai.com.utils.Consts;

import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeContentFragment extends BaseFragment implements IAdapterCallBack {

	private static final String TAG = HomeContentFragment.class.getSimpleName();

	private View mParentView;
	private View mHeaderLoadingContent;
	private ProgressBar mPbLoadingData;
	private TextView mTvNoData;
	private ListView mListView;
	private HomeContentAdapter mAdapter;
	private ArrayList<ConferenceResource> mListConference = new ArrayList<ConferenceResource>();

	private String mCategoryId = "0";
	private int mCurrentPage = 1;
	private boolean mIsLoaded = false;

	private Long mRequestId;
	private RestBroadcastReceiver mRequestReceiver = new RestBroadcastReceiver(TAG, new BroadcastReceiverCallback() {

		@Override
		public void onSuccess() {
			getDataFromDb();
		}

		@Override
		public void onError(int requestCode, String message) {
			showMessageBar(requestCode);
		}

		@Override
		public void onDifferenceId() {
		}

		@Override
		public void onComplete() {
			mRequestId = null;
			mPbLoadingData.setVisibility(View.GONE);
			mIsLoaded = true;
		}
	});

	private void init() {
		mListView = (ListView) mParentView.findViewById(R.id.listView);
	}

	private void setAdapterAndGetData() {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mHeaderLoadingContent = inflater.inflate(R.layout.layout_list_loading, null, false);
		mPbLoadingData = (ProgressBar) mHeaderLoadingContent.findViewById(R.id.pbLoadingData);
		mTvNoData = (TextView) mHeaderLoadingContent.findViewById(R.id.tvNoData);
		mListView.addHeaderView(mHeaderLoadingContent);

		mAdapter = new HomeContentAdapter(getActivity(), mListConference, this);
		mListView.setAdapter(mAdapter);

		getDataFromDb();
		callApiGetConference(1);
	}

	private void getDataFromDb() {
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

	public void callApiGetConference(int page) {
		if (getActivity() == null) return;
		// Call api get category.
		mIsLoaded = false;
		mHeaderLoadingContent.setVisibility(View.VISIBLE);
		mPbLoadingData.setVisibility(View.VISIBLE);
		if (TextUtils.isEmpty(mCategoryId)) mCategoryId = "0";
		Bundle bundle = new Bundle();
		bundle.putString(Consts.JSON_CATEGORY_ID, mCategoryId);
		bundle.putInt(Consts.JSON_PAGE, page);
		ServiceHelper serviceHelper = ServiceHelper.getInstance(getActivity());
		mRequestId = serviceHelper.sendRequest(Actions.GET_CONFERENCE_ACTION, bundle);
		mRequestReceiver.setRequestId(mRequestId);
	}

	public void setCategoryId(String categoryId) {
		mCategoryId = categoryId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_home_content, container, false);

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
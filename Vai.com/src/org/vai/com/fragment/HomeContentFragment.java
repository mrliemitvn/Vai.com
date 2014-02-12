package org.vai.com.fragment;

import java.util.ArrayList;

import org.vai.com.R;
import org.vai.com.adapter.HomeContentAdapter;
import org.vai.com.appinterface.IAdapterCallBack;
import org.vai.com.broadcastreceiver.BroadcastReceiverCallback;
import org.vai.com.broadcastreceiver.RestBroadcastReceiver;
import org.vai.com.resource.home.ConferenceResource;

import android.content.Context;
import android.os.Bundle;
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

	private Long mRequestId;
	private RestBroadcastReceiver mRequestReceiver = new RestBroadcastReceiver(TAG, new BroadcastReceiverCallback() {

		@Override
		public void onSuccess() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(int requestCode, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDifferenceId() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete() {
			// TODO Auto-generated method stub

		}
	});

	private void init() {
		mListView = (ListView) mParentView.findViewById(R.id.listView);
	}

	private void setAdapter() {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mHeaderLoadingContent = inflater.inflate(R.layout.layout_list_loading, null, false);
		mPbLoadingData = (ProgressBar) mHeaderLoadingContent.findViewById(R.id.pbLoadingData);
		mTvNoData = (TextView) mHeaderLoadingContent.findViewById(R.id.tvNoData);
		mListView.addHeaderView(mHeaderLoadingContent);

		mAdapter = new HomeContentAdapter(getActivity(), mListConference, this);
		mListView.setAdapter(mAdapter);
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
		setAdapter();
	}

	@Override
	public void adapterCallBack(Bundle bundle) {
		// TODO Auto-generated method stub

	}
}
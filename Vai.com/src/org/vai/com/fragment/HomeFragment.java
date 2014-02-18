package org.vai.com.fragment;

import org.vai.com.broadcastreceiver.BroadcastReceiverCallback;
import org.vai.com.broadcastreceiver.RestBroadcastReceiver;
import org.vai.com.service.Actions;
import org.vai.com.service.ServiceHelper;
import org.vai.com.utils.Consts;

import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

public class HomeFragment extends BaseFragment {
	protected static final String TAG = HomeFragment.class.getSimpleName();

	protected View mParentView;

	protected String mCategoryId;
	protected boolean mIsLoaded = false;
	protected int mCurrentPage = 1;
	protected int mTotalItems = 0;

	protected Long mRequestId;
	protected RestBroadcastReceiver mRequestReceiver = new RestBroadcastReceiver(TAG, new BroadcastReceiverCallback() {

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
			hideLoadingView();
			mIsLoaded = true;
		}
	});

	protected void init() {
	}

	protected void getDataFromDb() {
	}

	protected void showLoadingView() {
	}

	protected void hideLoadingView() {
	}

	protected void scrollToFirstItem() {
	}

	protected void setAdapterAndGetData() {
		getDataFromDb();
		callApiGetConference(1);
	}

	public void setCategoryId(String categoryId) {
		mCategoryId = categoryId;
	}

	public void callApiGetConference(int page) {
		if (getActivity() == null) return;
		if (page <= 1) {
			scrollToFirstItem();
			mTotalItems = 0;
		}
		// Call api get category.
		mIsLoaded = false;
		mCurrentPage = page;
		showLoadingView();
		if (TextUtils.isEmpty(mCategoryId)) mCategoryId = "0";
		Bundle bundle = new Bundle();
		bundle.putString(Consts.JSON_CATEGORY_ID, mCategoryId);
		bundle.putInt(Consts.JSON_PAGE, page);
		ServiceHelper serviceHelper = ServiceHelper.getInstance(getActivity());
		mRequestId = serviceHelper.sendRequest(Actions.GET_CONFERENCE_ACTION, bundle);
		mRequestReceiver.setRequestId(mRequestId);
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
}

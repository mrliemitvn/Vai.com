package org.vai.com.fragment;

import org.vai.com.broadcastreceiver.BroadcastReceiverCallback;
import org.vai.com.broadcastreceiver.RestBroadcastReceiver;
import org.vai.com.service.Actions;
import org.vai.com.service.ServiceHelper;
import org.vai.com.utils.Consts;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

public class HomeFragment extends BaseFragment {
	protected static final String TAG = HomeFragment.class.getSimpleName();

	protected View mParentView;

	protected String mCategoryId;
	protected boolean mIsLoaded = false;

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

	public void setCategoryId(String categoryId) {
		mCategoryId = categoryId;
	}

	public void callApiGetConference(int page) {
		if (getActivity() == null) return;
		// Call api get category.
		mIsLoaded = false;
		showLoadingView();
		if (TextUtils.isEmpty(mCategoryId)) mCategoryId = "0";
		Bundle bundle = new Bundle();
		bundle.putString(Consts.JSON_CATEGORY_ID, mCategoryId);
		bundle.putInt(Consts.JSON_PAGE, page);
		ServiceHelper serviceHelper = ServiceHelper.getInstance(getActivity());
		mRequestId = serviceHelper.sendRequest(Actions.GET_CONFERENCE_ACTION, bundle);
		mRequestReceiver.setRequestId(mRequestId);
	}

}

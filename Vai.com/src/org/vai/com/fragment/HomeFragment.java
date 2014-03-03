package org.vai.com.fragment;

import org.vai.com.appinterface.IAdapterCallBack;
import org.vai.com.broadcastreceiver.BroadcastReceiverCallback;
import org.vai.com.broadcastreceiver.RestBroadcastReceiver;
import org.vai.com.service.Actions;
import org.vai.com.service.ServiceHelper;
import org.vai.com.utils.Consts;

import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

/**
 * This class is parent class of {@link HomeVerticalFragment} and {@link HomeHorizontalFragment} class.<br>
 * This contain some common variable and common method.
 */
public class HomeFragment extends BaseFragment {
	protected static final String TAG = HomeFragment.class.getSimpleName();

	/* Root view. */
	protected View mParentView;

	/* Category id. */
	protected String mCategoryId;

	/* Use this flag to define api is loaded or not. */
	protected boolean mIsLoaded = false;

	/* Use this flag to define api is loading or not. */
	protected boolean mIsLoading = false;

	/* For load more conference. */
	protected int mCurrentPage = 1;
	protected int mTotalItems = 0;

	/* Use this interface to like or share to facebook. */
	protected IAdapterCallBack mAdapterCallBack;

	/* For call get conference api */
	protected Long mRequestId;
	protected RestBroadcastReceiver mRequestReceiver = new RestBroadcastReceiver(TAG, new BroadcastReceiverCallback() {

		@Override
		public void onSuccess() {
			getDataFromDb(); // Call api successfully, get data from database and display.
		}

		@Override
		public void onError(int requestCode, String message) {
			showMessageBar(requestCode); // Error occur, show notification.
		}

		@Override
		public void onDifferenceId() {
		}

		@Override
		public void onComplete() {
			/* Complete call api, reset request id, hide loading view and reset loading, loaded flag. */
			mRequestId = null;
			hideLoadingView();
			mIsLoaded = true;
			mIsLoading = false;
		}
	});

	/**
	 * Initialize all view will be used.
	 */
	protected void init() {
	}

	/**
	 * Show loading view when loading data.
	 */
	protected void showLoadingView() {
	}

	/**
	 * Hide loading view when loading complete.
	 */
	protected void hideLoadingView() {
	}

	/**
	 * Scroll to first item of list conference.
	 */
	protected void scrollToFirstItem() {
	}

	protected void setAdapterAndGetData() {
		getDataFromDb();
		callApiGetConference(1);
	}

	/**
	 * Get conference data from database to display on screen.
	 */
	public void getDataFromDb() {
	}

	/**
	 * Set category id.
	 * 
	 * @param categoryId
	 *            category id to set.
	 */
	public void setCategoryId(String categoryId) {
		mCategoryId = categoryId;
	}

	/**
	 * Set call back interface.
	 * 
	 * @param adapterCallBack
	 *            adapterCallBack to set.
	 */
	public void setAdapterCallBack(IAdapterCallBack adapterCallBack) {
		mAdapterCallBack = adapterCallBack;
	}

	/**
	 * Call get conference api.
	 * 
	 * @param page
	 *            page to load data.
	 */
	public void callApiGetConference(int page) {
		/* If fragment not attach to activity or is loading, do nothing. */
		if (getActivity() == null || mIsLoading) return;
		if (page <= 1) { // Load first page.
			scrollToFirstItem(); // Scroll to top of list or back to first of view pager.
			mTotalItems = 0; // Reset total items.
		}
		/* Prepare before call api. */
		mIsLoaded = false; // Set loaded flag.
		mIsLoading = true; // Set loading flag.
		mCurrentPage = page; // Set current page.
		showLoadingView(); // Show loading view.
		if (TextUtils.isEmpty(mCategoryId)) mCategoryId = "0"; // Default category if not set.
		/* Prepare data and call api. */
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
		/* Register receiver. */
		IntentFilter filter = new IntentFilter(ServiceHelper.ACTION_REQUEST_RESULT);
		getActivity().registerReceiver(mRequestReceiver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		mIsLoading = false; // Reset flag.
		/* Unregister receiver. */
		try {
			getActivity().unregisterReceiver(mRequestReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

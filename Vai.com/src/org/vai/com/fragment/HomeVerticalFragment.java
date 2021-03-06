package org.vai.com.fragment;

import java.util.ArrayList;

import org.vai.com.R;
import org.vai.com.adapter.HomeVerticalAdapter;
import org.vai.com.provider.DbContract;
import org.vai.com.provider.DbContract.Conference;
import org.vai.com.provider.DbContract.LikeState;
import org.vai.com.provider.DbHelper.Tables;
import org.vai.com.provider.SharePrefs;
import org.vai.com.resource.home.ConferenceResource;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This fragment is used to display conference in {@link ListView}.
 */
public class HomeVerticalFragment extends HomeFragment {

	/* Header loading view. */
	private View mHeaderLoadingContent;

	/* Footer load more view. */
	private View mFooterLoadmoreContent;

	/* Progress bar loading conference. */
	private ProgressBar mPbLoadingData;

	/* TextView show notice when has not conference data. */
	private TextView mTvNoData;

	/* ListView and adapter display conference. */
	private ListView mListView;
	private HomeVerticalAdapter mAdapter;

	/* List conference data. */
	private ArrayList<ConferenceResource> mListConference = new ArrayList<ConferenceResource>();

	/* Use this runnable to scroll list to first item. */
	private Runnable mRunnableScrollToFirst;

	/* Variable for show or hide action bar event. */
	private int oldFirstVisibleItem = -1;
	protected int oldTop = -1;

	/**
	 * Show load more footer.
	 */
	private void showLoadingMore() {
		if (mFooterLoadmoreContent == null) return;
		((View) mFooterLoadmoreContent.findViewById(R.id.pbLoadingMore)).setVisibility(View.VISIBLE);
		((View) mFooterLoadmoreContent.findViewById(R.id.tvLoadingMore)).setVisibility(View.VISIBLE);
		mFooterLoadmoreContent.setVisibility(View.VISIBLE);
	}

	@Override
	protected void setAdapterAndGetData() {
		/*
		 * Inflate header and footer view.
		 * Add them to list view.
		 */
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mHeaderLoadingContent = inflater.inflate(R.layout.layout_list_loading, null, false);
		mFooterLoadmoreContent = inflater.inflate(R.layout.layout_load_more, null, false);
		mPbLoadingData = (ProgressBar) mHeaderLoadingContent.findViewById(R.id.pbLoadingData);
		mTvNoData = (TextView) mHeaderLoadingContent.findViewById(R.id.tvNoData);
		mListView.addHeaderView(mHeaderLoadingContent);
		mListView.addFooterView(mFooterLoadmoreContent);

		// Hide footer load more.
		mFooterLoadmoreContent.setVisibility(View.GONE);
		((View) mFooterLoadmoreContent.findViewById(R.id.pbLoadingMore)).setVisibility(View.GONE);
		((View) mFooterLoadmoreContent.findViewById(R.id.tvLoadingMore)).setVisibility(View.GONE);

		/* Setup adapter. */
		mAdapter = new HomeVerticalAdapter(getActivity(), mListConference, mAdapterCallBack);
		mListView.setAdapter(mAdapter);

		super.setAdapterAndGetData();
	}

	@Override
	protected void init() {
		mListView = (ListView) mParentView.findViewById(R.id.listView);
		OnScrollListener listOnScrollListener = new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Show or hide action bar.
				// if (firstVisibleItem == oldFirstVisibleItem) {
				// int top = view.getChildAt(0).getTop();
				// if (top > oldTop && Math.abs(top - oldTop) > Consts.MAX_SCROLL_DIFF) {
				// ((SlidingFragmentActivity) getActivity()).getSupportActionBar().show();
				// } else if (top < oldTop && Math.abs(top - oldTop) > Consts.MAX_SCROLL_DIFF) {
				// ((SlidingFragmentActivity) getActivity()).getSupportActionBar().hide();
				// }
				// oldTop = top;
				// } else {
				// View child = view.getChildAt(0);
				// if (child != null) {
				// oldFirstVisibleItem = firstVisibleItem;
				// oldTop = child.getTop();
				// }
				// }

				// catch load more event
				int headerFooterCount = (mListView.getHeaderViewsCount() + mListView.getFooterViewsCount());
				if (totalItemCount > headerFooterCount && totalItemCount > mTotalItems) {
					// what is the bottom item that is visible
					int lastInScreen = firstVisibleItem + visibleItemCount;
					// is the bottom item visible & not loading more already ? Load more !
					if ((lastInScreen != totalItemCount) || mIsLoading) return;
					mTotalItems = totalItemCount;
					showLoadingMore();
					callApiGetConference(mCurrentPage + 1);
				}
			}
		};
		mListView.setOnScrollListener(listOnScrollListener);
	}

	@Override
	protected void showLoadingView() { // Show loading header view.
		mHeaderLoadingContent.setVisibility(View.VISIBLE);
		mPbLoadingData.setVisibility(View.VISIBLE);
	}

	@Override
	protected void hideLoadingView() { // Hide loading header view.
		mPbLoadingData.setVisibility(View.GONE);
		mHeaderLoadingContent.setVisibility(View.GONE);
		if (mFooterLoadmoreContent != null) {
			((View) mFooterLoadmoreContent.findViewById(R.id.pbLoadingMore)).setVisibility(View.GONE);
			((View) mFooterLoadmoreContent.findViewById(R.id.tvLoadingMore)).setVisibility(View.GONE);
			mFooterLoadmoreContent.setVisibility(View.GONE);
		}
	}

	@Override
	protected void scrollToFirstItem() { // Scroll to first item of list.
		if (mRunnableScrollToFirst == null) mRunnableScrollToFirst = new Runnable() {
			@Override
			public void run() {
				mListView.setSelection(0);
			}
		};
		mListView.post(mRunnableScrollToFirst);
	}

	@Override
	public void getDataFromDb() {
		/* If fragment has not attach to activity, do nothing. */
		if (getActivity() == null) return;

		/* Query database to get conference data. */
		String where = new StringBuilder().append(DbContract.getAlias(Tables.CONFERENCE, Conference.CATEGORY_ID))
				.append("='").append(mCategoryId).append("' and ")
				.append(DbContract.getAlias(Tables.LIKE_STATE, LikeState.FACEBOOK_USER_ID)).append("='")
				.append(SharePrefs.getInstance().getFacebookUserId()).append("'").toString();
		String order = new StringBuilder().append(DbContract.getQualifiedColumnName(Tables.CONFERENCE, Conference._ID))
				.append(" DESC").toString();
		Cursor cursor = getActivity().getContentResolver().query(Conference.CONTENT_URI_CONFERENCE_JOIN_LIKE_STATE,
				null, where, null, order);
		/* Update conference data on list. */
		if (cursor != null && cursor.moveToFirst()) {
			mListConference.clear(); // Clear old conference data.
			do {
				/* Create conference data and add to list. */
				ConferenceResource conference = new ConferenceResource(cursor);
				mListConference.add(conference);
			} while (cursor.moveToNext());

			mAdapter.notifyDataSetChanged(); // Notify data changed.
		}
		if (cursor != null) cursor.close();

		/*
		 * If has conference data, hide no data notice.
		 * Else show no data notice.
		 */
		if (mListConference.size() > 0) {
			mTvNoData.setVisibility(View.GONE);
			mHeaderLoadingContent.setVisibility(View.GONE);
		} else if (mIsLoaded) {
			mTvNoData.setVisibility(View.VISIBLE);
			mHeaderLoadingContent.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/* Inflate root view. */
		mParentView = inflater.inflate(R.layout.fragment_home_vertical, container, false);

		init(); // Initialize view.

		return mParentView;
	}
}
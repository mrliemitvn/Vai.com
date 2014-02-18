package org.vai.com.fragment;

import java.util.ArrayList;

import org.vai.com.R;
import org.vai.com.adapter.HomeVerticalAdapter;
import org.vai.com.appinterface.IAdapterCallBack;
import org.vai.com.provider.DbContract.Conference;
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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

public class HomeVerticalFragment extends HomeFragment implements IAdapterCallBack {

	private View mHeaderLoadingContent;
	private ProgressBar mPbLoadingData;
	private TextView mTvNoData;
	private ListView mListView;
	private HomeVerticalAdapter mAdapter;
	private ArrayList<ConferenceResource> mListConference = new ArrayList<ConferenceResource>();

	private Runnable mRunnableScrollToFirst;

	@Override
	protected void setAdapterAndGetData() {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mHeaderLoadingContent = inflater.inflate(R.layout.layout_list_loading, null, false);
		mPbLoadingData = (ProgressBar) mHeaderLoadingContent.findViewById(R.id.pbLoadingData);
		mTvNoData = (TextView) mHeaderLoadingContent.findViewById(R.id.tvNoData);
		mListView.addHeaderView(mHeaderLoadingContent);

		mAdapter = new HomeVerticalAdapter(getActivity(), mListConference, this);
		mListView.setAdapter(mAdapter);

		super.setAdapterAndGetData();
	}

	@Override
	protected void init() {
		mListView = (ListView) mParentView.findViewById(R.id.listView);
		OnScrollListener listOnScrollListener = new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// catch load more event
				int headerFooterCount = (mListView.getHeaderViewsCount() + mListView.getFooterViewsCount());
				if (totalItemCount > headerFooterCount && totalItemCount > mTotalItems) {
					// what is the bottom item that is visible
					int lastInScreen = firstVisibleItem + visibleItemCount;
					// is the bottom item visible & not loading more already ? Load more !
//					if (totalItemCount - lastInScreen <= 2) showLoadingMore();
					if ((lastInScreen != totalItemCount)) return;
					mTotalItems = totalItemCount;
					callApiGetConference(mCurrentPage + 1);
				}
			}
		};
		PauseOnScrollListener pauseListener = new PauseOnScrollListener(ImageLoader.getInstance(), true, true,
				listOnScrollListener);
		mListView.setOnScrollListener(pauseListener);
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
	protected void scrollToFirstItem() {
		if (mRunnableScrollToFirst == null) mRunnableScrollToFirst = new Runnable() {
			@Override
			public void run() {
				mListView.setSelection(0);
			}
		};
		mListView.post(mRunnableScrollToFirst);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_home_vertical, container, false);

		init();

		return mParentView;
	}

	@Override
	public void adapterCallBack(Bundle bundle) {
		// TODO Auto-generated method stub

	}
}
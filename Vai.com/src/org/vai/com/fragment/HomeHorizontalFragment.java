package org.vai.com.fragment;

import java.util.ArrayList;

import org.vai.com.R;
import org.vai.com.adapter.SmartFragmentStatePagerAdapter;
import org.vai.com.provider.DbContract;
import org.vai.com.provider.DbContract.Conference;
import org.vai.com.provider.DbContract.LikeState;
import org.vai.com.provider.DbHelper.Tables;
import org.vai.com.provider.SharePrefs;
import org.vai.com.resource.home.ConferenceResource;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * This fragment is used to display conference in {@link ViewPager}.
 */
public class HomeHorizontalFragment extends HomeFragment {

	/* Progress dialog is used when loading conference. */
	private ProgressDialog mProgressDialog;

	/* ViewPager, adapter to display conference and list conference data. */
	private ViewPager mViewPager;
	private HomeHorizontalPagerAdapter mAdapter;
	private ArrayList<SherlockFragment> mListFragments = new ArrayList<SherlockFragment>();

	@Override
	protected void init() {
		mViewPager = (ViewPager) mParentView.findViewById(R.id.viewPager);
	}

	@Override
	protected void setAdapterAndGetData() {
		/* Set adapter for ViewPager, after that call api get conference from server. */
		mAdapter = new HomeHorizontalPagerAdapter(getFragmentManager(), mListFragments);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int position) {
				/* When page is selected, display conference. */
				((HomeContentHorizontalFragment) mListFragments.get(position)).updateData();
				/* Setup for sliding menu. */
				switch (position) {
				case 0:
					((SlidingFragmentActivity) getActivity()).getSlidingMenu().setTouchModeAbove(
							SlidingMenu.TOUCHMODE_FULLSCREEN);
					break;
				default:
					((SlidingFragmentActivity) getActivity()).getSlidingMenu().setTouchModeAbove(
							SlidingMenu.TOUCHMODE_MARGIN);
					break;
				}
				/* Catch load more event. */
				if (position == (mListFragments.size() - 1) && mListFragments.size() > mTotalItems) callApiGetConference(mCurrentPage + 1);
			}

		});

		mViewPager.setCurrentItem(0);
		((SlidingFragmentActivity) getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		super.setAdapterAndGetData();
	}

	@Override
	protected void showLoadingView() { // Show progress dialog.
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setMessage(getResources().getString(R.string.msg_info_loading));
		}
		if (!mProgressDialog.isShowing()) mProgressDialog.show();
	}

	@Override
	protected void hideLoadingView() { // Hide progress dialog.
		if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
	}

	@Override
	protected void scrollToFirstItem() { // Scroll to first page.
		mViewPager.setCurrentItem(0);
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
		Cursor cursor = getActivity().getContentResolver().query(Conference.CONTENT_URI_CONFERENCE_JOIN_LIKE_STATE,
				null, where, null, null);

		/* Get conference data and update adapter. */
		if (cursor != null && cursor.moveToFirst()) {
			mListFragments.clear(); // Clear old list conference fragments.
			do {
				/* Create conference data and add to list. */
				ConferenceResource conference = new ConferenceResource(cursor);
				HomeContentHorizontalFragment fragment = new HomeContentHorizontalFragment();
				fragment.setConference(conference);
				fragment.setAdapterCallBack(mAdapterCallBack);
				mListFragments.add(fragment);
			} while (cursor.moveToNext());

			/* Prepare data to catch load more event. */
			if (mListFragments.size() > mTotalItems) {
				mTotalItems = mListFragments.size(); // Set total conference items.
				/* Use below fragment to catch load more event. */
				HomeContentHorizontalFragment fragment = new HomeContentHorizontalFragment();
				mListFragments.add(fragment);
			}

			/* Notify data changed and display first conference item. */
			mAdapter.notifyDataSetChanged();
			((HomeContentHorizontalFragment) mListFragments.get(mViewPager.getCurrentItem())).updateData();
		}
		if (cursor != null) cursor.close();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/* Inflate root view. */
		mParentView = inflater.inflate(R.layout.fragment_home_horizontal, container, false);

		init(); // Initialize view.

		return mParentView;
	}

	/**
	 * Adapter class for ViewPager in this fragment.
	 */
	public class HomeHorizontalPagerAdapter extends SmartFragmentStatePagerAdapter {

		/* List conference fragments. */
		private ArrayList<SherlockFragment> mFragments;

		/**
		 * Constructor create {@link HomeHorizontalPagerAdapter} object.
		 * 
		 * @param fm
		 *            fragment manager to set.
		 * @param listFragments
		 *            list conference fragments to set.
		 */
		public HomeHorizontalPagerAdapter(FragmentManager fm, ArrayList<SherlockFragment> listFragments) {
			super(fm);
			mFragments = listFragments;
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public SherlockFragment getItem(int position) {
			return mFragments.get(position);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}
}

package org.vai.com.fragment;

import java.util.ArrayList;

import org.vai.com.R;
import org.vai.com.activity.HomeActivity;
import org.vai.com.adapter.SmartFragmentStatePagerAdapter;
import org.vai.com.provider.DbContract;
import org.vai.com.provider.DbContract.Conference;
import org.vai.com.provider.DbContract.LikeState;
import org.vai.com.provider.DbHelper.Tables;
import org.vai.com.provider.SharePrefs;
import org.vai.com.resource.home.ConferenceResource;

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

public class HomeHorizontalFragment extends HomeFragment {

	private ViewPager mViewPager;
	private HomeHorizontalPagerAdapter mAdapter;
	private ArrayList<SherlockFragment> mListFragments = new ArrayList<SherlockFragment>();

	@Override
	protected void init() {
		mViewPager = (ViewPager) mParentView.findViewById(R.id.viewPager);
	}

	@Override
	protected void setAdapterAndGetData() {
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
				((HomeContentHorizontalFragment) mListFragments.get(position)).updateData();
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
				if (position == (mListFragments.size() - 1) && mListFragments.size() > mTotalItems) callApiGetConference(mCurrentPage + 1);
			}

		});

		mViewPager.setCurrentItem(0);
		((SlidingFragmentActivity) getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		super.setAdapterAndGetData();
	}

	@Override
	protected void getDataFromDb() {
		if (getActivity() == null) return;
		String where = new StringBuilder().append(DbContract.getAlias(Tables.CONFERENCE, Conference.CATEGORY_ID))
				.append("='").append(mCategoryId).append("' and ")
				.append(DbContract.getAlias(Tables.LIKE_STATE, LikeState.FACEBOOK_USER_ID)).append("='")
				.append(SharePrefs.getInstance().getFacebookUserId()).append("'").toString();
		Cursor cursor = getActivity().getContentResolver().query(Conference.CONTENT_URI_CONFERENCE_JOIN_LIKE_STATE,
				null, where, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			mListFragments.clear();
			do {
				ConferenceResource conference = new ConferenceResource(cursor);
				HomeContentHorizontalFragment fragment = new HomeContentHorizontalFragment();
				fragment.setConference(conference);
				fragment.setAdapterCallBack(mAdapterCallBack);
				mListFragments.add(fragment);
			} while (cursor.moveToNext());
			if (mListFragments.size() > mTotalItems) {
				mTotalItems = mListFragments.size();
				HomeContentHorizontalFragment fragment = new HomeContentHorizontalFragment();
				mListFragments.add(fragment);
			}

			mAdapter.notifyDataSetChanged();
			((HomeContentHorizontalFragment) mListFragments.get(mViewPager.getCurrentItem())).updateData();
		}
		if (cursor != null) cursor.close();
	}

	@Override
	protected void showLoadingView() {
		((HomeActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);
	}

	@Override
	protected void hideLoadingView() {
		((HomeActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
	}

	@Override
	protected void scrollToFirstItem() {
		mViewPager.setCurrentItem(0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_home_horizontal, container, false);

		init();

		return mParentView;
	}

	public class HomeHorizontalPagerAdapter extends SmartFragmentStatePagerAdapter {

		private ArrayList<SherlockFragment> mFragments;

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

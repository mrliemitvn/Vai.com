package org.vai.com.fragment;

import java.util.ArrayList;

import org.vai.com.R;
import org.vai.com.provider.DbContract.Conference;
import org.vai.com.resource.home.ConferenceResource;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
			}

		});

		mViewPager.setCurrentItem(0);
		((SlidingFragmentActivity) getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		super.setAdapterAndGetData();
	}

	@Override
	protected void getDataFromDb() {
		if (getActivity() == null) return;
		String where = new StringBuilder().append(Conference.CATEGORY_ID).append("='").append(mCategoryId).append("'")
				.toString();
		Cursor cursor = getActivity().getContentResolver().query(Conference.CONTENT_URI, null, where, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			mListFragments.clear();
			do {
				ConferenceResource conference = new ConferenceResource(cursor);
				HomeContentHorizontalFragment fragment = new HomeContentHorizontalFragment();
				fragment.setConference(conference);
				mListFragments.add(fragment);
			} while (cursor.moveToNext());

			mAdapter.notifyDataSetChanged();
		}
		if (cursor != null) cursor.close();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_home_horizontal, container, false);

		init();

		return mParentView;
	}

	public class HomeHorizontalPagerAdapter extends FragmentPagerAdapter {

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

	}
}

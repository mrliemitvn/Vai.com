package org.vai.com.fragment;

import java.util.ArrayList;

import org.vai.com.R;

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
	private ColorPagerAdapter mAdapter;

	@Override
	protected void init() {
		mViewPager = (ViewPager) mParentView.findViewById(R.id.viewPager);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_home_horizontal, container, false);

		init();

		return mParentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapter = new ColorPagerAdapter(getFragmentManager());
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
	}

	public class ColorPagerAdapter extends FragmentPagerAdapter {

		private ArrayList<SherlockFragment> mFragments;

		private final int[] COLORS = new int[] { R.color.white, R.color.black };

		public ColorPagerAdapter(FragmentManager fm) {
			super(fm);
			mFragments = new ArrayList<SherlockFragment>();
			for (int color : COLORS) {
				ColorFragment fragment = new ColorFragment();
				fragment.setColorRes(color);
				mFragments.add(fragment);
			}
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

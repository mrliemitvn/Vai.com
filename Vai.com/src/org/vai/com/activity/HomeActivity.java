package org.vai.com.activity;

import org.vai.com.R;
import org.vai.com.appinterface.IAdapterCallBack;
import org.vai.com.fragment.HomeFragment;
import org.vai.com.fragment.HomeHorizontalFragment;
import org.vai.com.fragment.HomeMenuFragment;
import org.vai.com.fragment.HomeVerticalFragment;
import org.vai.com.provider.DbContract.Category;
import org.vai.com.provider.SharePrefs;
import org.vai.com.utils.Consts;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class HomeActivity extends SlidingFragmentActivity implements IAdapterCallBack {

	private SharePrefs mSharePrefs = SharePrefs.getInstance();
	private HomeMenuFragment mMenuFragment;
	private HomeFragment mContentFragment;

	private String mCategoryId = "";

	/**
	 * Initialize category id and menu.
	 */
	private void init() {
		// Get category id.
		Cursor cursor = getContentResolver().query(Category.CONTENT_URI, null, null, null, null);
		mCategoryId = "0";
		if (cursor != null && cursor.moveToFirst()) {
			int idIndex = cursor.getColumnIndex(Category._ID);
			if (idIndex > -1) mCategoryId = cursor.getString(idIndex);
			int nameIndex = cursor.getColumnIndex(Category.NAME);
			if (nameIndex > -1) getSupportActionBar().setTitle(cursor.getString(nameIndex));
		}
		if (cursor != null) cursor.close();

		// check if the content frame contains the menu frame
		if (findViewById(R.id.menu_frame) == null) {
			setBehindContentView(R.layout.menu_frame);
			getSlidingMenu().setSlidingEnabled(true);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			// show home as up so we can toggle
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		} else {
			// add a dummy view
			View v = new View(this);
			setBehindContentView(v);
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
		// Configure the SlidingMenu
		mMenuFragment = new HomeMenuFragment();
		mMenuFragment.setAdapterCallBack(this);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, mMenuFragment).commit();
		SlidingMenu sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		getSupportActionBar().setIcon(R.drawable.main_menu_icon);

		// Initialize content and menu.
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Set content.
		int viewStyle = mSharePrefs.getShowingContentOption();
		if (mContentFragment != null) {
			if (viewStyle == SharePrefs.HORIZONTAL_SHOWING_CONTENT
					&& mContentFragment.getClass().isAssignableFrom(HomeHorizontalFragment.class)) return;
			if (viewStyle == SharePrefs.VERTICAL_SHOWING_CONTENT
					&& mContentFragment.getClass().isAssignableFrom(HomeVerticalFragment.class)) return;
		}
		if (mSharePrefs.getShowingContentOption() == SharePrefs.HORIZONTAL_SHOWING_CONTENT) {
			mContentFragment = new HomeHorizontalFragment();
		} else {
			mContentFragment = new HomeVerticalFragment();
		}
		mContentFragment.setCategoryId(mCategoryId);
		getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, mContentFragment).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, android.R.id.button1, Menu.NONE, R.string.refresh).setIcon(R.drawable.icon_refesh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			break;
		case android.R.id.button1:
			if (mContentFragment != null) mContentFragment.callApiGetConference(1);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void adapterCallBack(Bundle bundle) {
		mCategoryId = bundle.getString(Consts.JSON_CATEGORY_ID);
		String categoryName = bundle.getString(Consts.JSON_NAME);
		getSupportActionBar().setTitle(categoryName);
		toggle();
		mContentFragment.setCategoryId(mCategoryId);
		mContentFragment.callApiGetConference(1);
	}
}
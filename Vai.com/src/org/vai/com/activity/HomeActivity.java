package org.vai.com.activity;

import org.vai.com.R;
import org.vai.com.appinterface.IAdapterCallBack;
import org.vai.com.fragment.HomeContentFragment;
import org.vai.com.fragment.HomeMenuFragment;
import org.vai.com.provider.DbContract.Category;
import org.vai.com.utils.Consts;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class HomeActivity extends SlidingFragmentActivity implements IAdapterCallBack {

	private HomeMenuFragment mMenuFragment;
	private HomeContentFragment mContentFragment;

	/**
	 * Initialize content and menu.
	 */
	private void init() {
		// Add content.
		mContentFragment = new HomeContentFragment();
		String order = new StringBuilder().append(Category._ID).append(" ASC").toString();
		Cursor cursor = getContentResolver().query(Category.CONTENT_URI, null, null, null, order);
		String categoryId = "0";
		if (cursor != null && cursor.moveToFirst()) {
			int idIndex = cursor.getColumnIndex(Category._ID);
			if (idIndex > -1) categoryId = cursor.getString(idIndex);
			int nameIndex = cursor.getColumnIndex(Category.NAME);
			if (nameIndex > -1) getSupportActionBar().setTitle(cursor.getString(nameIndex));
		}
		if (cursor != null) cursor.close();
		mContentFragment.setCategoryId(categoryId);
		getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, mContentFragment).commit();

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

		// Initialize content and menu.
		init();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void adapterCallBack(Bundle bundle) {
		String categoryId = bundle.getString(Consts.JSON_CATEGORY_ID);
		String categoryName = bundle.getString(Consts.JSON_NAME);
		getSupportActionBar().setTitle(categoryName);
		toggle();
		mContentFragment.setCategoryId(categoryId);
		mContentFragment.callApiGetConference(1);
	}
}
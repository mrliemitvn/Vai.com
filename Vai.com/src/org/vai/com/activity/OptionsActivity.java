package org.vai.com.activity;

import org.vai.com.R;
import org.vai.com.VaiApplication;
import org.vai.com.appinterface.IFacebookCallBack;
import org.vai.com.provider.SharePrefs;
import org.vai.com.utils.FacebookUtils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Session;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * This class is used to setting in application.<br>
 * <ul>
 * <li>Login with facebook account.<br>
 * <li>Clear image cache.<br>
 * <li>Go to Google play store to vote application.<br>
 * <li>Set option showing content in horizontal or vertical.
 * </ul>
 */
public class OptionsActivity extends SherlockActivity implements IFacebookCallBack, OnClickListener {

	/* Display AdView (Google admob). */
	private AdView adView;

	/* Share preference to save showing content option and facebook information. */
	private SharePrefs mSharePrefs = SharePrefs.getInstance();

	/* Showing content option CheckBox. */
	private CheckBox mCbHorizontal;
	private CheckBox mCbVertical;

	/* Login facebook button. */
	private Button mBtnLoginFacebook;

	/* Display facebook user name. */
	private TextView mTvFacebookName;

	/* TextView rate application. */
	private TextView mTvRateApp;

	/* TextView clear cache. */
	private TextView mTvClearCache;

	/* Use this variable to login facebook. */
	private FacebookUtils mFacebookUtils;

	/**
	 * Login facebook.
	 */
	private void loginFacebook() {
		// start Facebook Login
		if (mFacebookUtils == null) {
			mFacebookUtils = new FacebookUtils(this, this);
		}
		mFacebookUtils.loginFacebook();
	}

	/**
	 * Update view type to show content.
	 */
	private void updateViewType() {
		// Set up view type of content.
		if (mSharePrefs.getShowingContentOption() == SharePrefs.HORIZONTAL_SHOWING_CONTENT) {
			mCbHorizontal.setChecked(true);
			mCbVertical.setChecked(false);
		} else {
			mCbHorizontal.setChecked(false);
			mCbVertical.setChecked(true);
		}
	}

	/**
	 * Update facebook data on view.
	 */
	private void updateFacebookData() {
		// Show facebook name if logined.
		if (TextUtils.isEmpty(mSharePrefs.getFacebookUserToken())) {
			mTvFacebookName.setText(R.string.options_login_facebook);
			mBtnLoginFacebook.setText(R.string.options_login);
		} else {
			mTvFacebookName.setText(mSharePrefs.getFacebookUserName());
			mBtnLoginFacebook.setText(R.string.options_logout);
		}
	}

	/**
	 * Define view.
	 */
	private void init() {
		mCbHorizontal = (CheckBox) findViewById(R.id.cbHorizontal);
		mCbVertical = (CheckBox) findViewById(R.id.cbVertical);
		mBtnLoginFacebook = (Button) findViewById(R.id.btnLoginFacebook);
		mTvFacebookName = (TextView) findViewById(R.id.tvFacebookName);
		mTvRateApp = (TextView) findViewById(R.id.tvRateApp);
		mTvClearCache = (TextView) findViewById(R.id.tvClearCache);

		mCbHorizontal.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				/*
				 * Change checked state.
				 * If horizontal checkbox is selected, vertical checkbox will be unselected.
				 * And if horizontal checkbox is not selected, vertical checkbox will be selected.
				 */
				mCbVertical.setChecked(!isChecked);
				if (isChecked) {
					mSharePrefs.setShowingContentOption(SharePrefs.HORIZONTAL_SHOWING_CONTENT);
				} else {
					mSharePrefs.setShowingContentOption(SharePrefs.VERTICAL_SHOWING_CONTENT);
				}
			}
		});

		mCbVertical.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				/*
				 * Change checked state.
				 * If horizontal checkbox is selected, vertical checkbox will be unselected.
				 * And if horizontal checkbox is not selected, vertical checkbox will be selected.
				 */
				mCbHorizontal.setChecked(!isChecked);
				if (isChecked) {
					mSharePrefs.setShowingContentOption(SharePrefs.VERTICAL_SHOWING_CONTENT);
				} else {
					mSharePrefs.setShowingContentOption(SharePrefs.HORIZONTAL_SHOWING_CONTENT);
				}
			}
		});

		// Update view type.
		updateViewType();
		// Update facebook data on view.
		updateFacebookData();

		/* Set click event. */
		mBtnLoginFacebook.setOnClickListener(this);
		mTvRateApp.setOnClickListener(this);
		mTvClearCache.setOnClickListener(this);

		// For admob.
		adView = (AdView) this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest();
		adView.loadAd(adRequest);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);

		/* Show back button on action bar and set its icon. */
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(R.drawable.icon_back);

		init(); // Define view.
	}

	@Override
	public void onDestroy() {
		if (adView != null) adView.destroy(); // Destroy AdView.
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// For google anlytics.
		EasyTracker.getInstance(this).activityStart(this);
		VaiApplication.getGaTracker().set(Fields.SCREEN_NAME, this.getClass().getName());
		VaiApplication.getGaTracker().send(MapBuilder.createAppView().build());
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Stop google analytics for this activity.
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/* Result from login facebook. */
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		if (Session.getActiveSession().isOpened() && mFacebookUtils != null) {
			mFacebookUtils.getFacebookInfo(Session.getActiveSession());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) { // Finish activity when click on back button.
		case android.R.id.home:
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSuccess(Session session) {
		/* Login facebook successfully, update data. */
		updateFacebookData();
	}

	@Override
	public void onFailed() {
		/* Login failed, show error message. */
		Toast toast = Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	@Override
	public void onClick(View v) {
		if (v == mTvRateApp) { // Go to google play store to rate application.
			String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
			try { // Try open google play application.
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
			} catch (android.content.ActivityNotFoundException anfe) {
				// Cannot open google play application, open browser.
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="
						+ appPackageName)));
			}
		} else if (v == mBtnLoginFacebook) {
			/*
			 * If has not logged in facebook, login.
			 * Else logout.
			 * After that, update data.
			 */
			if (TextUtils.isEmpty(mSharePrefs.getFacebookUserToken())) {
				loginFacebook();
			} else {
				mSharePrefs.logoutFacebook();
			}
			updateFacebookData();
		} else if (v == mTvClearCache) { // Clear image cache.
			ImageLoader.getInstance().clearDiscCache();
			ImageLoader.getInstance().clearMemoryCache();
		}
	}
}

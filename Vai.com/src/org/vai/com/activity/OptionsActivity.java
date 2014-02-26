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
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class OptionsActivity extends SherlockActivity implements IFacebookCallBack, OnClickListener {

	private AdView adView;

	private SharePrefs mSharePrefs = SharePrefs.getInstance();
	private CheckBox mCbHorizontal;
	private CheckBox mCbVertical;
	private Button mBtnLoginFacebook;
	private TextView mTvFacebookName;
	private TextView mTvRateApp;
	private TextView mTvClearCache;
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

		mBtnLoginFacebook.setOnClickListener(this);
		mTvRateApp.setOnClickListener(this);
		mTvClearCache.setOnClickListener(this);

		// For admob.
		adView = (AdView) this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(R.drawable.icon_back);

		init(); // Define view.
	}

	@Override
	public void onPause() {
		adView.pause();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		adView.resume();
	}

	@Override
	public void onDestroy() {
		adView.destroy();
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
		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		if (Session.getActiveSession().isOpened() && mFacebookUtils != null) {
			mFacebookUtils.getFacebookInfo(Session.getActiveSession());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSuccess(Session session) {
		updateFacebookData();
	}

	@Override
	public void onFailed() {
		Toast toast = Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	@Override
	public void onClick(View v) {
		if (v == mTvRateApp) {
			String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
			} catch (android.content.ActivityNotFoundException anfe) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="
						+ appPackageName)));
			}
		} else if (v == mBtnLoginFacebook) {
			if (TextUtils.isEmpty(mSharePrefs.getFacebookUserToken())) {
				loginFacebook();
			} else {
				mSharePrefs.logoutFacebook();
			}
			updateFacebookData();
		} else if (v == mTvClearCache) {
			ImageLoader.getInstance().clearDiscCache();
			ImageLoader.getInstance().clearMemoryCache();
		}
	}
}

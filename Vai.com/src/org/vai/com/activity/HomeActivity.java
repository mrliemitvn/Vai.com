package org.vai.com.activity;

import org.vai.com.R;
import org.vai.com.VaiApplication;
import org.vai.com.appinterface.IAdapterCallBack;
import org.vai.com.appinterface.IFacebookCallBack;
import org.vai.com.fragment.HomeFragment;
import org.vai.com.fragment.HomeHorizontalFragment;
import org.vai.com.fragment.HomeMenuFragment;
import org.vai.com.fragment.HomeVerticalFragment;
import org.vai.com.provider.DbContract.Category;
import org.vai.com.provider.DbContract.LikeState;
import org.vai.com.provider.SharePrefs;
import org.vai.com.utils.Consts;
import org.vai.com.utils.FacebookUtils;
import org.vai.com.utils.Logger;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class HomeActivity extends SlidingFragmentActivity implements IAdapterCallBack, IFacebookCallBack {
	private static final String TAG = HomeActivity.class.getSimpleName();

	private AdView adView;

	private SharePrefs mSharePrefs = SharePrefs.getInstance();
	private HomeMenuFragment mMenuFragment;
	private HomeFragment mContentFragment;
	private FacebookUtils mFacebookUtils;
	private UiLifecycleHelper mUiHelper;

	private String mCategoryId = "";
	private String mConferenceId = "";
	private String mTitle = "";
	private String mImgUrl = "";
	private boolean isCallShare = false;

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

		// For admob.
		adView = (AdView) this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	/**
	 * Like action.
	 */
	private void callLikeAction() {
		if (mFacebookUtils == null) mFacebookUtils = new FacebookUtils(this, this);
		mFacebookUtils.initActiveSession();
		Bundle params = new Bundle();
		params.putString("object", Consts.URLConstants.BASE_URL + mConferenceId);
		/* make the API call */
		new Request(Session.getActiveSession(), "/me/og.likes", params, HttpMethod.POST, new Request.Callback() {
			public void onCompleted(Response response) {
				/* handle the result */
				Logger.debug(TAG, response.toString());
				if (response.getError() == null) { // Call like successfully.
					String id = response.getGraphObject().getProperty("id").toString();
					ContentValues values = new ContentValues();
					values.put(LikeState.FACEBOOK_CONTENT_LIKED_ID, id);
					values.put(LikeState.LIKE_STATE, Consts.STATE_ON);
					String where = new StringBuilder().append(LikeState._ID).append("='").append(mConferenceId)
							.append("' and ").append(LikeState.FACEBOOK_USER_ID).append("='")
							.append(mSharePrefs.getFacebookUserId()).append("'").toString();
					int resultUpdate = getContentResolver().update(LikeState.CONTENT_URI, values, where, null);
					Logger.debug(TAG, "number update = " + resultUpdate);
				}
			}
		}).executeAsync();
	}

	/**
	 * Unlike action.
	 */
	private void callUnlikeAction() {
		if (mFacebookUtils == null) mFacebookUtils = new FacebookUtils(this, this);
		mFacebookUtils.initActiveSession();
		String idFbContentLiked = "";
		String where = new StringBuilder().append(LikeState._ID).append("='").append(mConferenceId).append("' and ")
				.append(LikeState.FACEBOOK_USER_ID).append("='").append(mSharePrefs.getFacebookUserId()).append("'")
				.toString();
		Cursor cursor = getContentResolver().query(LikeState.CONTENT_URI, null, where, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			int idFbContentLikedIndex = cursor.getColumnIndex(LikeState.FACEBOOK_CONTENT_LIKED_ID);
			if (idFbContentLikedIndex > -1) idFbContentLiked = cursor.getString(idFbContentLikedIndex);
		}
		if (cursor != null) cursor.close();
		ContentValues values = new ContentValues();
		values.put(LikeState.FACEBOOK_CONTENT_LIKED_ID, "");
		values.put(LikeState.LIKE_STATE, Consts.STATE_OFF);
		int resultUpdate = getContentResolver().update(LikeState.CONTENT_URI, values, where, null);
		Logger.debug(TAG, "number update = " + resultUpdate);
		new Request(Session.getActiveSession(), idFbContentLiked, null, HttpMethod.DELETE, new Request.Callback() {
			public void onCompleted(Response response) {
				/* handle the result */
				Logger.debug(TAG, response.toString());
			}
		}).executeAsync();
	}

	/**
	 * Share content on facebook.
	 */
	private void shareOnFacebook() {
		if (mFacebookUtils == null) mFacebookUtils = new FacebookUtils(this, this);
		mFacebookUtils.initActiveSession();
		String urlShare = Consts.URLConstants.BASE_URL + mConferenceId;
		if (FacebookDialog.canPresentShareDialog(this, FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
			FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
					.setApplicationName(getResources().getString(R.string.app_name)).setName(mTitle)
					.setPicture(mImgUrl).setLink(urlShare).setDescription("")
					.setRequestCode(FacebookUtils.FACEBOOK_SHARE_REQUEST_CODE).build();
			mUiHelper.trackPendingDialogCall(shareDialog.present());
		} else {
			Bundle postParams = new Bundle();
			postParams.putString("name", getResources().getString(R.string.app_name));
			postParams.putString("caption", mTitle);
			postParams.putString("link", urlShare);
			postParams.putString("picture", mImgUrl);
			Request.Callback callback = new Request.Callback() {
				public void onCompleted(Response response) {
					FacebookRequestError error = response.getError();
					if (error != null) {
						Toast.makeText(HomeActivity.this, R.string.msg_err_share_failed, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(HomeActivity.this, R.string.msg_info_share_successfully, Toast.LENGTH_SHORT)
								.show();
					}
				}
			};
			Request request = new Request(Session.getActiveSession(), "me/feed", postParams, HttpMethod.POST, callback);
			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		getSupportActionBar().setIcon(R.drawable.main_menu_icon);

		// Initialize content and menu.
		init();

		mUiHelper = new UiLifecycleHelper(this, new StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
			}
		});
		mUiHelper.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mUiHelper.onResume();
		adView.resume();
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
		mContentFragment.setAdapterCallBack(this);
		mContentFragment.setCategoryId(mCategoryId);
		getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, mContentFragment).commit();
	}

	@Override
	protected void onPause() {
		adView.pause();
		super.onPause();
		mUiHelper.onPause();
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
	protected void onDestroy() {
		adView.destroy();
		super.onDestroy();
		mUiHelper.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent aIntent) {
		super.onActivityResult(requestCode, resultCode, aIntent);
		if (requestCode == FacebookUtils.FACEBOOK_SHARE_REQUEST_CODE) {
			mUiHelper.onActivityResult(requestCode, resultCode, aIntent, new FacebookDialog.Callback() {
				@Override
				public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
					Toast.makeText(HomeActivity.this, R.string.msg_err_share_failed, Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
					Toast.makeText(HomeActivity.this, R.string.msg_info_share_successfully, Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, aIntent);
			if (Session.getActiveSession().isOpened() && mFacebookUtils != null) {
				mFacebookUtils.getFacebookInfo(Session.getActiveSession());
			}
		}
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
		int callLikeState = bundle.getInt(Consts.JSON_LIKE, Consts.STATE_UNKNOWN);
		if (callLikeState == Consts.STATE_ON) { // Like conference.
			mConferenceId = bundle.getString(Consts.JSON_ID);
			/*
			 * If user hasn't logged in facebook, call login.
			 * else, call like action.
			 */
			if (TextUtils.isEmpty(mSharePrefs.getFacebookUserToken())) { // Not logged in, call login facebook.
				if (mFacebookUtils == null) mFacebookUtils = new FacebookUtils(this, this);
				mFacebookUtils.loginFacebook();
			} else { // Is logged in, call like action.
				callLikeAction();
			}
		} else if (callLikeState == Consts.STATE_OFF) { // Unlike conference.
			mConferenceId = bundle.getString(Consts.JSON_ID);
			callUnlikeAction();
		} else {
			int callShare = bundle.getInt(Consts.SHARE_CONFERENCE, Consts.STATE_UNKNOWN);
			if (callShare == Consts.STATE_ON) {
				// Call share to facebook.
				isCallShare = true;
				mConferenceId = bundle.getString(Consts.JSON_ID);
				mTitle = bundle.getString(Consts.JSON_TITLE);
				mImgUrl = bundle.getString(Consts.IMAGE_URL);
				if (TextUtils.isEmpty(mSharePrefs.getFacebookUserToken())) { // Not logged in, call login facebook.
					if (mFacebookUtils == null) mFacebookUtils = new FacebookUtils(this, this);
					mFacebookUtils.loginFacebook();
				} else { // Is logged in, call share action.
					shareOnFacebook();
				}
			} else {
				// Clicked on menu, change category.
				mCategoryId = bundle.getString(Consts.JSON_CATEGORY_ID);
				String categoryName = bundle.getString(Consts.JSON_NAME);
				getSupportActionBar().setTitle(categoryName);
				toggle();
				mContentFragment.setCategoryId(mCategoryId);
				mContentFragment.callApiGetConference(1);
			}
		}
	}

	@Override
	public void onSuccess(Session session) {
		// Login successfully, call like action or share to facebook.
		if (isCallShare) {
			shareOnFacebook();
			isCallShare = false; // Reset flag.
		} else {
			callLikeAction();
		}
	}

	@Override
	public void onFailed() {
		Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show();
		isCallShare = false; // Reset flag.
	}
}
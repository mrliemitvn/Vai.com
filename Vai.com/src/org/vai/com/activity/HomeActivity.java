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
import org.vai.com.provider.DbContract.Conference;
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
import android.view.Gravity;
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
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * This class display list conferences.<br>
 * Also display list categories on sliding menu.<br>
 * Handle some event like login, share to facebook.
 */
public class HomeActivity extends SlidingFragmentActivity implements IAdapterCallBack, IFacebookCallBack {
	private static final String TAG = HomeActivity.class.getSimpleName();

	/* Display AdView (Google admob). */
	private AdView adView;

	/* Share preference in application. */
	private SharePrefs mSharePrefs = SharePrefs.getInstance();

	/* Fragment display list categories and menu. */
	private HomeMenuFragment mMenuFragment;

	/* Fragment display list conference. */
	private HomeFragment mContentFragment;

	/* For login and share to facebook. */
	private FacebookUtils mFacebookUtils;
	private UiLifecycleHelper mUiHelper;

	/* Category id. */
	private String mCategoryId = "";

	/* Conference id. */
	private String mConferenceId = "";

	/* Conference title. */
	private String mTitle = "";

	/* Image url. */
	private String mImgUrl = "";

	/* This flag to use to detect call share to facebook or not. */
	private boolean isCallShare = false;

	/* Like number of conference. */
	private long likeNumber;

	/**
	 * Initialize category id and menu.
	 */
	private void init() {
		// Get first category id.
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
		AdRequest adRequest = new AdRequest();
		adView.loadAd(adRequest);
	}

	/**
	 * Like action.
	 */
	private void callLikeAction() {
		/* Initialize FacebookUtils if not set. */
		if (mFacebookUtils == null) mFacebookUtils = new FacebookUtils(this, this);
		/* Active facebook session. */
		mFacebookUtils.initActiveSession();
		Bundle params = new Bundle();
		params.putString("object", Consts.URLConstants.BASE_URL + mConferenceId);
		/* make the API call */
		new Request(Session.getActiveSession(), "/me/og.likes", params, HttpMethod.POST, new Request.Callback() {
			public void onCompleted(Response response) {
				/* handle the result */
				Logger.debug(TAG, response.toString());
				if (response.getError() == null) { // Call like successfully.
					/*
					 * After like successfully, need get id of like action and save it.
					 * This id will be used when call unlike action.
					 * This id is saved on LIKE_STATE table in database.
					 */
					/* Get id from facebook response. */
					String id = response.getGraphObject().getProperty("id").toString();
					/* Prepare data and save to database. */
					ContentValues values = new ContentValues();
					values.put(LikeState.FACEBOOK_CONTENT_LIKED_ID, id);
					values.put(LikeState.LIKE_STATE, Consts.STATE_ON);
					String where = new StringBuilder().append(LikeState._ID).append("='").append(mConferenceId)
							.append("' and ").append(LikeState.FACEBOOK_USER_ID).append("='")
							.append(mSharePrefs.getFacebookUserId()).append("'").toString();
					int resultUpdate = getContentResolver().update(LikeState.CONTENT_URI, values, where, null);
					Logger.debug(TAG, "number update = " + resultUpdate);
				} else {
					/*
					 * Like unsuccessfully, show error message and update like number again.
					 */
					// Show error message.
					Toast toast = Toast.makeText(HomeActivity.this, R.string.msg_err_cannot_like_now,
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					// Like unsuccessfully, update like number data.
					String where = new StringBuilder().append(Conference._ID).append("='").append(mConferenceId)
							.append("'").toString();
					ContentValues values = new ContentValues();
					values.put(Conference.LIKE, likeNumber);
					int resultUpdate = getContentResolver().update(Conference.CONTENT_URI, values, where, null);
					Logger.debug(TAG, "number conference is updated = " + resultUpdate);
					mContentFragment.getDataFromDb();
				}
			}
		}).executeAsync();
	}

	/**
	 * Unlike action.<br>
	 * Need get like id is saved on database. Use this id for call facebook unlike action.<br>
	 * Also update conference like state of this user and clear like id.
	 * Then call unlike action.
	 */
	private void callUnlikeAction() {
		/* Initialize FacebookUtils if not set. */
		if (mFacebookUtils == null) mFacebookUtils = new FacebookUtils(this, this);
		/* Active facebook session. */
		mFacebookUtils.initActiveSession();

		/* Get like id. */
		String idFbContentLiked = "";
		String where = new StringBuilder().append(LikeState._ID).append("='").append(mConferenceId).append("' and ")
				.append(LikeState.FACEBOOK_USER_ID).append("='").append(mSharePrefs.getFacebookUserId()).append("'")
				.toString();
		Cursor cursor = getContentResolver().query(LikeState.CONTENT_URI, null, where, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			int idFbContentLikedIndex = cursor.getColumnIndex(LikeState.FACEBOOK_CONTENT_LIKED_ID);
			if (idFbContentLikedIndex > -1) idFbContentLiked = cursor.getString(idFbContentLikedIndex);
		}

		/* Update like state and clear like id. */
		if (cursor != null) cursor.close();
		ContentValues values = new ContentValues();
		values.put(LikeState.FACEBOOK_CONTENT_LIKED_ID, "");
		values.put(LikeState.LIKE_STATE, Consts.STATE_OFF);
		int resultUpdate = getContentResolver().update(LikeState.CONTENT_URI, values, where, null);
		Logger.debug(TAG, "number update = " + resultUpdate);

		/* Call unlike action. */
		new Request(Session.getActiveSession(), idFbContentLiked, null, HttpMethod.DELETE, new Request.Callback() {
			public void onCompleted(Response response) {
				/* handle the result */
				Logger.debug(TAG, response.toString());
			}
		}).executeAsync();
	}

	/**
	 * Share content on facebook.
	 * If already installed facebook application, use it to share.
	 * Else, not installed facebook application, use facebook share api.
	 */
	private void shareOnFacebook() {
		/* Initialize FacebookUtils if not set. */
		if (mFacebookUtils == null) mFacebookUtils = new FacebookUtils(this, this);
		/* Active facebook session. */
		mFacebookUtils.initActiveSession();

		/* Create object link to share. */
		String urlShare = Consts.URLConstants.BASE_URL + mConferenceId;
		mFacebookUtils.shareToFacebook(urlShare, mTitle); // Use WebDialog.
		
		// TODO: Use ShareDialogFeature or share api.
		// /* Check if already installed facebook application or not. */
		// if (FacebookDialog.canPresentShareDialog(this, FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
		// /* Already installed facebook application, use it to share to facebook. */
		// FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
		// .setApplicationName(getResources().getString(R.string.app_name)).setName(mTitle)
		// .setPicture(mImgUrl).setLink(urlShare).setDescription("")
		// .setRequestCode(FacebookUtils.FACEBOOK_SHARE_REQUEST_CODE).build();
		// mUiHelper.trackPendingDialogCall(shareDialog.present());
		// } else { // Not installed facebook application, use facebook share api.
		// /* Prepare data. */
		// Bundle postParams = new Bundle();
		// postParams.putString("name", getResources().getString(R.string.app_name));
		// postParams.putString("caption", mTitle);
		// postParams.putString("link", urlShare);
		// postParams.putString("picture", mImgUrl);
		// /* Make request. */
		// Request.Callback callback = new Request.Callback() {
		// public void onCompleted(Response response) {
		// /* Handle the result */
		// FacebookRequestError error = response.getError();
		// Toast toast;
		// if (error != null) { // Share unsuccessfully.
		// toast = Toast.makeText(HomeActivity.this, R.string.msg_err_share_failed, Toast.LENGTH_SHORT);
		// } else { // Share successfully.
		// toast = Toast.makeText(HomeActivity.this, R.string.msg_info_share_successfully,
		// Toast.LENGTH_SHORT);
		// }
		// toast.setGravity(Gravity.CENTER, 0, 0);
		// toast.show();
		// }
		// };
		// Request request = new Request(Session.getActiveSession(), "me/feed", postParams, HttpMethod.POST, callback);
		// RequestAsyncTask task = new RequestAsyncTask(request);
		// task.execute();
		// }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		/* Set menu icon. */
		getSupportActionBar().setIcon(R.drawable.main_menu_icon);

		// Initialize content and menu.
		init();

		/* Initialize facebook ui helper for share action. */
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

		/*
		 * Set content to display conferences.
		 * If view style not change, do nothing.
		 * Else, must be change to horizontal or vertical style base on view style.
		 * NOTE: view style can get from share preference and can change it at option screen.
		 */
		int viewStyle = mSharePrefs.getShowingContentOption(); // Get view style from share preference.
		/* Check if not change view style, do nothing. */
		if (mContentFragment != null) {
			if (viewStyle == SharePrefs.HORIZONTAL_SHOWING_CONTENT
					&& mContentFragment.getClass().isAssignableFrom(HomeHorizontalFragment.class)) return;
			if (viewStyle == SharePrefs.VERTICAL_SHOWING_CONTENT
					&& mContentFragment.getClass().isAssignableFrom(HomeVerticalFragment.class)) return;
		}
		/* Create content fragment base on view style. */
		if (mSharePrefs.getShowingContentOption() == SharePrefs.HORIZONTAL_SHOWING_CONTENT) { // Horizontal style.
			mContentFragment = new HomeHorizontalFragment();
		} else { // Vertical style.
			mContentFragment = new HomeVerticalFragment();
		}
		mContentFragment.setAdapterCallBack(this); // Set call back interface to handle login or share to facebook.
		mContentFragment.setCategoryId(mCategoryId); // Set category id to get list conferences.
		getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, mContentFragment).commit();
	}

	@Override
	protected void onPause() {
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
		EasyTracker.getInstance(this).activityStop(this); // Stop google analytics for this activity.
	}

	@Override
	protected void onDestroy() {
		if (adView != null) adView.destroy(); // Destroy AdView.
		super.onDestroy();
		mUiHelper.onDestroy(); // Destroy UiLifecycleHelper.
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent aIntent) {
		super.onActivityResult(requestCode, resultCode, aIntent);
		/* Handle when activity result from other activity. */
		if (requestCode == FacebookUtils.FACEBOOK_SHARE_REQUEST_CODE) {
			/* Handle when result from share to facebook. */
			mUiHelper.onActivityResult(requestCode, resultCode, aIntent, new FacebookDialog.Callback() {
				@Override
				public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
					/* Share unsuccessfully, show error message. */
					Toast toast = Toast.makeText(HomeActivity.this, R.string.msg_err_share_failed, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}

				@Override
				public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
					/* Share successfully, show message. */
					Toast toast = Toast.makeText(HomeActivity.this, R.string.msg_info_share_successfully,
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			});
		} else {
			/* Handle when result from login facebook. */
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, aIntent);
			if (Session.getActiveSession().isOpened() && mFacebookUtils != null) {
				mFacebookUtils.getFacebookInfo(Session.getActiveSession());
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/* Add refresh button to refresh conferences. */
		menu.add(Menu.NONE, android.R.id.button1, Menu.NONE, R.string.refresh).setIcon(R.drawable.icon_refesh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/* Handle click event on action bar. */
		switch (item.getItemId()) {
		case android.R.id.home: // Click back button, toggle sliding menu.
			toggle();
			break;
		case android.R.id.button1: // Click refresh button, reload conference first page.
			if (mContentFragment != null) mContentFragment.callApiGetConference(1);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * This method will be call when user want to like a conference, unlike conference, share to facebook or change
	 * category.<br>
	 * 
	 * Use one variable to detect user action, called "callLikeState".<br>
	 * <ul>
	 * <li>If its value is Consts.STATE_ON, handle like action.
	 * <li>If its value is Consts.STATE_OFF, handle unlike action.
	 * <li>Else its value is Consts.STATE_UNKNOWN, user may want to share to facebook or change category.
	 * </ul>
	 * Use other variable to detect them, called "callShare".
	 * <ul>
	 * <li>If its value is Consts.STATE_ON, handle share action.
	 * <li>Else handle change category action.
	 * </ul>
	 */
	@Override
	public void adapterCallBack(Bundle bundle) {
		/* Use this variable to detect that user want to like or unlike conference. */
		int callLikeState = bundle.getInt(Consts.JSON_LIKE, Consts.STATE_UNKNOWN);
		if (callLikeState == Consts.STATE_ON) { // Like conference.
			mConferenceId = bundle.getString(Consts.JSON_ID);
			likeNumber = bundle.getLong(Consts.JSON_LIKE_NUMBER);
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
			likeNumber = bundle.getLong(Consts.JSON_LIKE_NUMBER);
			callUnlikeAction();
		} else {
			/* Use this variable to detect that user want to share conference or change category. */
			int callShare = bundle.getInt(Consts.SHARE_CONFERENCE, Consts.STATE_UNKNOWN);
			if (callShare == Consts.STATE_ON) { // Call share to facebook.
				isCallShare = true; // Set flag.
				/* Get data from bundle. */
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
		/* Login successfully, call like action or share to facebook. */
		if (isCallShare) { // Share to facebook.
			shareOnFacebook();
			isCallShare = false; // Reset flag.
		} else { // Like conference.
			callLikeAction();
		}
	}

	@Override
	public void onFailed() {
		/* Login failed, show error message. */
		Toast toast = Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
		isCallShare = false; // Reset flag.
	}
}
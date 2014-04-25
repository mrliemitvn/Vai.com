package org.vai.com;

import java.util.Locale;

import org.apache.http.HttpStatus;
import org.vai.com.activity.HomeActivity;
import org.vai.com.broadcastreceiver.BroadcastReceiverCallback;
import org.vai.com.broadcastreceiver.RestBroadcastReceiver;
import org.vai.com.rest.RestMethodResult;
import org.vai.com.service.Actions;
import org.vai.com.service.ServiceHelper;
import org.vai.com.utils.Consts;
import org.vai.com.utils.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gcm.GCMRegistrar;

/**
 * This class is a first activity of application.<br>
 * It will call api to server to request category and some information of application.
 */
public class MainActivity extends SherlockActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	/* Layout and TextView show notice of network */
	private RelativeLayout mRlMessageBar;
	private TextView mTvMessageBar;

	/* For GCM, push notification. */
	private AsyncTask<Void, Void, Void> mRegisterTask = null;
	private BroadcastReceiver mBackgroundServiceNotification = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			postNotificationRegisterIdToServer(intent.getStringExtra(Consts.GCM_INTENT_REGISTER_ID));
		}
	};

	/* Id to call api <get category> to server */
	private Long mRequestId;
	private RestBroadcastReceiver mRequestReceiver = new RestBroadcastReceiver(TAG, new BroadcastReceiverCallback() {

		/**
		 * Call api successfully, go to {@link HomeActivity} and finish this activity.
		 */
		@Override
		public void onSuccess() {
			startActivity(new Intent(MainActivity.this, HomeActivity.class)); // Go to {@link HomeActivity}.
			finish(); // Finish this activity.
		}

		@Override
		public void onError(int requestCode, String message) { // Call api unsuccessfully.
			// Show error message.
			if (requestCode == HttpStatus.SC_BAD_REQUEST) { // Data send incorrectly.
				showMessageBar(getResources().getString(R.string.msg_err_bad_request));
			} else if (requestCode == RestMethodResult.ERROR_CODE_NETWORK_ISSUE) {
				showMessageBar(getResources().getString(R.string.msg_err_connecting_error));
			} else {
				showMessageBar(getResources().getString(R.string.msg_err_something_wrong));
			}
		}

		@Override
		public void onDifferenceId() {
		}

		@Override
		public void onComplete() {
			mRequestId = null; // Reset request id.
		}
	});

	/* For post device GCM token to server. */
	private Long requestIdPushNotification;
	private RestBroadcastReceiver mPostGCMRegisterID = new RestBroadcastReceiver(TAG, new BroadcastReceiverCallback() {
		@Override
		public void onSuccess() {
			Logger.debug(TAG, "onSuccess post GCM Register Id");
		}

		@Override
		public void onError(int requestCode, String message) {
			Logger.debug(TAG, "onError  post GCM Register Id. Request code " + requestCode + " . Message:" + message);
		}

		@Override
		public void onDifferenceId() {
			Logger.debug(TAG, "onDifferenceId post GCM Register Id");
		}

		@Override
		public void onComplete() {
			requestIdPushNotification = null;
		}
	});

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Set language.
		setLanguage();

		// GCM, push notification.
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		registerReceiver(mBackgroundServiceNotification, new IntentFilter(Consts.GCM_BROADCAST_RECEIVER));
		registerPushNotification();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Register receiver.
		IntentFilter filter = new IntentFilter(ServiceHelper.ACTION_REQUEST_RESULT);
		registerReceiver(mRequestReceiver, filter);
		registerReceiver(mPostGCMRegisterID, filter);

		// Call api get category.
		getCategory();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Unregister receiver.
		try {
			unregisterReceiver(mRequestReceiver);
			unregisterReceiver(mPostGCMRegisterID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// For google analytics.
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
		/* Cancel all task. */
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}

		try {
			unregisterReceiver(mBackgroundServiceNotification); // Unregister receiver.
			GCMRegistrar.onDestroy(getApplicationContext());
		} catch (Exception e) {
			Logger.error(TAG, "onDestroy(). Exception:" + e.getMessage());
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	/**
     * Set language.
     */
    private void setLanguage() {
        // Get device language.
        String language = Locale.getDefault().getISO3Language();
        Resources res = this.getResources();
        // Change locale settings in the app.
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(language.toLowerCase());
        res.updateConfiguration(conf, displayMetrics);
    }

	/**
	 * Register push notification.
	 */
	private void registerPushNotification() {
		final String regId = GCMRegistrar.getRegistrationId(getApplicationContext());
		Logger.debug(TAG, "GCM REGISTER ID:" + regId);
		if (TextUtils.isEmpty(regId)) {
			GCMRegistrar.register(getApplicationContext(), Consts.PUSH_NOTIFICATION_SENDER_ID);
		} else {
			if (GCMRegistrar.isRegisteredOnServer(getApplicationContext())) {
				Logger.debug(TAG, "Device has already registered on server");
				postNotificationRegisterIdToServer(regId);
			} else {
				Logger.debug(TAG, "Not registered on server");
				mRegisterTask = new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						boolean registered = GCMIntentService.register(getApplicationContext(), regId);
						if (!registered) {
							GCMRegistrar.unregister(getApplicationContext());
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}
				};
				mRegisterTask.execute();
			}
		}
	}

	/**
	 * Post notification register id to server.
	 * 
	 * @param reqId
	 */
	private void postNotificationRegisterIdToServer(String reqId) {
		if (TextUtils.isEmpty(reqId)) return;
		Bundle bundle = new Bundle();
		bundle.putString(Consts.JSON_DEVICE, reqId);
		bundle.putString(Consts.JSON_NAME, android.os.Build.MODEL);
		bundle.putString(Consts.JSON_OS, android.os.Build.VERSION.RELEASE);

		requestIdPushNotification = ServiceHelper.getInstance(this).sendRequest(Actions.POST_GCM_TOKEN, bundle);
		mPostGCMRegisterID.setRequestId(requestIdPushNotification);
	}

	/**
	 * Get list categories from server.
	 */
	private void getCategory() {
		ServiceHelper serviceHelper = ServiceHelper.getInstance(this);
		mRequestId = serviceHelper.sendRequest(Actions.GET_CATEGORY_ACTION, new Bundle());
		mRequestReceiver.setRequestId(mRequestId);
	}

	/**
	 * Show message bar on a top screen.
	 * 
	 * @param message
	 *            message to show.
	 */
	private void showMessageBar(String message) {
		if (mRlMessageBar == null) { // Not initialize message bar.
			/* Initialize message bar with animation */
			try {
				FrameLayout vRoot = (FrameLayout) findViewById(android.R.id.content);
				LayoutInflater inflater = LayoutInflater.from(this);
				mRlMessageBar = (RelativeLayout) inflater.inflate(R.layout.common_message_bar, null);
				mTvMessageBar = (TextView) mRlMessageBar.findViewById(R.id.tvMessageBar);
				mTvMessageBar.setText(message);
				mTvMessageBar.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						mRlMessageBar.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
								R.anim.bottom_to_top_out));
						mRlMessageBar.setVisibility(View.GONE);
						// Get list categories again.
						getCategory();
					}
				});
				vRoot.addView(mRlMessageBar);
				mRlMessageBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.top_to_bottom_in));
			} catch (Exception ex) {
				ex.printStackTrace();
				Logger.error(TAG, ex.getMessage());
			}
		} else if (mRlMessageBar.getVisibility() != View.VISIBLE) { // Message bar is initialized but not showing.
			// Define message and show it with animation.
			mTvMessageBar.setText(message);
			mRlMessageBar.setVisibility(View.VISIBLE);
			mRlMessageBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.top_to_bottom_in));
		} else if (!message.equals(mTvMessageBar.getText())) { // Message bar is showing, just define message.
			mTvMessageBar.setText(message);
		}
	}
}
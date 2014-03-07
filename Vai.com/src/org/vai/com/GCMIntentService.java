/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vai.com;

import java.util.Random;

import org.vai.com.utils.Consts;
import org.vai.com.utils.Logger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {
	private static final String TAG = GCMIntentService.class.getSimpleName();
	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();
	private static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";
	private static final String GOOGLE_APP_LINK = "market://details?id=";
	private static final String GOOGLE_WEB_LINK = "https://play.google.com/store/apps/details?id=";
	private static BroadcastReceiver deleteReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String objectId = intent.getStringExtra(Consts.GCM_RECEIVE.ID); // Object id.
			Logger.debug(TAG, "remove notification: " + objectId);
		}
	};

	public GCMIntentService() {
		super(Consts.GCM_RECEIVE.PUSH_NOTIFICATION_SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		if (register(context, registrationId)) {
			Intent intent = new Intent(Consts.GCM_RECEIVE.GCM_BROADCAST_RECEIVER);
			intent.putExtra(Consts.GCM_RECEIVE.GCM_INTENT_REGISTER_ID, registrationId);
			sendBroadcast(intent);
		}
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			unregister(context, registrationId);
		} else {
			Logger.debug(TAG, "Ignoring unregister callback");
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		// notifies user
		generateNotification(context, intent);
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		// notifies user
		generateNotification(context, null);
	}

	@Override
	public void onError(Context context, String errorId) {
		Logger.error(TAG, "onError: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Logger.error(TAG, "onRecoverableError: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	public static void generateNotification(Context context, Intent aIntent) {
		// Get all field and data in intent.
		// TODO: If SETTINGS_NOTIFICATION if off, not handle push notification.
		// SharePrefs sharePrefs = SharePrefs.getInstance();
		// // check if setting notification off
		// int hasNotification = sharePrefs.getSettings(SharePrefs.SETTINGS_NOTIFICATION, Consts.STATE_ON);
		// if (hasNotification == Consts.STATE_OFF) return;

		Bundle bundle = aIntent.getExtras();
		for (String key : bundle.keySet()) {
			Object value = bundle.get(key);
			Logger.debug(TAG, "Data of intent, new message: key = " + key + " value = " + value.toString());
		}
		Intent notificationIntent = null;
		// TODO Get data from intent.
		// String type = aIntent.getStringExtra(Consts.GCM_RECEIVE.TYPE); // Type of notification.
		String message = aIntent.getStringExtra(Consts.GCM_RECEIVE.MESSAGE); // Message of notification.
		// String objectId = aIntent.getStringExtra(Consts.GCM_RECEIVE.ID); // Object id.

		// if (type == null) {
		// return;
		// }

		// if (Consts.GCM_RECEIVE.NEW_VERSION.equals(type)) {
		// // Go to google play UBox download page.
		// try {
		// notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_APP_LINK + APP_NAME));
		// } catch (ActivityNotFoundException e) {
		// // Catch error if google play is not installed in device.
		// notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_WEB_LINK + APP_NAME));
		// }
		// } else {
		// Go to app.
		notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// }

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// set icon on status bar of device.
		String title = context.getString(R.string.app_name); // Title will show on status bar of device.
		long when = System.currentTimeMillis();
		// intent when click on notification
		PendingIntent intent = PendingIntent.getActivity(context, (int) when, notificationIntent, 0);
		// intent when notification removed
		Intent deleteIntent = new Intent(NOTIFICATION_DELETED_ACTION);
		deleteIntent.putExtras(aIntent);
		context.registerReceiver(deleteReceiver, new IntentFilter(NOTIFICATION_DELETED_ACTION));
		PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context, (int) when, deleteIntent, 0);

		Notification notification = new Notification(R.drawable.img_icon_notify, message, when);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.deleteIntent = deletePendingIntent;

		/*
		 * TODO Check setting, if sound setting is on, play sound when received notification.
		 */
		// int hasSoundNotification = sharePrefs.getSettings(SharePrefs.SETTINGS_SOUND, Consts.STATE_ON);
		// if (hasSoundNotification == Consts.STATE_ON) { // Sound setting is on.
		// notification.defaults |= Notification.DEFAULT_SOUND; // Set sound.
		// }
		// if (Consts.STATE_ON == sharePrefs.getSettings(SharePrefs.SETTINGS_VIBRATE, Consts.STATE_ON)) {
		// notification.defaults |= Notification.DEFAULT_VIBRATE; // Set vibrate.
		// }
		// Show notification on status bar of device.
		int id = 0;
		notificationManager.notify(id, notification);
	}

	/**
	 * Register device.
	 * 
	 * @param context
	 *            app context.
	 * @param regId
	 *            register id.
	 * @return true if GCM register successfully.
	 */
	public static boolean register(final Context context, final String regId) {
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			try {
				GCMRegistrar.setRegisteredOnServer(context, true);
				return true;
			} catch (Exception e) {
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					Logger.error(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return false;
				}

				backoff *= 2;
			}
		}
		return false;
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	public static void unregister(final Context context, final String regId) {
		try {
			GCMRegistrar.setRegisteredOnServer(context, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

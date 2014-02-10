package org.vai.com.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class ServiceHelper {

	public static String ACTION_REQUEST_RESULT = "REQUEST_RESULT";
	public static String EXTRA_REQUEST_ID = "EXTRA_REQUEST_ID";
	public static String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";
	/** Key for custom error code from server in intent extra */
	public static String EXTRA_CUSTOM_CODE = "EXTRA_CUSTOM_CODE";
	private static final int MAX_SERVICE = 3;
	private static final String REQUEST_ID = "REQUEST_ID";

	public static final String SERVICE_CALLBACK = "org.vai.com.service.SERVICE_CALLBACK";

	private static Object lock = new Object();

	private static ServiceHelper instance;

	private Context ctx;

	private Map<String, Long> pendingRequests = new HashMap<String, Long>();

	// variable count determine what service run
	private int currentService = 0;

	private ServiceHelper(Context ctx) {
		if (ctx != null) {
			this.ctx = ctx.getApplicationContext();
		}
	}

	public static ServiceHelper getInstance(Context ctx) {
		synchronized (lock) {
			if (instance == null) {
				instance = new ServiceHelper(ctx);
			}
		}

		return instance;
	}

	public long sendRequest(String action, Bundle extras) {
		if (ctx == null) return 0;
		if (pendingRequests.containsKey(action)) {
			return pendingRequests.get(action);
		}

		// generate request ID for service
		long requestId = generateRequestID();
		pendingRequests.put(action, requestId);

		// add service callback to service
		ResultReceiver serviceCallback = new ResultReceiver(null) {

			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				handleResponse(resultCode, resultData);
			}
		};

		Intent intent = getServiceIntent();

		intent.putExtra(Actions.ACTION, action); // for processor factory
		/** serviceCallback sent to UboxService */
		intent.putExtra(SERVICE_CALLBACK, serviceCallback);
		// add request id that will be checked when receive result from service
		intent.putExtra(REQUEST_ID, requestId);

		if (extras != null) intent.putExtras(extras); // param for rest method
		// start service
		ctx.startService(intent);

		return requestId;
	}

	private Intent getServiceIntent() {
		Intent intent;
		switch (currentService) {
		case 0:
			intent = new Intent(ctx, Service1.class);
			break;
		case 1:
			intent = new Intent(ctx, Service2.class);
			break;
		case 2:
			intent = new Intent(ctx, Service3.class);
			break;
		default:
			intent = new Intent(ctx, Service1.class);
			break;
		}
		currentService++;
		// if current service is MAX_SERVICE, calk back Service1
		if (currentService == MAX_SERVICE) {
			currentService = 0;
		}
		return intent;
	}

	/**
	 * Handle response from Service
	 * 
	 * @param resultCode
	 * @param resultData
	 */
	private void handleResponse(int resultCode, Bundle resultData) {

		Intent origIntent = (Intent) resultData.getParcelable(ServiceBase.ORIGINAL_INTENT_EXTRA);
		// add custom error code from server
		String customCode = resultData.getString(EXTRA_CUSTOM_CODE);

		if (origIntent != null && ctx != null) {
			String action = origIntent.getStringExtra(Actions.ACTION);
			long requestId = origIntent.getLongExtra(REQUEST_ID, 0);
			pendingRequests.remove(action);

			Intent resultBroadcast = new Intent(ACTION_REQUEST_RESULT);
			resultBroadcast.putExtra(EXTRA_REQUEST_ID, requestId);
			resultBroadcast.putExtra(EXTRA_RESULT_CODE, resultCode);

			resultBroadcast.putExtra(EXTRA_CUSTOM_CODE, customCode);

			ctx.sendBroadcast(resultBroadcast);

		}
	}

	/**
	 * Generate a request id
	 * 
	 * @return a random request id
	 */
	private long generateRequestID() {
		long requestId = UUID.randomUUID().getLeastSignificantBits();
		return requestId;
	}

	/**
	 * Check if request pending or not
	 * 
	 * @param requestId
	 * @return true if request still pending false otherwise
	 */
	public boolean isRequestPending(long requestId) {
		return this.pendingRequests.containsValue(requestId);
	}

}
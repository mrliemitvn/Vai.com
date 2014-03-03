package org.vai.com.service;

import org.vai.com.processor.Processor;
import org.vai.com.processor.ProcessorCallback;
import org.vai.com.processor.ProcessorFactory;
import org.vai.com.utils.Logger;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

/**
 * Service is used to call api.
 */
public class ServiceBase extends IntentService {
	private static final String TAG = ServiceBase.class.getName();
	public static final String ORIGINAL_INTENT_EXTRA = "org.vai.com.service.ORIGINAL_INTENT_EXTRA";

	private Intent mOriginalRequestIntent;

	public ServiceBase(String name) {
		super(name);
	}

	protected void onHandleIntent(Intent intent) {
		mOriginalRequestIntent = intent;
		if (intent == null) return; // fix some crash intent null (not understand why it null)
		ResultReceiver mCallback = intent.getParcelableExtra(ServiceHelper.SERVICE_CALLBACK);

		String action = intent.getStringExtra(Actions.ACTION);
		Bundle extras = intent.getExtras();

		Logger.debug(TAG, "Service action request: " + action + ", request Id: " + intent.getLongExtra("REQUEST_ID", 0));
		Processor processor = ProcessorFactory.createProcessor(getApplicationContext(), action);
		processor.execute(makeProcessorCallback(mCallback), extras);

	}

	private ProcessorCallback makeProcessorCallback(final ResultReceiver mCallback) {
		return new ProcessorCallback() {
			@Override
			public void send(int resultCode) {
				if (mCallback != null) {
					mCallback.send(resultCode, getOriginalIntentBundle());
				}
			}

			@Override
			public void send(int resultCode, String... strings) {
				if (mCallback != null) {
					Bundle bundle = getOriginalIntentBundle();
					// add custom error code to bundle to send back
					bundle.putString(ServiceHelper.EXTRA_CUSTOM_CODE, strings[0]);
					mCallback.send(resultCode, bundle);
				}

			}
		};
	}

	protected Bundle getOriginalIntentBundle() {
		Bundle originalRequest = new Bundle();
		originalRequest.putParcelable(ORIGINAL_INTENT_EXTRA, mOriginalRequestIntent);
		return originalRequest;
	}
}

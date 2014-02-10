package org.vai.com.processor;

import org.vai.com.resource.Resource;
import org.vai.com.rest.RestMethodResult;

import android.content.Context;
import android.os.Bundle;

public abstract class BaseProcessor<T extends Resource> implements Processor {

	protected static final String TAG = BaseProcessor.class.getSimpleName();

	protected Context mContext;

	protected RestMethodResult<T> mResult;

	public BaseProcessor(Context context) {
		this.mContext = context;
	}

	public void execute(ProcessorCallback callback, Bundle extras) {
		// (4) Insert-Update the ContentProvider with a status column and
		// results column
		// Look at ContentProvider example, and build a content provider
		// that tracks the necessary data.
		preUpdateContentProvider(extras);

		// (5) Call the REST method
		// Create a RESTMethod class that knows how to assemble the URL,
		// and performs the HTTP operation.
		mResult = executeWS(extras);

		/*
		 * (8) Insert-Update the ContentProvider status, and insert the result on success Parsing the JSON response (on
		 * success) and inserting into the content provider
		 */
		postUpdateContentProvider();

		// (9) Operation complete callback to Service
		if (mResult.isNeedCustomErrorMsg()) {
			// send custom error message from server
			callback.send(mResult.getStatusCode(), mResult.getStatusMsg());
		} else {
			// send status code only
			callback.send(mResult.getStatusCode());
		}

	}

	/**
	 * (4) Insert-Update the ContentProvider with a status column and results column Look at ContentProvider example,
	 * and build a content provider that tracks the necessary data.
	 * 
	 * @param extras
	 *            information collected from Activity, used to insert into local database if necessary
	 */
	public abstract void preUpdateContentProvider(Bundle extras);

	/**
	 * (5) Call the REST method Create a RESTMethod class that knows how to assemble the URL, and performs the HTTP
	 * operation.
	 */
	public abstract RestMethodResult<T> executeWS(Bundle extras);

	/**
	 * (8) Insert-Update the ContentProvider status, and insert the result on success Parsing the JSON response (on
	 * success) and inserting into the content provider
	 */
	public abstract void postUpdateContentProvider();

}

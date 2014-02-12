package org.vai.com.rest;

import org.vai.com.rest.home.GetCategoryRestMethod;
import org.vai.com.rest.home.GetConferenceRestMethod;
import org.vai.com.utils.Consts.UriConsts;

import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.Bundle;

public class RestMethodFactory {
	private static RestMethodFactory instance;
	private static Object lock = new Object();
	private UriMatcher uriMatcher;
	private Context mContext;

	/**
	 * @param context
	 */
	private RestMethodFactory(Context context) {
		mContext = context.getApplicationContext();
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		/* Get category */
		uriMatcher.addURI(UriConsts.AUTHORITY, UriConsts.PATH_GET_CATEGORY_API,
				UriConsts.RESOURCE_TYPE_GET_CATEGORY_API);
		/* Get conference */
		uriMatcher.addURI(UriConsts.AUTHORITY, UriConsts.PATH_GET_CONFERENCE_API,
				UriConsts.RESOURCE_TYPE_GET_CONFERENCE_API);
	}

	public static RestMethodFactory getInstance(Context context) {
		synchronized (lock) {
			if (instance == null) {
				instance = new RestMethodFactory(context);
			}
		}
		return instance;
	}

	/**
	 * @param resourceUri
	 * @param method
	 * @param extras
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public RestMethod getRestMethod(Uri resourceUri, Method method, Bundle extras) {

		AbstractRestMethod abstractRestMethod = null;
		switch (uriMatcher.match(resourceUri)) {
		case UriConsts.RESOURCE_TYPE_GET_CATEGORY_API:
			if (method == Method.GET) {
				abstractRestMethod = new GetCategoryRestMethod(mContext);
			}
			break;
		case UriConsts.RESOURCE_TYPE_GET_CONFERENCE_API:
			if (method == Method.GET) {
				abstractRestMethod = new GetConferenceRestMethod(mContext);
			}
			break;
		default:
			break;
		}

		if (abstractRestMethod != null) {
			abstractRestMethod.setExtras(extras);
		}

		return abstractRestMethod;
	}

	public static enum Method {
		GET, POST, PUT, DELETE;
	}
}
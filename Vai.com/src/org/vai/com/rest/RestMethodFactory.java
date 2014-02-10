package org.vai.com.rest;

import org.vai.com.utils.Consts;
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
	/**
	 * @param context
	 */
	private RestMethodFactory(Context context) {
		mContext = context.getApplicationContext();
		// step (1) register URi
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(UriConsts.AUTHORITY, Consts.UriConsts.PATH_VERIFICATION,
				Consts.UriConsts.RESOURCE_TYPE_VERIFICATION);
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
		case Consts.UriConsts.RESOURCE_TYPE_VERIFICATION:
			// if (method == Method.POST) {
			// abstractRestMethod = new PhoneVerificationRestMethod(mContext);
			// } else if (method == Method.PUT) {
			// abstractRestMethod = new VerifyCodeRestMethod(mContext);
			// }
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
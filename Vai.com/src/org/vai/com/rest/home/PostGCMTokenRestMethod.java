package org.vai.com.rest.home;

import org.vai.com.resource.SimpleResource;
import org.vai.com.rest.AbstractRestMethod;
import org.vai.com.rest.Request;
import org.vai.com.rest.RestMethodFactory.Method;
import org.vai.com.utils.Consts;
import org.vai.com.utils.Consts.URLConstants;
import org.vai.com.utils.Logger;

import android.content.Context;

public class PostGCMTokenRestMethod extends AbstractRestMethod<SimpleResource> {

	private Context mContext;

	public PostGCMTokenRestMethod(Context context) {
		mContext = context;
	}

	@Override
	protected Context getContext() {
		return mContext;
	}

	@Override
	protected Request buildRequest() {
		String url = "";
		String device = "";
		String name = "";
		String os = "";
		url = URLConstants.POST_GCM_TOKEN_URL;
		if (getExtras().containsKey(Consts.JSON_DEVICE)) {
			device = getExtras().getString(Consts.JSON_DEVICE);
		}
		if (getExtras().containsKey(Consts.JSON_NAME)) {
			name = getExtras().getString(Consts.JSON_NAME);
		}
		if (getExtras().containsKey(Consts.JSON_OS)) {
			os = getExtras().getString(Consts.JSON_OS);
		}

		Request request = new Request(url);
		request.setMethod(Method.POST);
		request.addParam(Consts.JSON_DEVICE, device);
		request.addParam(Consts.JSON_NAME, name);
		request.addParam(Consts.JSON_OS, os);
		Logger.debug(TAG, url);
		return request;
	}

	@Override
	protected SimpleResource parseResponseBody(String responseBody) throws Exception {
		return new SimpleResource();
	}

}

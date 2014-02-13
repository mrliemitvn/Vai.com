package org.vai.com.rest.home;

import org.json.JSONObject;
import org.vai.com.resource.menu.ListCategoryResource;
import org.vai.com.rest.AbstractRestMethod;
import org.vai.com.rest.Request;
import org.vai.com.rest.RestMethodFactory.Method;
import org.vai.com.utils.Consts.URLConstants;
import org.vai.com.utils.Logger;

import android.content.Context;

public class GetCategoryRestMethod extends AbstractRestMethod<ListCategoryResource> {

	private Context mContext;

	public GetCategoryRestMethod(Context context) {
		mContext = context;
	}

	@Override
	protected Context getContext() {
		return mContext;
	}

	@Override
	protected Request buildRequest() {
		String url = "";
		url = URLConstants.GET_CATEGORY_URL;

		Request request = new Request(url);
		request.setMethod(Method.GET);
		Logger.debug(TAG, url);
		return request;
	}

	@Override
	protected ListCategoryResource parseResponseBody(String responseBody) throws Exception {
		JSONObject jsonObject = new JSONObject(responseBody);
		return new ListCategoryResource(jsonObject);
	}
}

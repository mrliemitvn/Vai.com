package org.vai.com.rest.home;

import org.json.JSONArray;
import org.vai.com.resource.home.ListConferenceResource;
import org.vai.com.rest.AbstractRestMethod;
import org.vai.com.rest.Request;
import org.vai.com.rest.RestMethodFactory.Method;
import org.vai.com.utils.Consts;
import org.vai.com.utils.Consts.URLConstants;
import org.vai.com.utils.Logger;

import android.content.Context;

public class GetConferenceRestMethod extends AbstractRestMethod<ListConferenceResource> {

	private Context mContext;

	public GetConferenceRestMethod(Context context) {
		mContext = context;
	}

	@Override
	protected Context getContext() {
		return mContext;
	}

	@Override
	protected Request buildRequest() {
		String categoryId = "0";
		int page = 1;
		if (getExtras().containsKey(Consts.JSON_CATEGORY_ID)) {
			categoryId = getExtras().getString(Consts.JSON_CATEGORY_ID);
		}
		if (getExtras().containsKey(Consts.JSON_PAGE)) {
			page = getExtras().getInt(Consts.JSON_PAGE, 1);
		}
		String url = "";
		url = URLConstants.GET_CONFERENCE_URL.replace(Consts.CATEGORY_PLACE_HOLDER, categoryId) + page;

		Request request = new Request(url);
		request.setMethod(Method.GET);
		Logger.debug(TAG, url);
		return request;
	}

	@Override
	protected ListConferenceResource parseResponseBody(String responseBody) throws Exception {
		JSONArray jsonArray = new JSONArray(responseBody);
		return new ListConferenceResource(jsonArray);
	}
}

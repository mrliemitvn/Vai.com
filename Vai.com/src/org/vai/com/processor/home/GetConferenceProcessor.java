package org.vai.com.processor.home;

import java.util.ArrayList;

import org.vai.com.processor.BaseProcessor;
import org.vai.com.provider.DbContract.Conference;
import org.vai.com.resource.home.ConferenceResource;
import org.vai.com.resource.home.ListConferenceResource;
import org.vai.com.rest.RestMethod;
import org.vai.com.rest.RestMethodFactory;
import org.vai.com.rest.RestMethodFactory.Method;
import org.vai.com.rest.RestMethodResult;
import org.vai.com.utils.Consts;
import org.vai.com.utils.Consts.UriConsts;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

public class GetConferenceProcessor extends BaseProcessor<ListConferenceResource> {

	private Bundle bundle;

	public GetConferenceProcessor(Context context) {
		super(context);
	}

	@Override
	public void preUpdateContentProvider(Bundle extras) {
		bundle = extras;
	}

	@Override
	public RestMethodResult<ListConferenceResource> executeWS(Bundle extras) {
		RestMethod<ListConferenceResource> restMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
				UriConsts.CONTENT_URI_GET_CONFERENCE_API, Method.GET, extras);
		RestMethodResult<ListConferenceResource> result = restMethod.execute();
		return result;
	}

	@Override
	public void postUpdateContentProvider() {
		if (mResult != null && mResult.getResource() != null) {
			int page = 1;
			String category = "0";
			String where = "";
			if (bundle.containsKey(Consts.JSON_CATEGORY_ID)) {
				category = bundle.getString(Consts.JSON_CATEGORY_ID);
			}
			if (bundle.containsKey(Consts.JSON_PAGE)) {
				page = bundle.getInt(Consts.JSON_PAGE);
			}
			if (page <= 1) {
				// Delete database.
				where = new StringBuilder().append(Conference.CATEGORY_ID).append("='").append(category).append("'")
						.toString();
				mContext.getContentResolver().delete(Conference.CONTENT_URI, where, null);
			}
			ArrayList<ConferenceResource> listConference = mResult.getResource().getListConference();
			ContentValues[] values = new ContentValues[listConference.size()];
			for (int i = 0; i < listConference.size(); i++) {
				ConferenceResource conference = listConference.get(i);
				if (TextUtils.isEmpty(conference.categoryId)) conference.categoryId = category;
				ContentValues valuesConference = conference.prepareContentValues();
				where = new StringBuilder().append(Conference._ID).append("='").append(conference.id).append("' and ")
						.append(Conference.CATEGORY_ID).append("='").append(conference.categoryId).append("'")
						.toString();
				int resultUpdate = mContext.getContentResolver().update(Conference.CONTENT_URI, valuesConference,
						where, null);
				if (resultUpdate <= 0) values[i] = valuesConference;
			}

			mContext.getContentResolver().bulkInsert(Conference.CONTENT_URI, values);
		}
	}

}

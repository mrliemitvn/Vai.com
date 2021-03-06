package org.vai.com.processor.home;

import java.util.ArrayList;

import org.vai.com.processor.BaseProcessor;
import org.vai.com.provider.DbContract.Category;
import org.vai.com.provider.DbContract.MoreWeb;
import org.vai.com.resource.menu.CategoryResource;
import org.vai.com.resource.menu.ListCategoryResource;
import org.vai.com.resource.menu.MoreWebResource;
import org.vai.com.rest.RestMethod;
import org.vai.com.rest.RestMethodFactory;
import org.vai.com.rest.RestMethodFactory.Method;
import org.vai.com.rest.RestMethodResult;
import org.vai.com.utils.Consts.UriConsts;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;

public class GetCategoryProcessor extends BaseProcessor<ListCategoryResource> {

	public GetCategoryProcessor(Context context) {
		super(context);
	}

	@Override
	public void preUpdateContentProvider(Bundle extras) {
	}

	@Override
	public RestMethodResult<ListCategoryResource> executeWS(Bundle extras) {
		RestMethod<ListCategoryResource> restMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
				UriConsts.CONTENT_URI_GET_CATEGORY_API, Method.GET, extras);
		RestMethodResult<ListCategoryResource> result = restMethod.execute();
		return result;
	}

	@Override
	public void postUpdateContentProvider() {
		if (mResult != null && mResult.getResource() != null) {
			// Delete all category before.
			mContext.getContentResolver().delete(Category.CONTENT_URI, null, null);

			ArrayList<CategoryResource> listCategoryResources = mResult.getResource().getListCategoryResources();
			ContentValues[] valuesCategory = new ContentValues[listCategoryResources.size()];
			for (int i = 0; i < listCategoryResources.size(); i++) {
				CategoryResource categoryResource = listCategoryResources.get(i);
				valuesCategory[i] = categoryResource.prepareContentValues();
			}

			// Delete all more web before.
			mContext.getContentResolver().delete(MoreWeb.CONTENT_URI, null, null);

			ArrayList<MoreWebResource> listMoreWebResources = mResult.getResource().getListMoreWebResources();
			ContentValues[] valuesMoreWeb = new ContentValues[listMoreWebResources.size()];
			for (int i = 0; i < listMoreWebResources.size(); i++) {
				MoreWebResource moreWebResource = listMoreWebResources.get(i);
				valuesMoreWeb[i] = moreWebResource.prepareContentValues();
			}

			mContext.getContentResolver().bulkInsert(Category.CONTENT_URI, valuesCategory);
			mContext.getContentResolver().bulkInsert(MoreWeb.CONTENT_URI, valuesMoreWeb);
		}
	}
}
package org.vai.com.processor.home;

import org.vai.com.processor.BaseProcessor;
import org.vai.com.resource.SimpleResource;
import org.vai.com.rest.RestMethod;
import org.vai.com.rest.RestMethodFactory;
import org.vai.com.rest.RestMethodResult;
import org.vai.com.rest.RestMethodFactory.Method;
import org.vai.com.utils.Consts.UriConsts;

import android.content.Context;
import android.os.Bundle;

public class PostGCMTokenProcessor extends BaseProcessor<SimpleResource> {

	public PostGCMTokenProcessor(Context context) {
		super(context);
	}

	@Override
	public void preUpdateContentProvider(Bundle extras) {
	}

	@Override
	public RestMethodResult<SimpleResource> executeWS(Bundle extras) {
		RestMethod<SimpleResource> restMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
				UriConsts.CONTENT_URI_POST_GCM_TOKEN_API, Method.POST, extras);
		RestMethodResult<SimpleResource> result = restMethod.execute();
		return result;
	}

	@Override
	public void postUpdateContentProvider() {
	}

}

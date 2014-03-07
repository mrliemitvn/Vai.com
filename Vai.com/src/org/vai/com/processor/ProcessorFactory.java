package org.vai.com.processor;

import org.vai.com.processor.home.GetCategoryProcessor;
import org.vai.com.processor.home.GetConferenceProcessor;
import org.vai.com.processor.home.PostGCMTokenProcessor;
import org.vai.com.service.Actions;

import android.content.Context;

public class ProcessorFactory {

	public static Processor createProcessor(Context context, String action) {
		if (Actions.GET_CATEGORY_ACTION.equals(action)) {
			return new GetCategoryProcessor(context);
		} else if (Actions.GET_CONFERENCE_ACTION.equals(action)) {
			return new GetConferenceProcessor(context);
		} else if (Actions.POST_GCM_TOKEN.equals(action)) {
			return new PostGCMTokenProcessor(context);
		}
		return null;
	}
}
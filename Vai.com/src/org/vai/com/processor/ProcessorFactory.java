package org.vai.com.processor;

import org.vai.com.processor.home.GetCategoryProcessor;
import org.vai.com.service.Actions;

import android.content.Context;

public class ProcessorFactory {

	public static Processor createProcessor(Context context, String action) {
		if (action.equals(Actions.GET_CATEGORY_ACTION)) {
			return new GetCategoryProcessor(context);
		}
		return null;
	}
}
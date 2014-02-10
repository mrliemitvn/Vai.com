package org.vai.com.resource;

import android.content.ContentValues;

public interface BaseResource {
	String _POSTING = "posting";
	String _UPDATING = "updating";
	String _DELETING = "deleting";

	public ContentValues prepareContentValues();

	public String getId();

	public String getRestRequestStatus();
}

package org.vai.com.service;

import android.content.Intent;

public class Service1 extends ServiceBase {

	public Service1() {
		super("Service1");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		super.onHandleIntent(intent);
	}

}
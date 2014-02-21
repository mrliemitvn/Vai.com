package org.vai.com.appinterface;

import com.facebook.Session;

public interface IFacebookCallBack {

	public void onSuccess(Session session);

	public void onFailed();
}

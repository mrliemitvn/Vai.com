package org.vai.com.utils;

import java.util.ArrayList;
import java.util.List;

import org.vai.com.appinterface.IFacebookCallBack;
import org.vai.com.provider.SharePrefs;

import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.model.GraphUser;

public class FacebookUtils {

	private IFacebookCallBack facebookCallBack;
	private SherlockActivity activity;

	public FacebookUtils(SherlockActivity activity, IFacebookCallBack facebookCallBack) {
		this.activity = activity;
		this.facebookCallBack = facebookCallBack;
	}

	public void loginFacebook() {
		Session currentSession = Session.getActiveSession();
		if (currentSession == null || currentSession.getState().isClosed()) {
			Session session = new Session.Builder(activity).build();
			Session.setActiveSession(session);
			currentSession = session;
		}
		if (!currentSession.isOpened()) { // User hasn't logged in.
			OpenRequest op = new Session.OpenRequest(activity);
			List<String> permissions = new ArrayList<String>();
			permissions.add("basic_info");
			permissions.add("email");
			op.setPermissions(permissions);

			Session session = new Session.Builder(activity).build();
			Session.setActiveSession(session);
			session.openForPublish(op);
		} else {
			// User has logged in
			facebookCallBack.onSuccess(currentSession);
		}
	}

	public void getFacebookInfo(final Session session) {
		// make request to the /me API
		Request.newMeRequest(session, new Request.GraphUserCallback() {
			// callback after Graph API response with user object
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (user != null) {
					SharePrefs.getInstance().saveFacebookUserToken(session.getAccessToken());
					SharePrefs.getInstance().saveFacebookUserId(user.getId());
					SharePrefs.getInstance().saveFacebookUserName(user.getName());
					facebookCallBack.onSuccess(session);
				} else {
					facebookCallBack.onFailed();
				}
			}
		}).executeAsync();
	}
}
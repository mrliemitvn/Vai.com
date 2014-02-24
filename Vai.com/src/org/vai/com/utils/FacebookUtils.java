package org.vai.com.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vai.com.appinterface.IFacebookCallBack;
import org.vai.com.provider.SharePrefs;

import android.app.Activity;

import com.facebook.AccessToken;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class FacebookUtils {

	public static final int FACEBOOK_SHARE_REQUEST_CODE = 123;
	private static final List<String> FACEBOOK_PERMISSION = Arrays.asList("basic_info", "email", "publish_stream",
			"publish_actions");

	private IFacebookCallBack facebookCallBack;
	private Activity activity;

	public FacebookUtils(Activity activity, IFacebookCallBack facebookCallBack) {
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
			permissions.add("publish_stream");
			permissions.add("publish_actions");
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

	public void initActiveSession() {
		if (Session.getActiveSession() == null || Session.getActiveSession().isClosed()) { // Recreate session.
			// Create AccessToken from token which is saved in SharePrefs.
			String token = SharePrefs.getInstance().getFacebookUserToken();
			AccessToken accessToken = AccessToken.createFromExistingAccessToken(token, null, null, null,
					FacebookUtils.FACEBOOK_PERMISSION);
			// Recreate Session.
			Session.openActiveSessionWithAccessToken(activity, accessToken, new Session.StatusCallback() {
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					if (session.isOpened()) {
						Session.setActiveSession(session);
					}
				}
			});
		}
	}
}

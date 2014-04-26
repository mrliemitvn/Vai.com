package org.vai.com.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vai.com.R;
import org.vai.com.appinterface.IFacebookCallBack;
import org.vai.com.provider.SharePrefs;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

/**
 * This class is used to working with facebook.
 */
public class FacebookUtils {

	/* Use this variable to share on facebook. */
	public static final int FACEBOOK_SHARE_REQUEST_CODE = 123;

	/* Facebook permission will be used. */
	private static final List<String> FACEBOOK_PERMISSION = Arrays.asList("basic_info", "email", "publish_stream",
			"publish_actions");

	/* Use this interface when logged in and got facebook user information. */
	private IFacebookCallBack facebookCallBack;

	/* Activity is used in facebook process. */
	private Activity activity;

	/**
	 * Constructor create {@link FacebookUtils} object.
	 * 
	 * @param activity
	 *            activity to set.
	 * @param facebookCallBack
	 *            facebookCallBack interface to set.
	 */
	public FacebookUtils(Activity activity, IFacebookCallBack facebookCallBack) {
		this.activity = activity;
		this.facebookCallBack = facebookCallBack;
	}

	/**
	 * Login facebook.<br>
	 * After logged in, get facebook user information.
	 */
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

	/**
	 * Get facebook user information.
	 * 
	 * @param session
	 *            session facebook.
	 */
	public void getFacebookInfo(final Session session) {
		// make request to the /me API
		Request.newMeRequest(session, new Request.GraphUserCallback() {
			// callback after Graph API response with user object
			@Override
			public void onCompleted(GraphUser user, Response response) {
				/*
				 * If got user information, save to share preference and notify successfully.
				 * Else notify unsuccessfully.
				 */
				if (user != null) { // Successful, save information.
					SharePrefs.getInstance().saveFacebookUserToken(session.getAccessToken()); // Save token.
					SharePrefs.getInstance().saveFacebookUserId(user.getId()); // Save facebook user id.
					SharePrefs.getInstance().saveFacebookUserName(user.getName()); // Save facebook user name.
					facebookCallBack.onSuccess(session); // Notify successfully.
				} else { // Unsuccessful.
					facebookCallBack.onFailed();
				}
			}
		}).executeAsync();
	}

	/**
	 * Initialize facebook active session again.
	 */
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

	/**
	 * Share to facebook.
	 * 
	 * @param urlShare
	 *            the url to share.
	 * @param title
	 *            the title.
	 */
	public void shareToFacebook(String urlShare, String title) {
		Bundle params = new Bundle();
		params.putString("name", title);
		params.putString("link", urlShare);
		params.putString("caption", activity.getResources().getString(R.string.app_name));

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(activity, Session.getActiveSession(), params))
				.setOnCompleteListener(new OnCompleteListener() {
					@Override
					public void onComplete(Bundle values, FacebookException error) {
						if (error != null) {
							/* Share unsuccessfully, show error message. */
							Toast toast = Toast.makeText(activity, R.string.msg_err_share_failed, Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						} else {
							/* Share successfully, show message. */
							Toast toast = Toast.makeText(activity, R.string.msg_info_share_successfully,
									Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
					}
				}).build();
		feedDialog.show();
	}
}

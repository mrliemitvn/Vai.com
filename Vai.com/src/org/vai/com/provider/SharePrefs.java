package org.vai.com.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.Session;

/**
 * This class save some application information in share preference.
 */
public class SharePrefs {

	/* Default string value. */
	public static final String DEFAULT_BLANK = "";

	/* Key to save ads id. */
	public static final String ADS_ID = "ads_id";

	/* Key to save facebook app id. */
	public static final String FACEBOOK_APP_ID = "facebook_app_id";

	/* Key to save facebook user id. */
	public static final String FACEBOOK_USER_ID = "facebook_user_id";

	/* Key to save facebook user name. */
	public static final String FACEBOOK_USER_NAME = "facebook_user_name";

	/* Key to save facebook user token. */
	public static final String FACEBOOK_USER_TOKEN = "facebook_token";

	/* Key to save showing content option. */
	public static final String SHOWING_CONTENT_OPTION = "show_content_option";

	/* Value to showing content on listView. */
	public static final int VERTICAL_SHOWING_CONTENT = 0;

	/* Value to showing content on viewPager. */
	public static final int HORIZONTAL_SHOWING_CONTENT = 1;

	private static SharePrefs instance = new SharePrefs();
	private SharedPreferences sharedPreferences;

	public static SharePrefs getInstance() {
		return instance;
	}

	public void init(Context ctx) {
		if (sharedPreferences == null) {
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		}
	}

	/**
	 * Clear share preference.
	 */
	public void clear() {
		sharedPreferences.edit().clear().commit();
	}

	/**
	 * Save a string value in share preference.
	 * 
	 * @param key
	 *            The name of the preference to modify.
	 * @param value
	 *            string value to save.
	 */
	public void save(String key, String value) {
		sharedPreferences.edit().putString(key, value).commit();
	}

	/**
	 * Get string value has been saved in share preference.
	 * 
	 * @param key
	 *            The name of the preference to retrieve.
	 * @param _default
	 *            value to return if this preference does not exist.
	 * @return Returns the preference value if it exists, or defValue. Throws ClassCastException if there is a
	 *         preference with this name that is not a String.
	 */
	public String get(String key, String _default) {
		return sharedPreferences.getString(key, _default);
	}

	/**
	 * Save a integer value in share preference.
	 * 
	 * @param key
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference.
	 */
	public void save(String key, int value) {
		sharedPreferences.edit().putInt(key, value).commit();
	}

	/**
	 * Get integer value has been saved in share preference.
	 * 
	 * @param key
	 *            The name of the preference to retrieve.
	 * @param defValue
	 *            Value to return if this preference does not exist.
	 * 
	 * @return Returns the preference value if it exists, or defValue. Throws ClassCastException if there is a
	 *         preference with this name that is not an int.
	 */
	public int get(String key, int defValue) {
		return sharedPreferences.getInt(key, defValue);
	}

	/**
	 * Save boolean value in share preference.
	 * 
	 * @param key
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference.
	 */
	public void save(String key, boolean value) {
		sharedPreferences.edit().putBoolean(key, value).commit();
	}

	/**
	 * Get boolean value has been saved in share preference.
	 * 
	 * @param key
	 *            The name of the preference to retrieve.
	 * @param _default
	 *            Value to return if this preference does not exist.
	 * @return Returns the preference value if it exists, or defValue. Throws ClassCastException if there is a
	 *         preference with this name that is not a boolean.
	 */
	public boolean get(String key, boolean _default) {
		return sharedPreferences.getBoolean(key, _default);
	}

	/**
	 * Save ads id in share preference.
	 * 
	 * @param adsId
	 *            ads id to save.
	 */
	public void saveAdsId(String adsId) {
		save(ADS_ID, adsId);
	}

	/**
	 * Get ads id has been save in share preference.
	 * 
	 * @return ads id has been save in share preference or {@link #DEFAULT_BLANK} if this preference does not
	 *         exist.
	 */
	public String getAdsId() {
		return get(ADS_ID, DEFAULT_BLANK);
	}

	/**
	 * Save facebook app id in share preference.
	 * 
	 * @param facebookAppId
	 *            facebook app id to save.
	 */
	public void saveFacebookAppId(String facebookAppId) {
		save(FACEBOOK_APP_ID, facebookAppId);
	}

	/**
	 * Get facebook app id has been saved in share preference.
	 * 
	 * @return facebook app id has been saved in share preference or {@link #DEFAULT_BLANK} if this preference does not
	 *         exist.
	 */
	public String getFacebookAppId() {
		return get(FACEBOOK_APP_ID, DEFAULT_BLANK);
	}

	/**
	 * Save showing content option in share preference.
	 * 
	 * @param option
	 *            {@link #VERTICAL_SHOWING_CONTENT} if want to show content on listview.<br>
	 *            or {@link #HORIZONTAL_SHOWING_CONTENT} if want to show content on viewPager.
	 */
	public void setShowingContentOption(int option) {
		save(SHOWING_CONTENT_OPTION, option);
	}

	/**
	 * Get showing content option has been saved in share preference.
	 * 
	 * @return showing content option has been saved in share preference.<br>
	 *         {@link #HORIZONTAL_SHOWING_CONTENT} or {@link #VERTICAL_SHOWING_CONTENT}
	 */
	public int getShowingContentOption() {
		return get(SHOWING_CONTENT_OPTION, VERTICAL_SHOWING_CONTENT);
	}

	/**
	 * Save facebook user id in share preference.
	 * 
	 * @param facebookUseId
	 *            facebook user id to save.
	 */
	public void saveFacebookUserId(String facebookUseId) {
		save(FACEBOOK_USER_ID, facebookUseId);
	}

	/**
	 * Get facebook user id has been saved in share preference.
	 * 
	 * @return facebook user id has been saved in share preference or {@link #DEFAULT_BLANK} if this preference does not
	 *         exist.
	 */
	public String getFacebookUserId() {
		return get(FACEBOOK_USER_ID, DEFAULT_BLANK);
	}

	/**
	 * Save facebook user name in share preference.
	 * 
	 * @param facebookName
	 *            facebook user name to saved.
	 */
	public void saveFacebookUserName(String facebookName) {
		save(FACEBOOK_USER_NAME, facebookName);
	}

	/**
	 * Get facebook user name has been saved in share preference.
	 * 
	 * @return facebook user name has been saved in share preference or {@link #DEFAULT_BLANK} if this preference does
	 *         not exist.
	 */
	public String getFacebookUserName() {
		return get(FACEBOOK_USER_NAME, DEFAULT_BLANK);
	}

	/**
	 * Save facebook user token in share preference.
	 * 
	 * @param token
	 *            token to save.
	 */
	public void saveFacebookUserToken(String token) {
		save(FACEBOOK_USER_TOKEN, token);
	}

	/**
	 * Get facebook user token has been saved in share preference.
	 * 
	 * @return facebook user token has been saved in share preference or {@link #DEFAULT_BLANK} if this preference does
	 *         not exist.
	 */
	public String getFacebookUserToken() {
		return get(FACEBOOK_USER_TOKEN, DEFAULT_BLANK);
	}

	/**
	 * Method will be called when user logout facebook.
	 * This method will close facebook session and clear all facebook user information.
	 */
	public void logoutFacebook() {
		// Close facebook session.
		if (Session.getActiveSession() != null) {
			Session.getActiveSession().closeAndClearTokenInformation();
		}
		Session.setActiveSession(null);
		saveFacebookUserToken(DEFAULT_BLANK); // Clear facebook user token.
		saveFacebookUserId(DEFAULT_BLANK); // Clear facebook user id.
		saveFacebookUserName(DEFAULT_BLANK); // Clear facebook user name.
	}
}
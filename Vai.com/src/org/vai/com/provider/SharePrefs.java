package org.vai.com.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePrefs {

	public static final String DEFAULT_BLANK = "";
	public static final String ADS_ID = "ads_id";
	public static final String FACEBOOK_APP_ID = "facebook_app_id";
	public static final String SHOWING_CONTENT_OPTION = "show_content_option";
	public static final int VERTICAL_SHOWING_CONTENT = 0;
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

	public void clear() {
		// clear all
		sharedPreferences.edit().clear().commit();
	}

	public void save(String key, String value) {
		sharedPreferences.edit().putString(key, value).commit();
	}

	public void save(String key, int value) {
		sharedPreferences.edit().putInt(key, value).commit();
	}

	public String get(String key, String _default) {
		return sharedPreferences.getString(key, _default);
	}

	public int get(String key, int defValue) {
		return sharedPreferences.getInt(key, defValue);
	}

	public void save(String key, boolean value) {
		sharedPreferences.edit().putBoolean(key, value).commit();
	}

	public boolean get(String key, boolean _default) {
		return sharedPreferences.getBoolean(key, _default);
	}

	public void saveAdsId(String adsId) {
		save(ADS_ID, adsId);
	}

	public String getAdsId() {
		return get(ADS_ID, DEFAULT_BLANK);
	}

	public void saveFacebookAppId(String facebookAppId) {
		save(FACEBOOK_APP_ID, facebookAppId);
	}

	public String getFacebookAppId() {
		return get(FACEBOOK_APP_ID, DEFAULT_BLANK);
	}

	public void setShowingContentOption(int option) {
		save(SHOWING_CONTENT_OPTION, option);
	}

	public int getShowingContentOption() {
		return get(SHOWING_CONTENT_OPTION, VERTICAL_SHOWING_CONTENT);
	}
}
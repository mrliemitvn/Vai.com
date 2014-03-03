package org.vai.com.resource.menu;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.vai.com.provider.SharePrefs;
import org.vai.com.resource.Resource;
import org.vai.com.utils.Consts;

public class ListCategoryResource implements Resource {

	private ArrayList<CategoryResource> listCategoryResources = new ArrayList<CategoryResource>();
	private ArrayList<MoreWebResource> listMoreWebResources = new ArrayList<MoreWebResource>();

	public ArrayList<CategoryResource> getListCategoryResources() {
		return listCategoryResources;
	}

	public void setListCategoryResources(ArrayList<CategoryResource> listCategoryResources) {
		this.listCategoryResources = listCategoryResources;
	}

	/**
	 * @return the listMoreWebResources
	 */
	public ArrayList<MoreWebResource> getListMoreWebResources() {
		return listMoreWebResources;
	}

	/**
	 * @param listMoreWebResources
	 *            the listMoreWebResources to set
	 */
	public void setListMoreWebResources(ArrayList<MoreWebResource> listMoreWebResources) {
		this.listMoreWebResources = listMoreWebResources;
	}

	/**
	 * Handler json data received from server after call GET_CATEGORY api.
	 * 
	 * @param jsonObject
	 *            json data received from server.
	 */
	public ListCategoryResource(JSONObject jsonObject) {
		if (jsonObject != null) {
			/* Save ads code to share preference. */
			if (!jsonObject.isNull(Consts.JSON_ADS)) {
				try {
					SharePrefs.getInstance().saveAdsId(jsonObject.getString(Consts.JSON_ADS));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			/* Save facebook application id to share preference. */
			if (jsonObject.isNull(Consts.JSON_FBAPPID)) {
				try {
					SharePrefs.getInstance().saveFacebookAppId(jsonObject.getString(Consts.JSON_FBAPPID));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			/* Get all category information. */
			if (!jsonObject.isNull(Consts.JSON_CATEGORY)) {
				try {
					JSONArray jsonArray = jsonObject.getJSONArray(Consts.JSON_CATEGORY);
					if (jsonArray != null) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonCategory = jsonArray.getJSONObject(i);
							CategoryResource categoryResource = new CategoryResource(jsonCategory);
							listCategoryResources.add(categoryResource);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			/* Get all other websites information. */
			if (!jsonObject.isNull(Consts.JSON_MOREWEB)) {
				try {
					JSONArray jsonArrayMoreWeb = jsonObject.getJSONArray(Consts.JSON_MOREWEB);
					if (jsonArrayMoreWeb != null) {
						for (int i = 0; i < jsonArrayMoreWeb.length(); i++) {
							JSONArray arrayMoreWeb = jsonArrayMoreWeb.getJSONArray(i);
							MoreWebResource moreWebResource = new MoreWebResource(arrayMoreWeb);
							listMoreWebResources.add(moreWebResource);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}

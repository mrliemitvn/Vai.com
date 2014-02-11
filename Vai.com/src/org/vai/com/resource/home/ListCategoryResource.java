package org.vai.com.resource.home;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.vai.com.resource.Resource;
import org.vai.com.utils.Consts;

public class ListCategoryResource implements Resource {

	private ArrayList<CategoryResource> listCategoryResources = new ArrayList<CategoryResource>();

	public ArrayList<CategoryResource> getListCategoryResources() {
		return listCategoryResources;
	}

	public void setListCategoryResources(ArrayList<CategoryResource> listCategoryResources) {
		this.listCategoryResources = listCategoryResources;
	}

	public ListCategoryResource(JSONObject jsonObject) {
		if (jsonObject != null && !jsonObject.isNull(Consts.JSON_CATEGORY)) {
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
	}
}

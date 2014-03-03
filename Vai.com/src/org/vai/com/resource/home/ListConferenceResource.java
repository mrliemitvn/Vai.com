package org.vai.com.resource.home;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.vai.com.resource.Resource;

public class ListConferenceResource implements Resource {

	private ArrayList<ConferenceResource> listConference = new ArrayList<ConferenceResource>();

	public ArrayList<ConferenceResource> getListConference() {
		return listConference;
	}

	public void setListConference(ArrayList<ConferenceResource> listConference) {
		this.listConference = listConference;
	}

	/**
	 * Get conference data from json array data receiver from server.<br>
	 * After that, save to list
	 * 
	 * @param jsonArray
	 *            json array data received from server.
	 */
	public ListConferenceResource(JSONArray jsonArray) {
		if (jsonArray != null) {
			try {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					ConferenceResource conference = new ConferenceResource(jsonObject);
					listConference.add(conference);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

package org.vai.com.rest;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.vai.com.rest.RestMethodFactory.Method;

public class Request {

	private Method method;

	private ArrayList<NameValuePair> params;
	private ArrayList<NameValuePair> headers;
	private boolean isUpload;
	private HashMap uploadItemInfo;

	/**
	 * @return the uploadItemInfo
	 */
	public HashMap getUploadItemInfo() {
		return uploadItemInfo;
	}

	/**
	 * @param uploadItemInfo
	 *            the uploadItemInfo to set
	 */
	public void setUploadItemInfo(HashMap uploadItemInfo) {
		this.uploadItemInfo = uploadItemInfo;
	}

	/**
	 * @return the isUpload
	 */
	public boolean isUpload() {
		return isUpload;
	}

	/**
	 * @param isUpload
	 *            the isUpload to set
	 */
	public void setUpload(boolean isUpload) {
		this.isUpload = isUpload;
	}

	private String url;

	public Request(String url) {
		this.url = url;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public void addParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	public void addHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}

	public ArrayList<NameValuePair> getParams() {
		return params;
	}

	public void setParams(ArrayList<NameValuePair> params) {
		this.params = params;
	}

	public ArrayList<NameValuePair> getHeaders() {
		return headers;
	}

	public void setHeaders(ArrayList<NameValuePair> headers) {
		this.headers = headers;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}

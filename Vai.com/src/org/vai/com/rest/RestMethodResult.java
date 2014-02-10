package org.vai.com.rest;

import org.vai.com.resource.Resource;

public class RestMethodResult<T extends Resource> {

	private int statusCode = 0;
	private String statusMsg;
	private T resource;
	/** set true when server return custom error code message for ONE Http status code */
	private boolean isNeedCustomErrorMsg = false;

	/**
	 * @return the isNeedCustomErrorMsg
	 */
	public boolean isNeedCustomErrorMsg() {
		return isNeedCustomErrorMsg;
	}

	/**
	 * @param isNeedCustomErrorMsg
	 *            the isNeedCustomErrorMsg to set
	 */
	public void setNeedCustomErrorMsg(boolean isNeedCustomErrorMsg) {
		this.isNeedCustomErrorMsg = isNeedCustomErrorMsg;
	}

	public final static int ERROR_CODE_NETWORK_ISSUE = 600;
	public final static int ERROR_CODE_INVALID_RESPONSE = 601;
	public final static int ERROR_THRESHOLD = 300;

	public RestMethodResult(int statusCode, String statusMsg, T resource) {
		super();
		this.statusCode = statusCode;
		this.statusMsg = statusMsg;
		this.resource = resource;
	}

	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Used to change status message when receive custom error code from server.
	 * 
	 * @return
	 */
	public String getStatusMsg() {
		return statusMsg;
	}

	/**
	 * @param statusMsg
	 *            the statusMsg to set
	 */
	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	public T getResource() {
		return resource;
	}

}

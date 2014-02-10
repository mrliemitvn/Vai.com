package org.vai.com.rest;


public class Response {
    
    /**
     * The HTTP status code
     */
	private int responseCode;
    
    /**
     * HTTP response
     */
	private String response;

	public Response(int responseCode, String response) {
		super();
		this.responseCode = responseCode;
		this.response = response;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}	

}


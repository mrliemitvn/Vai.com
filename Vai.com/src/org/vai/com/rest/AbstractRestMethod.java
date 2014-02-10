package org.vai.com.rest;

import java.io.IOException;
import java.net.UnknownHostException;

import org.vai.com.resource.Resource;
import org.vai.com.utils.HttpClient;
import org.vai.com.utils.Logger;

import android.content.Context;
import android.os.Bundle;

public abstract class AbstractRestMethod<T extends Resource> implements RestMethod<T> {
	private Bundle extras;

	public Bundle getExtras() {
		return extras;
	}

	public void setExtras(Bundle extras) {
		this.extras = extras;
	}

	protected static final String TAG = AbstractRestMethod.class.getSimpleName();

	public RestMethodResult<T> execute() {
		Request request = buildRequest();

		Response response;
		try {
			response = doRequest(request);
			if (Logger.isEnabled(Logger.DEBUG)) {
				Logger.debug(TAG, "HTTP Code: " + response.getResponseCode());
				Logger.debug(TAG, "Response: " + response.getResponse());
			}

			if (response.getResponseCode() < 300) { // successful
				return buildResult(response);
			} else {
				return new RestMethodResult<T>(response.getResponseCode(), response.getResponse(), null);
			}
		} catch (final UnknownHostException e) {
			e.printStackTrace();
			Logger.error(TAG, e.getMessage());
			T resource = null;
			return new RestMethodResult<T>(RestMethodResult.ERROR_CODE_NETWORK_ISSUE, e.toString(), resource);
		} catch (final IOException e) {
			e.printStackTrace();
			Logger.error(TAG, e.getMessage());
			T resource = null;
			return new RestMethodResult<T>(RestMethodResult.ERROR_CODE_NETWORK_ISSUE, e.toString(), resource);
		} catch (final Exception e) { // this error happens while parse body of response
			e.printStackTrace();
			Logger.error(TAG, request.getUrl() + " abstract rest method error: " + e.getMessage());
			Logger.error(TAG, request.getUrl() + " abstract rest method error: " + e.toString());
			T resource = null;
			return new RestMethodResult<T>(RestMethodResult.ERROR_CODE_INVALID_RESPONSE, e.toString(), resource);
		}
	}

	protected abstract Context getContext();

	/**
	 * Subclasses can overwrite for full control, eg. need to do special inspection of response headers, etc.
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected RestMethodResult<T> buildResult(Response response) throws Exception {
		int status = response.getResponseCode();
		String statusMsg = "";
		T resource = null;

		resource = parseResponseBody(response.getResponse());

		return new RestMethodResult<T>(status, statusMsg, resource);
	}

	protected abstract Request buildRequest();

	protected boolean requiresAuthorization() {
		return true;
	}

	protected abstract T parseResponseBody(String responseBody) throws Exception;

	private Response doRequest(Request request) throws IOException, UnknownHostException, Exception {
		HttpClient client = new HttpClient(request, extras);
		Response reponse = client.doRequest(request.getMethod());
		return reponse;
	}

}

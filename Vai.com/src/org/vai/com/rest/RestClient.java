package org.vai.com.rest;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.vai.com.rest.RestMethodFactory.Method;
import org.vai.com.rest.RestMultipartEntity.ProgressListener;
import org.vai.com.utils.Consts;
import org.vai.com.utils.Logger;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;

public class RestClient {

	protected static final String TAG = "RestClient";
	// TODO reduce timeout when switch to real server
	private final static int REQUEST_TIME_OUT = 30 * 1000; // timeout to wait for data
	private final static int CONNECTION_TIME_OUT = 20 * 1000; // timeout to make an connection

	public enum RequestMethod {
		GET, POST, PUT, DELETE
	}

	private Bundle extras;
	private ArrayList<NameValuePair> params;
	private ArrayList<NameValuePair> headers;
	private HashMap uploadItemInfo;
	private boolean isUpload;

	private long totalSize;
	private String url;

	private int responseCode;

	private String response;

	public String getResponse() {
		return response;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public RestClient(Request request, Bundle extras) {
		this.url = request.getUrl();
		this.params = request.getParams();
		this.headers = request.getHeaders();
		this.uploadItemInfo = request.getUploadItemInfo();
		this.isUpload = request.isUpload();
		this.extras = extras;
	}

	Response execute(Method method) throws IOException, OutOfMemoryError {
		Logger.debug(TAG, "URL     : " + this.url);
		Logger.debug(TAG, "Params : " + this.params.toString());
		Logger.debug(TAG, "Method : " + method);

		switch (method) {
		case GET: {
			// add parameters
			String combinedParams = "";
			if (!params.isEmpty()) {
				combinedParams += "?";
				for (NameValuePair p : params) {
					String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
					if (combinedParams.length() > 1) {
						combinedParams += "&" + paramString;
					} else {
						combinedParams += paramString;
					}
				}
			}

			HttpGet request = new HttpGet(url + combinedParams);

			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			this.executeRequest(request, url);
			break;
		}
		case POST: {
			HttpPost request = new HttpPost(url);
			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			if (!params.isEmpty()) {
				// if upload
				if (isUpload) {
					request.setEntity(buildUploadEntity(uploadItemInfo));
				} else {
					// if not upload
					request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				}
			}

			this.executeRequest(request, url);
			break;
		}
		case PUT: {
			HttpPut request = new HttpPut(url);
			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}
			if (!params.isEmpty()) {
				// if upload
				if (isUpload) {
					request.setEntity(buildUploadEntity(uploadItemInfo));
				} else {
					// if not upload
					request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				}
			}
			this.executeRequest(request, url);
			break;

		}
		case DELETE: {
			HttpDelete request = new HttpDelete(url);
			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}
			this.executeRequest(request, url);
			break;

		}
		}

		return new Response(this.getResponseCode(), this.getResponse());

	}

	public static final HttpParams defaultHttpParams;
	static {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8);
		params.setParameter(CoreProtocolPNames.USER_AGENT, "Apache-HttpClient/Android");
		params.setParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
		HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIME_OUT);
		HttpConnectionParams.setSoTimeout(params, REQUEST_TIME_OUT);
		HttpConnectionParams.setTcpNoDelay(params, true);
		defaultHttpParams = params;
	}

	public static final ThreadSafeClientConnManager defaultConnection;
	static {
		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
		final SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", socketFactory, 443));
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		defaultConnection = new ThreadSafeClientConnManager(defaultHttpParams, schemeRegistry);
	}

	private void executeRequest(HttpUriRequest request, String url) throws IOException, OutOfMemoryError {
		HttpResponse httpResponse;
		HttpClient client = new DefaultHttpClient(defaultConnection, defaultHttpParams);
		try {
			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				response = EntityUtils.toString(entity);
			}

		} catch (IOException e) {
			defaultConnection.closeExpiredConnections();
			e.printStackTrace();
			throw e;
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	/**
	 * build entity when upload file
	 * 
	 * @param params
	 *            map contains param name and mime content type of file upload
	 * @return entity for HttpRequest
	 */
	RestMultipartEntity buildUploadEntity(HashMap uploadFileInfoMap) {
		final Messenger messenger = (Messenger) extras.get(Consts.EXTRA_UPLOAD_PROGRESS);
		RestMultipartEntity partEntity = new RestMultipartEntity(new ProgressListener() {
			@Override
			public void transferred(long num) {
				if (messenger != null) {
					Message msg = Message.obtain();
					if (msg == null) return;
					msg.arg1 = (int) ((num / (float) totalSize) * 100);
					Logger.debug(TAG, "Upload percent: " + msg.arg1);
					try {
						messenger.send(msg);
					} catch (android.os.RemoteException e1) {
						Logger.error(TAG, "Exception sending message", e1);
					}
				}
			}
		});

		for (NameValuePair p : params) {
			try {
				// get mimeType in uploadFileInfoMap
				String mimeType = (String) uploadFileInfoMap.get(p.getName());
				if (mimeType != null && !"".equals(mimeType)) {
					// if mimeType not null and not empty then add file to entity
					partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()), mimeType));
				} else {
					// else add string to entity
					partEntity.addPart(p.getName(),
							new StringBody(p.getValue(), HTTP.PLAIN_TEXT_TYPE, Charset.forName(HTTP.UTF_8)));
				}
				totalSize = partEntity.getContentLength();
			} catch (Exception e) {
				e.printStackTrace();
				Logger.error(TAG, e.getLocalizedMessage());
			}
		}
		return partEntity;
	}

	RestClient(String url) {
		this.url = url;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
	}

}
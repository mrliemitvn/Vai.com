/*
	HttpClient
	Copyright 2013 ThanhLCM

	Licensed under the Apache License, Version 2.0 (the "License");
 	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */

package org.vai.com.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;
import org.vai.com.rest.Request;
import org.vai.com.rest.Response;
import org.vai.com.rest.RestMethodFactory.Method;
import org.vai.com.rest.RestMultipartEntity;
import org.vai.com.rest.RestMultipartEntity.ProgressListener;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;

import com.nostra13.universalimageloader.core.assist.FlushedInputStream;

public class HttpClient {

	private static final String TAG = HttpClient.class.getSimpleName();

	private static final int DEFAULT_READ_TIMEOUT = 10000;
	private static final int DEFAULT_CONNECT_TIMEOUT = 15000;
	private static final int DEFAULT_MAX_RETRIES = 3;
	private static final String DEFAULT_ENCODING = "UTF-8";

	private final RequestOptions requestOptions = new RequestOptions();

	private class RequestOptions {
		public int readTimeout = DEFAULT_READ_TIMEOUT;
		public int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
		public int maxRetries = DEFAULT_MAX_RETRIES;
		public String encoding = DEFAULT_ENCODING;
	}

	private int retries = 0;
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

	/**
	 * Create a new HttpClient
	 */
	public HttpClient(Request request, Bundle extras) {
		this.url = request.getUrl();
		this.params = request.getParams();
		this.headers = request.getHeaders();
		this.uploadItemInfo = request.getUploadItemInfo();
		this.isUpload = request.isUpload();
		this.extras = extras;

	}

	/**
	 * Performs a HTTP GET/POST Request
	 * 
	 * @return id of the request
	 */
	public Response doRequest(final Method method) throws IOException, UnknownHostException, Exception {
		HttpURLConnection conn = null;
		try {
			/* append query string for GET requests */
			if (method == Method.GET) {
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
					url += combinedParams;
				}
			}

			/* open and configure the connection */
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestProperty("Connection", "Keep-Alive");

			if (method == Method.GET) {
				conn.setRequestMethod("GET");
			} else if (method == Method.POST) {
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
			} else if (method == Method.PUT) {
				conn.setRequestMethod("PUT");
				conn.setUseCaches(false);
			} else if (method == Method.DELETE) {
				conn.setRequestMethod("DELETE");
				conn.setUseCaches(false);
			}
			conn.setAllowUserInteraction(false);
			conn.setReadTimeout(requestOptions.readTimeout);
			conn.setConnectTimeout(requestOptions.connectTimeout);

			/* add headers to the connection */
			for (NameValuePair h : headers) {
				conn.addRequestProperty(h.getName(), h.getValue());
			}

			/* do post */
			if (method == Method.POST || method == Method.PUT) {
				if (isUpload) {
					// default not retry for upload
					retries = requestOptions.maxRetries;

					RestMultipartEntity partEntity = getMultipart();
					totalSize = partEntity.getContentLength();
					conn.setFixedLengthStreamingMode((int) totalSize);
					conn.addRequestProperty("Content-length", totalSize + "");
					conn.addRequestProperty(partEntity.getContentType().getName(), partEntity.getContentType()
							.getValue());
					final OutputStream os = conn.getOutputStream();
					partEntity.writeTo(os);
					os.flush();
					os.close();
				} else {
					InputStream is;
					is = new ByteArrayInputStream(URLEncodedUtils.format(params, requestOptions.encoding).getBytes());
					final OutputStream os = conn.getOutputStream();
					writeStream(os, is);
					os.flush();
					os.close();
				}
			}

			conn.connect();

			responseCode = conn.getResponseCode();

			/* do get */
			if (responseCode < 300) {
				// for slow network
				InputStream in = new FlushedInputStream(conn.getInputStream());
				response = readStream(in);
			} else {
				response = readStream(conn.getErrorStream());
			}
		} catch (final UnknownHostException e) {
			if (retries < requestOptions.maxRetries) {
				retries++;
				return doRequest(method);
			} else {
				throw e;
			}
		} catch (final IOException e) {
			// Ignore 401 response code and not retry
			responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_UNAUTHORIZED) {
				if (retries < requestOptions.maxRetries) {
					retries++;
					return doRequest(method);
				} else {
					throw e;
				}
			}
		} catch (final Exception e) {
			throw e;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return new Response(this.getResponseCode(), this.getResponse());
	}

	protected RestMultipartEntity getMultipart() throws IOException {
		final Messenger messenger = (Messenger) extras.get(Consts.EXTRA_UPLOAD_PROGRESS);
		final Object data = extras.get(Consts.EXTRA_UPLOAD_DATA);
		final RestMultipartEntity partEntity = new RestMultipartEntity(new ProgressListener() {
			@Override
			public void transferred(long num) {
				if (messenger != null) {
					Message msg = Message.obtain();
					if (msg == null) return;
					msg.arg1 = (int) ((num / (float) totalSize) * 100);
					msg.obj = data;
					Logger.debug(TAG, "Upload percent: " + msg.arg1);
					try {
						messenger.send(msg);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		for (NameValuePair p : params) {
			try {
				// get mimeType in uploadFileInfoMap
				String mimeType = (String) uploadItemInfo.get(p.getName());
				if (mimeType != null && !"".equals(mimeType)) {
					// if mimeType not null and not empty then add file to entity
					partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()), mimeType));
				} else {
					// else add string to entity
					partEntity.addPart(p.getName(),
							new StringBody(p.getValue(), HTTP.PLAIN_TEXT_TYPE, Charset.forName(HTTP.UTF_8)));
				}
			} catch (Exception e) {
				e.printStackTrace();
				Logger.error(TAG, e.getLocalizedMessage());
			}
		}
		return partEntity;
	}

	private String readStream(final InputStream is) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}

		try {
			br.close();
		} catch (final IOException e) {
		}

		try {
			is.close();
		} catch (final IOException e) {
		}

		return sb.toString();
	}

	private void writeStream(final OutputStream os, final InputStream is) throws Exception {
		final BufferedInputStream bis = new BufferedInputStream(is);
		int read = 0;
		final byte[] buffer = new byte[8192];
		while (true) {
			read = bis.read(buffer);
			if (read == -1) {
				break;
			}
			os.write(buffer, 0, read);
		}
		try {
			os.close();
		} catch (final IOException e) {
		}

		try {
			bis.close();
		} catch (final IOException e) {
		}

		try {
			is.close();
		} catch (final IOException e) {
		}
	}

	/**
	 * Gets the HttpURLConnection readTimeout
	 * 
	 * @return readTimeout in milliseconds
	 */
	public int getReadTimeout() {
		return requestOptions.readTimeout;
	}

	/**
	 * Set the HttpURLConnection readTimeout
	 * 
	 * @param readTimeout
	 *            in milliseconds
	 */
	public void setReadTimeout(final int readTimeout) {
		requestOptions.readTimeout = readTimeout;
	}

	/**
	 * Gets the HttpURLConnection connectTimeout
	 * 
	 * @return connectTimeout in milliseconds
	 */
	public int getConnectTimeout() {
		return requestOptions.connectTimeout;
	}

	/**
	 * Set the HttpURLConnection connectTimeout
	 * 
	 * @param connectTimeout
	 *            in milliseconds
	 */
	public void setConnectTimeout(final int connectTimeout) {
		requestOptions.connectTimeout = connectTimeout;
	}

	/**
	 * Get the max number of retries
	 * 
	 * @return number of retries
	 */
	public int getMaxRetries() {
		return requestOptions.maxRetries;
	}

	/**
	 * Sets the max number of retries
	 * 
	 * @param maxRetries
	 */
	public void setMaxRetries(final int maxRetries) {
		requestOptions.maxRetries = maxRetries;
	}

}
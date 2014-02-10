package org.vai.com.broadcastreceiver;

import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.vai.com.R;
import org.vai.com.rest.RestMethodResult;
import org.vai.com.service.ServiceHelper;
import org.vai.com.utils.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * Class for base for receiver data
 * 
 */
public class RestBroadcastReceiver extends BroadcastReceiver {
	private Set<String> keySet = new HashSet<String>();

	/** ID of send */
	// private long requestId;

	/** tag for logcat */
	private String tag;

	/** for call back activity or other */
	private BroadcastReceiverCallback callBack;

	public RestBroadcastReceiver(String tag, BroadcastReceiverCallback callBack) {
		this.tag = tag;
		this.callBack = callBack;
	}

	public void setRequestId(Long requestId) {
		// this.requestId = requestId;
		if (requestId != null) {
			keySet.add(requestId + "");
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		long resultRequestId = intent.getLongExtra(ServiceHelper.EXTRA_REQUEST_ID, 0);

		Logger.debug(tag, "Received intent " + intent.getAction() + ", request ID " + resultRequestId);

		// resultRequestId == requestId
		if (keySet.contains(resultRequestId + "")) {

			// call when receiver a message from server
			callBack.onComplete();

			Logger.debug(tag, "Result is for our request ID");

			int resultCode = intent.getIntExtra(ServiceHelper.EXTRA_RESULT_CODE, 0);
			String customCode = intent.getStringExtra(ServiceHelper.EXTRA_CUSTOM_CODE);
			Logger.debug(tag, "Result code = " + resultCode);

			switch (resultCode) {
			case HttpStatus.SC_OK:// 200
			case HttpStatus.SC_CREATED:// 201
			case HttpStatus.SC_NO_CONTENT:// 204
				// call when sucessful
				callBack.onSuccess();
				break;
			case HttpStatus.SC_MOVED_TEMPORARILY: // 302
				callBack.onError(resultCode, context.getString(R.string.msg_err_move_temporarily));
				break;
			case HttpStatus.SC_BAD_REQUEST: // 400
				if (TextUtils.isEmpty(customCode)) {
					callBack.onError(resultCode, context.getString(R.string.msg_err_bad_request));
				} else {
					callBack.onError(resultCode, customCode);
				}

				break;
			case HttpStatus.SC_UNAUTHORIZED: // 401
				callBack.onError(resultCode, context.getString(R.string.msg_err_invalid_token));
				break;
			case HttpStatus.SC_FORBIDDEN:// 403:
				callBack.onError(resultCode, context.getString(R.string.msg_err_forbidden));
				break;
			case HttpStatus.SC_NOT_FOUND:// 404:
				callBack.onError(resultCode, context.getString(R.string.msg_err_not_found));
				break;

			case HttpStatus.SC_CONFLICT:// 409
				callBack.onError(resultCode, context.getString(R.string.msg_err_conflict));
				break;
			case HttpStatus.SC_INTERNAL_SERVER_ERROR:// 500
				if (TextUtils.isEmpty(customCode)) {
					callBack.onError(resultCode, context.getString(R.string.msg_err_server_error));
				} else {
					callBack.onError(resultCode, customCode);
				}
				break;
			case RestMethodResult.ERROR_CODE_NETWORK_ISSUE:// 600:
				callBack.onError(resultCode, context.getString(R.string.msg_err_connecting_error));
				break;
			case RestMethodResult.ERROR_CODE_INVALID_RESPONSE:// 601:
				callBack.onError(resultCode, context.getString(R.string.msg_err_data_error));
			default:
				break;
			}
		} else {
			Logger.debug(tag, "Result is NOT for our request ID");
			callBack.onDifferenceId();
		}
	}

}
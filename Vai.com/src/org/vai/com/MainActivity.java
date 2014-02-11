package org.vai.com;

import org.apache.http.HttpStatus;
import org.vai.com.activity.HomeActivity;
import org.vai.com.broadcastreceiver.BroadcastReceiverCallback;
import org.vai.com.broadcastreceiver.RestBroadcastReceiver;
import org.vai.com.rest.RestMethodResult;
import org.vai.com.service.Actions;
import org.vai.com.service.ServiceHelper;
import org.vai.com.utils.Logger;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class MainActivity extends SherlockActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private RelativeLayout mRlMessageBar;
	private TextView mTvMessageBar;

	private Long mRequestId;
	private RestBroadcastReceiver mRequestReceiver = new RestBroadcastReceiver(TAG, new BroadcastReceiverCallback() {

		@Override
		public void onSuccess() {
			startActivity(new Intent(MainActivity.this, HomeActivity.class));
			finish();
		}

		@Override
		public void onError(int requestCode, String message) {
			if (requestCode == HttpStatus.SC_BAD_REQUEST) { // Data send incorrectly.
				showMessageBar(getResources().getString(R.string.msg_err_bad_request));
			} else if (requestCode == RestMethodResult.ERROR_CODE_NETWORK_ISSUE) {
				showMessageBar(getResources().getString(R.string.msg_err_connecting_error));
			}
		}

		@Override
		public void onDifferenceId() {
		}

		@Override
		public void onComplete() {
			mRequestId = null; // Reset request id.
		}
	});

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter(ServiceHelper.ACTION_REQUEST_RESULT);
		registerReceiver(mRequestReceiver, filter);

		// Call api get category.
		ServiceHelper serviceHelper = ServiceHelper.getInstance(this);
		mRequestId = serviceHelper.sendRequest(Actions.GET_CATEGORY_ACTION, new Bundle());
		mRequestReceiver.setRequestId(mRequestId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			unregisterReceiver(mRequestReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showMessageBar(String message) {
		if (mRlMessageBar == null) {
			try {
				FrameLayout vRoot = (FrameLayout) findViewById(android.R.id.content);
				LayoutInflater inflater = LayoutInflater.from(this);
				mRlMessageBar = (RelativeLayout) inflater.inflate(R.layout.common_message_bar, null);
				mTvMessageBar = (TextView) mRlMessageBar.findViewById(R.id.tvMessageBar);
				mTvMessageBar.setText(message);
				mTvMessageBar.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						mRlMessageBar.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
								R.anim.bottom_to_top_out));
						mRlMessageBar.setVisibility(View.GONE);
					}
				});
				vRoot.addView(mRlMessageBar);
				mRlMessageBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.top_to_bottom_in));
			} catch (Exception ex) {
				ex.printStackTrace();
				Logger.error(TAG, ex.getMessage());
			}
		} else if (mRlMessageBar.getVisibility() != View.VISIBLE) {
			mTvMessageBar.setText(message);
			mRlMessageBar.setVisibility(View.VISIBLE);
			mRlMessageBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.top_to_bottom_in));
		} else if (!message.equals(mTvMessageBar.getText())) {
			mTvMessageBar.setText(message);
		}
	}
}
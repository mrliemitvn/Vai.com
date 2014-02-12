package org.vai.com.fragment;

import org.apache.http.HttpStatus;
import org.vai.com.R;
import org.vai.com.rest.RestMethodResult;
import org.vai.com.utils.Logger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class BaseFragment extends SherlockFragment {
	private static final String TAG = BaseFragment.class.getSimpleName();

	private RelativeLayout mRlMessageBar;
	private TextView mTvMessageBar;

	public void showMessageBar(int requestCode) {
		if (requestCode == HttpStatus.SC_BAD_REQUEST) { // Data send incorrectly.
			showMessageBar(getResources().getString(R.string.msg_err_bad_request));
		} else if (requestCode == RestMethodResult.ERROR_CODE_NETWORK_ISSUE) {
			showMessageBar(getResources().getString(R.string.msg_err_connecting_error));
		} else {
			showMessageBar(getResources().getString(R.string.msg_err_something_wrong));
		}
	}

	public void showMessageBar(String message) {
		if (getActivity() == null) return;
		if (mRlMessageBar == null) {
			try {
				FrameLayout vRoot = (FrameLayout) getActivity().findViewById(android.R.id.content);
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				mRlMessageBar = (RelativeLayout) inflater.inflate(R.layout.common_message_bar, null);
				mTvMessageBar = (TextView) mRlMessageBar.findViewById(R.id.tvMessageBar);
				mTvMessageBar.setText(message);
				mTvMessageBar.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						mRlMessageBar.startAnimation(AnimationUtils.loadAnimation(getActivity(),
								R.anim.bottom_to_top_out));
						mRlMessageBar.setVisibility(View.GONE);
					}
				});
				vRoot.addView(mRlMessageBar);
				mRlMessageBar.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.top_to_bottom_in));
			} catch (Exception ex) {
				ex.printStackTrace();
				Logger.error(TAG, ex.getMessage());
			}
		} else if (mRlMessageBar.getVisibility() != View.VISIBLE) {
			mTvMessageBar.setText(message);
			mRlMessageBar.setVisibility(View.VISIBLE);
			mRlMessageBar.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.top_to_bottom_in));
		} else if (!message.equals(mTvMessageBar.getText())) {
			mTvMessageBar.setText(message);
		}
	}
}

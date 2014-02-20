package org.vai.com.activity;

import org.vai.com.R;
import org.vai.com.provider.SharePrefs;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class OptionsActivity extends SherlockActivity {

	private CheckBox mCbHorizontal;
	private CheckBox mCbVertical;
	private Button mBtnLoginFacebook;
	private TextView mTvFacebookName;

	/**
	 * Login facebook.
	 */
	private void loginFacebook() {
		// start Facebook Login
		Session.openActiveSession(OptionsActivity.this, true, new Session.StatusCallback() {
			// callback when session changes state
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {
					SharePrefs.getInstance().saveFacebookUserToken(session.getAccessToken());
					// make request to the /me API
					Request.newMeRequest(session, new Request.GraphUserCallback() {
						// callback after Graph API response with user object
						@Override
						public void onCompleted(GraphUser user, Response response) {
							if (user != null) {
								SharePrefs.getInstance().saveFacebookUserId(user.getId());
								SharePrefs.getInstance().saveFacebookUserName(user.getName());
								mTvFacebookName.setText(user.getName());
							}
						}
					});
				}
			}
		});
	}

	/**
	 * Update data on view.
	 */
	private void updateData() {
		// Set up view type of content.
		if (SharePrefs.getInstance().getShowingContentOption() == SharePrefs.HORIZONTAL_SHOWING_CONTENT) {
			mCbHorizontal.setChecked(true);
			mCbVertical.setChecked(false);
		} else {
			mCbHorizontal.setChecked(false);
			mCbVertical.setChecked(true);
		}

		// Show facebook name if logined.
		if (TextUtils.isEmpty(SharePrefs.getInstance().getFacebookUserToken())) {
			mTvFacebookName.setText(R.string.options_login_facebook);
		} else {
			mTvFacebookName.setText(SharePrefs.getInstance().getFacebookUserName());
		}
	}

	/**
	 * Define view.
	 */
	private void init() {
		mCbHorizontal = (CheckBox) findViewById(R.id.cbHorizontal);
		mCbVertical = (CheckBox) findViewById(R.id.cbVertical);
		mBtnLoginFacebook = (Button) findViewById(R.id.btnLoginFacebook);
		mTvFacebookName = (TextView) findViewById(R.id.tvFacebookName);

		mCbHorizontal.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mCbVertical.setChecked(!isChecked);
				if (isChecked) {
					SharePrefs.getInstance().setShowingContentOption(SharePrefs.HORIZONTAL_SHOWING_CONTENT);
				} else {
					SharePrefs.getInstance().setShowingContentOption(SharePrefs.VERTICAL_SHOWING_CONTENT);
				}
			}
		});

		mCbVertical.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mCbHorizontal.setChecked(!isChecked);
				if (isChecked) {
					SharePrefs.getInstance().setShowingContentOption(SharePrefs.VERTICAL_SHOWING_CONTENT);
				} else {
					SharePrefs.getInstance().setShowingContentOption(SharePrefs.HORIZONTAL_SHOWING_CONTENT);
				}
			}
		});

		// Update data on view.
		updateData();

		mBtnLoginFacebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loginFacebook();
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(R.drawable.icon_back);

		init(); // Define view.
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}

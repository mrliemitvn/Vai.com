package org.vai.com.activity;

import org.vai.com.R;
import org.vai.com.provider.SharePrefs;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class OptionsActivity extends SherlockActivity {

	private CheckBox mCbHorizontal;
	private CheckBox mCbVertical;

	/**
	 * Define view.
	 */
	private void init() {
		mCbHorizontal = (CheckBox) findViewById(R.id.cbHorizontal);
		mCbVertical = (CheckBox) findViewById(R.id.cbVertical);

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

		if (SharePrefs.getInstance().getShowingContentOption() == SharePrefs.HORIZONTAL_SHOWING_CONTENT) {
			mCbHorizontal.setChecked(true);
			mCbVertical.setChecked(false);
		} else {
			mCbHorizontal.setChecked(false);
			mCbVertical.setChecked(true);
		}
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}

package org.vai.com;

import org.vai.com.activity.HomeActivity;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

public class MainActivity extends SherlockActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		startActivity(new Intent(this, HomeActivity.class));
		finish();
	}
}
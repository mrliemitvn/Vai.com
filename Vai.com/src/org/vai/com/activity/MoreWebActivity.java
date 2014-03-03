package org.vai.com.activity;

import org.vai.com.R;
import org.vai.com.VaiApplication;
import org.vai.com.adapter.MoreWebAdapter;
import org.vai.com.provider.DbContract.MoreWeb;
import org.vai.com.resource.menu.MoreWebResource;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * This class display list other websites.
 */
public class MoreWebActivity extends SherlockFragmentActivity implements LoaderCallbacks<Cursor> {

	/* Display AdView (Google admob). */
	private AdView adView;

	/* Loader to get more website data from database. */
	private static final int LOADER_MORE_WEB = 1;

	/* ListView and adapter display list websites. */
	private ListView mListView;
	private MoreWebAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);

		/* Show back button on action bar and set its icon. */
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(R.drawable.icon_back);

		// Initialize cursor loader.
		getSupportLoaderManager().initLoader(LOADER_MORE_WEB, null, this);

		// Initialize view.
		mListView = (ListView) findViewById(R.id.listView);
		mAdapter = new MoreWebAdapter(this, null);
		mListView.setAdapter(mAdapter);

		// Set event.
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
				if (cursor != null) {
					/* Go to website by browser. */
					MoreWebResource moreWebResource = new MoreWebResource(cursor);
					String link = moreWebResource.link;
					if (!TextUtils.isEmpty(link) && !link.startsWith("http://") && !link.startsWith("https://")) {
						link = "http://" + link;
					}
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
					startActivity(browserIntent);
				}
			}
		});

		// For admob.
		adView = (AdView) this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest();
		adView.loadAd(adRequest);
	}

	@Override
	public void onDestroy() {
		if (adView != null) adView.destroy(); // Destroy AdView.
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// For google anlytics.
		EasyTracker.getInstance(this).activityStart(this);
		VaiApplication.getGaTracker().set(Fields.SCREEN_NAME, this.getClass().getName());
		VaiApplication.getGaTracker().send(MapBuilder.createAppView().build());
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Stop google analytics for this activity.
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) { // Finish activity when click on back button.
		case android.R.id.home:
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		switch (id) {
		case LOADER_MORE_WEB:
			return new CursorLoader(this, MoreWeb.CONTENT_URI, null, null, null, null); // Load more website data.
		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		int id = loader.getId();
		switch (id) {
		case LOADER_MORE_WEB:
			mAdapter.swapCursor(cursor); // Update adapter data.
			break;

		default:
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null); // Reset data.
	}
}

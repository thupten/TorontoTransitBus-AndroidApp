package com.thuptencho.torontotransitbus.routes;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.widget.Spinner;
import android.widget.TextView;

import com.thuptencho.torontotransitbus.C;
import com.thuptencho.torontotransitbus.R;
import com.thuptencho.torontotransitbus.models.Direction;
import com.thuptencho.torontotransitbus.models.Route;

public class RouteDetailActivity extends Activity {
	private TextView mTVTitle = null;
	private DirectionsAdapter mAdapter = null;
	private Spinner mDirectionSpinner = null;
	private Route mRoute = null;

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("adsf","asdfasdf");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		int routeId = i.getIntExtra("position", 999999);

		mRoute = new Route();
		mRoute.mTag = i.getStringExtra("routeTag");
		mRoute.mTitle = i.getStringExtra("routeTitle");

		IntentFilter ifilter = new IntentFilter(C.BROADCAST_ROUTES_SINGLE_UPDATED_ACTION);
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, ifilter);

		setContentView(R.layout.activity_route_detail);
		mTVTitle = (TextView) findViewById(R.id.labelRoute);
		mTVTitle.setText(mRoute.mTitle);

		mDirectionSpinner = (Spinner) findViewById(R.id.spinnerDirection);
		Cursor cursor = null;
		String[] projection = new String[] { Direction.KEY_ID, Direction.KEY_TAG, Direction.KEY_TITLE,
				Direction.KEY_NAME };
		String condition = "(" + Direction.KEY_ROUTE__TAG + "='" + mRoute.mTag + "')AND("
				+ Direction.KEY_USEFORUI + "='true')";
		Uri contentUriRouteSingle = ContentUris.withAppendedId(C.CONTENT_URI_ROUTE, routeId);
		cursor = getContentResolver().query(contentUriRouteSingle, projection, condition,
				null, null);
		this.mAdapter = new DirectionsAdapter(getApplicationContext(), R.layout.route_detail_direction_view,
				cursor, new String[] { Direction.KEY_TITLE }, new int[] { R.id.direction_text_view }, 0);
		this.mDirectionSpinner.setAdapter(mAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_detail, menu);
		return true;
	}

}

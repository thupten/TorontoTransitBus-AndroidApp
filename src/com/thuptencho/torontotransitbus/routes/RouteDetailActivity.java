package com.thuptencho.torontotransitbus.routes;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.thuptencho.torontotransitbus.C;
import com.thuptencho.torontotransitbus.R;
import com.thuptencho.torontotransitbus.models.Direction;
import com.thuptencho.torontotransitbus.utilities.MyLogger;

public class RouteDetailActivity extends Activity {
	private TextView mTVTitle = null;
	private SimpleCursorAdapter mDirectionAdapter = null;
	private Spinner mDirectionsSpinner = null;

	private String routeTag;

	/*
	 * private StopsAdapter mStopsAdapter = null; private Spinner mStopsSpinner
	 * = null;
	 * 
	 * private Route mRoute = null; private List<Direction> directions = null;
	 * private List<Stop> stops = null;
	 */
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			intent.getStringExtra("routeTag");

			String action = intent.getAction();
			MyLogger.log(action);
			if (action.equals(C.BROADCAST_ROUTES_SINGLE_UPDATED_ACTION)) {

			}
			else if (action.equals(C.BROADCAST_DIRECTIONS_UPDATED_ACTION)) {
				String[] projection = new String[] { Direction.KEY_ID, Direction.KEY_TITLE, Direction.KEY_NAME };
				String condition = "(" + Direction.KEY_ROUTE__TAG + "='" + routeTag + "')AND(" + Direction.KEY_USEFORUI
						+ "='true')";
				Cursor c = getContentResolver().query(C.CONTENT_URI_DIRECTION, projection, condition, null, null);
				mDirectionAdapter.swapCursor(c);
			}
			else if (action.equals(C.BROADCAST_STOPS_UPDATED_ACTION)) {
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		routeTag = i.getStringExtra("routeTag");
		// mRoute = new Route();

		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
				new IntentFilter(C.BROADCAST_ROUTES_SINGLE_UPDATED_ACTION));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
				new IntentFilter(C.BROADCAST_DIRECTIONS_UPDATED_ACTION));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
				new IntentFilter(C.BROADCAST_STOPS_UPDATED_ACTION));

		Uri contentUri = Uri.withAppendedPath(C.CONTENT_URI_ROUTE, routeTag);
		Cursor cc = getContentResolver().query(contentUri, new String[] { "_id", "tag", "title" }, null, null, null);
		cc.moveToFirst();
		String routeTitle = cc.getString(2);
		cc.close();

		setContentView(R.layout.activity_route_detail);
		mTVTitle = (TextView) findViewById(R.id.labelRoute);
		mTVTitle.setText(routeTitle);

		mDirectionsSpinner = (Spinner) findViewById(R.id.spinnerDirection);

		String[] projection = new String[] { Direction.KEY_ID, Direction.KEY_TITLE, Direction.KEY_NAME };
		String condition = "(" + Direction.KEY_ROUTE__TAG + "='" + routeTag + "')AND(" + Direction.KEY_USEFORUI
				+ "='true')";
		Cursor cursor = getContentResolver().query(C.CONTENT_URI_DIRECTION, projection, condition, null, null);

		this.mDirectionAdapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.spinner_item_layout_directions, cursor,
				new String[] { Direction.KEY_NAME, Direction.KEY_TITLE }, new int[] { R.id.spinner_item1,
						R.id.spinner_item2 }, 0);
		this.mDirectionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_directions);
		this.mDirectionsSpinner.setAdapter(mDirectionAdapter);
		
		mDirectionsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_detail, menu);
		return true;
	}

}

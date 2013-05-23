package com.thuptencho.torontotransitbus.routes;

import android.app.Activity;
import android.content.*;
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
import com.thuptencho.torontotransitbus.backgroundservice.DatabaseUpdatingService;
import com.thuptencho.torontotransitbus.backgroundservice.RestClient;
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

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		int position = i.getIntExtra("position", 999999);

		mRoute = new Route();
		mRoute.mTag = i.getStringExtra("routeTag");
		mRoute.mTitle = i.getStringExtra("routeTitle");

		String[] projection = new String[] { Direction.KEY_ID, Direction.KEY_TAG, Direction.KEY_TITLE,
				Direction.KEY_NAME };
		String condition = "(" + Direction.KEY_ROUTE__TAG + "=" + mRoute.mTag + ")AND(" + Direction.KEY_USEFORUI
				+ "=\"true\"";
		Cursor c = getContentResolver().query(C.CONTENT_URI_DIRECTION, projection, condition, null, null);
		if (c != null) 
			c.moveToFirst();
		
		//String routeTag = c.getString(0);

//		Intent intent = new Intent(this, DatabaseUpdatingService.class);
//		intent.putExtra(C.URL_STRING_KEY, RestClient.getRestUrlForRouteDetail(routeTag));
//		startService(intent);
		//
		// IntentFilter ifilter = new
		// IntentFilter(C.BROADCAST_ROUTES_SINGLE_UPDATED_ACTION);
		// LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
		// ifilter);
		//
		setContentView(R.layout.activity_route_detail);
		mTVTitle = (TextView) findViewById(R.id.labelRoute);
		mTVTitle.setText(mRoute.mTitle);
		//
		// mDirectionSpinner = (Spinner) findViewById(R.id.spinnerDirection);
		// Cursor cursor = null;
		// this.mAdapter = new DirectionsAdapter(getApplicationContext(),
		// R.layout.route_detail_direction_view, cursor,
		// new String[] { Direction.KEY_TITLE }, new int[] {
		// R.id.direction_text_view }, 0);
		// this.mDirectionSpinner.setAdapter(mAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_detail, menu);
		return true;
	}

}

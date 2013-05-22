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
import com.thuptencho.torontotransitbus.backgroundservice.DatabaseUpdatingService;
import com.thuptencho.torontotransitbus.backgroundservice.RestClient;
import com.thuptencho.torontotransitbus.models.Direction;
import com.thuptencho.torontotransitbus.models.Route;

public class RouteDetailActivity extends Activity {
	private TextView tvTitle;
	private DirectionsAdapter dAdapter;
	private Spinner directionSpinner;

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Cursor cursor = null, cursor1 = null;
			try {
				cursor = context.getContentResolver().query(C.CONTENT_URI_ROUTE,
						new String[] { Route.KEY_ID, Route.KEY_TITLE, Route.KEY_TAG },
						Route.KEY_TAG + "='" + intent.getStringExtra(Route.KEY_TAG) + "'", null, null);
				int indexRouteTitle = cursor.getColumnIndex(Route.KEY_TITLE);
				int indexRouteTag = cursor.getColumnIndex(Route.KEY_TAG);
				cursor.moveToFirst();
				String route_tag = cursor.getString(indexRouteTag);
				String route_title = cursor.getString(indexRouteTitle);
				tvTitle.setText(route_title);

				cursor1 = context.getContentResolver().query(C.CONTENT_URI_DIRECTION,
						new String[]{Direction.KEY_ID, Direction.KEY_TAG, Direction.KEY_TITLE}, 
						Direction.KEY_ROUTE__TAG + "='" + route_tag + "'" ,
						null, null);
				
				dAdapter.swapCursor(cursor1);
				Log.i("broadcast", route_title);
			} finally {
				cursor.close();
			}
			// todo use content resolver to gte data
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		int position = i.getIntExtra("position", 9999);
		Uri contentUriRouteSingle = ContentUris.withAppendedId(C.CONTENT_URI_ROUTE, position);
		Cursor c = getContentResolver().query(contentUriRouteSingle, new String[] { Route.KEY_TAG }, null, null, null);
		c.moveToFirst();
		String routeTag = c.getString(0);

		Intent intent = new Intent(this, DatabaseUpdatingService.class);
		intent.putExtra(C.URL_STRING_KEY, RestClient.getRestUrlForRouteDetail(routeTag));
		startService(intent);

		IntentFilter ifilter = new IntentFilter(C.BROADCAST_ROUTES_UPDATED_ACTION);
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, ifilter);

		setContentView(R.layout.activity_route_detail);
		this.tvTitle = (TextView) findViewById(R.id.labelRoute);
		this.directionSpinner = (Spinner) findViewById(R.id.spinnerDirection);
		Cursor cursor = null;
		this.dAdapter = new DirectionsAdapter(getApplicationContext(), R.layout.route_detail_direction_view, cursor,
				new String[] { Direction.KEY_TITLE }, new int[] { R.id.direction_text_view }, 0);
		this.directionSpinner.setAdapter(dAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_detail, menu);
		return true;
	}

}

package com.thuptencho.torontotransitbus.routes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.xmlpull.v1.XmlPullParserException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.thuptencho.torontotransitbus.C;
import com.thuptencho.torontotransitbus.R;
import com.thuptencho.torontotransitbus.backgroundservice.RestClient;
import com.thuptencho.torontotransitbus.models.Direction;
import com.thuptencho.torontotransitbus.models.Prediction;
import com.thuptencho.torontotransitbus.models.Stop;
import com.thuptencho.torontotransitbus.utilities.MyLogger;

public class RouteDetailFragmentActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	private TextView mTVTitle = null;

	private SimpleCursorAdapter mDirectionAdapter = null;

	private Spinner mDirectionsSpinner = null;

	private Button mBtnGo = null;

	private TableRow resultRow = null;

	private String mRouteTag;

	private String mStopTag = "";

	private SimpleCursorAdapter mStopsAdapter = null;

	private Spinner mStopsSpinner = null;

	private TextView tv = null;

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			intent.getStringExtra("routeTag");

			String action = intent.getAction();
			MyLogger.log(action);
			if (action.equals(C.Broadcast.ROUTES_SINGLE_UPDATED_ACTION)) {

			}
			else if (action.equals(C.Broadcast.DIRECTIONS_UPDATED_ACTION)) {
				String[] projection = new String[] { Direction.KEY_ID, Direction.KEY_TITLE, Direction.KEY_NAME };
				String condition = "(" + Direction.KEY_ROUTE__TAG + "='" + mRouteTag + "')AND("
								+ Direction.KEY_USEFORUI + "='true')";
				Cursor c = getContentResolver().query(C.ContentUri.DIRECTION, projection, condition, null, null);
				mDirectionAdapter.swapCursor(c);
			}
			else if (action.equals(C.Broadcast.D_STOPS_UPDATED_ACTION)) {

			}
		}
	};

	@Override
	protected void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		super.onPause();
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_detail);
		Intent i = getIntent();
		mRouteTag = i.getStringExtra("routeTag");
		// mRoute = new Route();

		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
						new IntentFilter(C.Broadcast.ROUTES_SINGLE_UPDATED_ACTION));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
						new IntentFilter(C.Broadcast.DIRECTIONS_UPDATED_ACTION));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
						new IntentFilter(C.Broadcast.D_STOPS_UPDATED_ACTION));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
						new IntentFilter(C.Broadcast.R_STOPS_UPDATED_ACTION));

		getSupportLoaderManager().initLoader(1, null, this);

		getSupportLoaderManager().initLoader(2, null, this);

		mDirectionsSpinner = (Spinner) findViewById(R.id.spinnerDirections);

		mDirectionAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.spinner_layout1, null,
						new String[] { Direction.KEY_NAME, Direction.KEY_TITLE }, new int[] { R.id.spinner_item1,
										R.id.spinner_item2 }, 0);
		mDirectionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_directions);
		mDirectionsSpinner.setAdapter(mDirectionAdapter);
		mDirectionsSpinner.setOnItemSelectedListener(listener);

		mStopsSpinner = (Spinner) findViewById(R.id.spinnerStops);
		mStopsAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.spinner_layout1, null, new String[] {
						Stop.KEY_TAG, Stop.KEY_TITLE }, new int[] { R.id.spinner_item1, R.id.spinner_item2 }, 0);
		mStopsAdapter.setDropDownViewResource(R.layout.spinner_dropdownlayout_stops);
		mStopsSpinner.setAdapter(mStopsAdapter);
		mStopsSpinner.setOnItemSelectedListener(listener);

		mBtnGo = (Button) findViewById(R.id.btnSubmit);

		mBtnGo.setOnClickListener(btnListener);

		resultRow = (TableRow) findViewById(R.id.table_row_result);

	}

	android.view.View.OnClickListener btnListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!mRouteTag.isEmpty() && !mStopTag.isEmpty()) {
				new PredictionsAsyncTask() {
					@Override
					protected void onPostExecute(List<Prediction> predictions) {
						for (Prediction p : predictions) {
							TextView tv = new TextView(getBaseContext());
							tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
							tv.setText(p.minutes + " minutes");
							resultRow.addView(tv);
						}
					}
				}.execute(new String[] { mRouteTag, mStopTag });
			}

		}
	};

	private OnItemSelectedListener listener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			switch (parent.getId()) {
			case R.id.spinnerDirections:
				Cursor dCursor = getContentResolver().query(C.ContentUri.DIRECTION, new String[] { Direction.KEY_TAG },
								"_id=" + id, null, null);
				dCursor.moveToFirst();
				String dTag = dCursor.getString(0);

				Cursor stopsCursor = getContentResolver().query(
								C.ContentUri.STOP,
								new String[] { "Stops." + Stop.KEY_ID, "Stops." + Stop.KEY_TAG,
												"Stops." + Stop.KEY_TITLE },
								"Directions_Stops." + Stop.KEY_DIRECTION__TAG + "='" + dTag + "'", null, null);
				mStopsAdapter.swapCursor(stopsCursor);
				mStopsAdapter.notifyDataSetChanged();
				break;
			case R.id.spinnerStops:
				Cursor stopCursor = getContentResolver().query(
								C.ContentUri.STOP,
								new String[] { C.Db.TABLE_STOPS + "." + Stop.KEY_TITLE,
												C.Db.TABLE_STOPS + "." + Stop.KEY_TAG }, "Stops._id=" + id, null, null);
				stopCursor.moveToFirst();
				mStopTag = stopCursor.getString(stopCursor.getColumnIndex(Stop.KEY_TAG));

				MyLogger.log("Stop Selected v");
				MyLogger.log(stopCursor);
				break;
			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch (loader.getId()) {
		case 1:
			cursor.moveToFirst();
			String routeTitle = cursor.getString(2);
			mTVTitle = (TextView) findViewById(R.id.labelRoute);
			mTVTitle.setText(routeTitle);
			break;
		case 2:
			this.mDirectionAdapter.swapCursor(cursor);
			break;
		default:
			break;
		}

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
		case 1:
			Loader<Cursor> cursorLoader1 = new CursorLoader(getApplicationContext(), Uri.withAppendedPath(
							C.ContentUri.ROUTE, mRouteTag), new String[] { "_id", "tag", "title" }, null, null, null);
			return cursorLoader1;
		case 2:
			String[] projection = new String[] { Direction.KEY_ID, Direction.KEY_TITLE, Direction.KEY_NAME };
			String condition = "(" + Direction.KEY_ROUTE__TAG + "='" + mRouteTag + "')AND(" + Direction.KEY_USEFORUI
							+ "='true')";
			Loader<Cursor> cursorLoader2 = new CursorLoader(getApplicationContext(), C.ContentUri.DIRECTION,
							projection, condition, null, null);
			return cursorLoader2;
		default:
			break;
		}

		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_detail, menu);
		return true;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub

	}

	private class PredictionsAsyncTask extends AsyncTask<String, Integer, List<Prediction>> {

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected List<Prediction> doInBackground(String... params) {
			String routeTag = params[0];
			String stopTag = params[1];
			List<Prediction> predictions = new ArrayList<Prediction>();
			try {
				predictions = RestClient.getPredictions(routeTag, stopTag);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
			return predictions;
		}

	}
}

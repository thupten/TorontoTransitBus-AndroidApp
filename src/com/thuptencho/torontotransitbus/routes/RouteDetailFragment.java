package com.thuptencho.torontotransitbus.routes;

import com.thuptencho.torontotransitbus.C;
import com.thuptencho.torontotransitbus.R;
import com.thuptencho.torontotransitbus.models.Direction;
import com.thuptencho.torontotransitbus.utilities.MyLogger;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class RouteDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private TextView mTVTitle = null;
	private SimpleCursorAdapter mDirectionAdapter = null;
	private Spinner mDirectionsSpinner = null;

	private String routeTag;
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getActivity().getIntent();
		routeTag = i.getStringExtra("routeTag");
		// mRoute = new Route();

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
				new IntentFilter(C.BROADCAST_ROUTES_SINGLE_UPDATED_ACTION));
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
				new IntentFilter(C.BROADCAST_DIRECTIONS_UPDATED_ACTION));
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
				new IntentFilter(C.BROADCAST_STOPS_UPDATED_ACTION));

		getLoaderManager().initLoader(1, null, this);

		getLoaderManager().initLoader(2, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.activity_route_detail, container);
		mTVTitle = (TextView) view.findViewById(R.id.labelRoute);
		mDirectionsSpinner = (Spinner) view.findViewById(R.id.spinnerDirection);
		

		this.mDirectionAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.spinner_item_layout_directions, null,
				new String[] { Direction.KEY_NAME, Direction.KEY_TITLE }, new int[] { R.id.spinner_item1,
						R.id.spinner_item2 }, 0);
		this.mDirectionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_directions);
		this.mDirectionsSpinner.setAdapter(mDirectionAdapter);

		mDirectionsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				MyLogger.log("id: " + id);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});

		return view;
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
		case 1:
			Loader<Cursor> cursorLoader1 = new CursorLoader(getActivity(), Uri.withAppendedPath(
					C.CONTENT_URI_ROUTE, routeTag), new String[] { "_id", "tag", "title" }, null, null, null);
			return cursorLoader1;
		case 2:
			String[] projection = new String[] { Direction.KEY_ID, Direction.KEY_TITLE, Direction.KEY_NAME };
			String condition = "(" + Direction.KEY_ROUTE__TAG + "='" + routeTag + "')AND(" + Direction.KEY_USEFORUI
					+ "='true')";
			Loader<Cursor> cursorLoader2 = new CursorLoader(getActivity(), C.CONTENT_URI_DIRECTION,
					projection, condition, null, null);
			return cursorLoader2;
		default:
			break;
		}

		return null;

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch (loader.getId()) {
		case 1:
			cursor.moveToFirst();
			String routeTitle = cursor.getString(2);
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
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}
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
				Cursor c = getActivity().getContentResolver().query(C.CONTENT_URI_DIRECTION, projection, condition, null, null);
				mDirectionAdapter.swapCursor(c);
			}
			else if (action.equals(C.BROADCAST_STOPS_UPDATED_ACTION)) {

			}
		}
	};
}

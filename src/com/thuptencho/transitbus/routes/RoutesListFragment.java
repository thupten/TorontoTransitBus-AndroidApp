package com.thuptencho.transitbus.routes;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.thuptencho.transitbus.R;
import com.thuptencho.transitbus.C;
import com.thuptencho.transitbus.models.Route;
import com.thuptencho.transitbus.utilities.MyLogger;

public class RoutesListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	// private RoutesAdapter adapter;
	private RoutesAdapter mAdapter;
	private ArrayList<Route> mRoutes;
	private ProgressDialog progressDialog;
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			mRoutes.clear();
			ArrayList<Parcelable> routes = intent.getParcelableArrayListExtra("routes");
			for (Parcelable r : routes) {
				Route rr = (Route) r;
				mRoutes.add(rr);
			}
			mAdapter.notifyDataSetChanged();
		}
	};

	@Override
	public void onStop() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
		super.onStop();
	
	};
	
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRoutes = new ArrayList<Route>();
		mAdapter = new RoutesAdapter(getActivity(), mRoutes);
		setListAdapter(mAdapter);
		getActivity().getSupportLoaderManager().initLoader(1, null, this);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
				new IntentFilter(C.Broadcast.ROUTES_UPDATED_ACTION));
		progressDialog = new ProgressDialog(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// we have the cursor of the routes from db use this to update the array
		mRoutes.clear();
		if (cursor.getCount() == 0) {
			MyLogger.log("show loading route list...");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
		while (cursor.moveToNext()) {
			Route r = new Route();
			// r._id = cursor.getInt(cursor.getColumnIndex(Route.KEY_ID));
			r.mTitle = cursor.getString(cursor.getColumnIndex(Route.KEY_TITLE));
			r.mTag = cursor.getString(cursor.getColumnIndex(Route.KEY_TAG));
			mRoutes.add(r);
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg) {
		Loader<Cursor> loader = new CursorLoader(getActivity(), C.ContentUri.ROUTE, new String[] { Route.KEY_TAG,
				Route.KEY_TITLE }, null, null, null);
		return loader;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.routes_fragment_layout, container, false);
		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		//TODO if first item 1-s yonge is selected app will crash. fix that.
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(getActivity(), RouteDetailFragmentActivity.class);
		Route r = (Route) getListAdapter().getItem(position);
		intent.putExtra("routeTag", r.mTag);
		startActivity(intent);
		Toast.makeText(getActivity(), "clicked " + position, Toast.LENGTH_SHORT).show();
	}

}

package com.thuptencho.torontotransitbus.routes;

import java.util.ArrayList;

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
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.thuptencho.torontotransitbus.C;
import com.thuptencho.torontotransitbus.models.Route;

public class RoutesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	// private RoutesAdapter adapter;
	private RoutesAdapter mAdapter;
	private ArrayList<Route> mRoutes;
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRoutes = new ArrayList<Route>();
		mAdapter = new RoutesAdapter(getActivity(), mRoutes);
		setListAdapter(mAdapter);
		getActivity().getSupportLoaderManager().initLoader(1, null, this);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
				new IntentFilter(C.BROADCAST_ROUTES_UPDATED_ACTION));
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// we have the cursor of the routes from db use this to update the array
		mRoutes.clear();
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
		Loader<Cursor> loader = new CursorLoader(getActivity(), C.CONTENT_URI_ROUTE, new String[] { Route.KEY_TAG,
				Route.KEY_TITLE }, null, null, null);
		return loader;

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Toast.makeText(getActivity(), "clicked " + position, Toast.LENGTH_SHORT).show();
		Intent i = new Intent(getActivity(), RouteDetailActivity.class);
		Route item = (Route) getListAdapter().getItem(position);
		Log.println(Log.ASSERT, "onListItemClick", item.toString());
		i.putExtra("routeTag", item.mTag);
		startActivity(i);
	}

}

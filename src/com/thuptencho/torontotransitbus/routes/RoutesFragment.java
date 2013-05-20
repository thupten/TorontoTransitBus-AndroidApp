package com.thuptencho.torontotransitbus.routes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.thuptencho.torontotransitbus.Constants;
import com.thuptencho.torontotransitbus.R;
import com.thuptencho.torontotransitbus.backgroundservice.DatabaseUpdatingService;
import com.thuptencho.torontotransitbus.backgroundservice.RestClient;
import com.thuptencho.torontotransitbus.models.Route;

public class RoutesFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	// private RoutesAdapter adapter;
	private SimpleCursorAdapter mAdapter;
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Uri uri = Constants.CONTENT_URI_ROUTE;
			String[] projection = new String[] { Route.KEY_ID, Route.KEY_TITLE };
			Cursor cursor = context.getContentResolver().query(uri, projection,
					null, null, null);
			mAdapter.changeCursor(cursor);
			mAdapter.notifyDataSetChanged();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.route_list_view, null,
				new String[] { Route.KEY_TITLE },
				new int[] { R.id.route_text_view0 }, 0);
		setListAdapter(mAdapter);
		
		getActivity().getSupportLoaderManager().initLoader(1, null, this);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				mMessageReceiver,
				new IntentFilter(Constants.BROADCAST_ROUTE_LIST_UPDATED));
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		this.mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		this.mAdapter.swapCursor(null);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg) {
		Loader<Cursor> loader = new CursorLoader(getActivity(), Constants.CONTENT_URI_ROUTE,
				new String[] { Route.KEY_ID, Route.KEY_TITLE }, null, null,
				null);
		Intent intent = new Intent(getActivity(), DatabaseUpdatingService.class);
		intent.putExtra(Constants.URL_STRING_KEY, RestClient.getRestUrlForRoutelist());
		getActivity().startService(intent);
		return loader;

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Toast.makeText(getActivity(), "clicked " + position, Toast.LENGTH_SHORT)
				.show();
		Intent i = new Intent(getActivity(), RouteDetailActivity.class);
		i.putExtra("position", position);
		startActivity(i);
	}

}

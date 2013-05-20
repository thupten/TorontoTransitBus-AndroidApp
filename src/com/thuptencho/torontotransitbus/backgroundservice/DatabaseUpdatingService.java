package com.thuptencho.torontotransitbus.backgroundservice;

import java.io.IOException;
import java.util.List;

import com.thuptencho.torontotransitbus.C;
import org.apache.http.client.ClientProtocolException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.thuptencho.torontotransitbus.models.Direction;
import com.thuptencho.torontotransitbus.models.Pathz;
import com.thuptencho.torontotransitbus.models.Pointz;
import com.thuptencho.torontotransitbus.models.Route;
import com.thuptencho.torontotransitbus.models.Stop;
import com.thuptencho.torontotransitbus.utilities.LogCursor;

/*Used by Content Providers*/
public class DatabaseUpdatingService extends IntentService {
	public DatabaseUpdatingService() {
		super("DatabaseUpdatingService");
	}

	public DatabaseUpdatingService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Log.d("DatabaseUpdatingService", "onHandleIntent(Intent intent)");

		try {
			this.loadFromRestAndSaveToDb(intent.getStringExtra("urlString"));
		} catch (ClientProtocolException e) {

			Log.e("onHandle", e.getLocalizedMessage());
		} catch (IOException e) {

			Log.e("onHandle", e.getLocalizedMessage());
		} catch (XmlPullParserException e) {

			Log.e("onHandle", e.getLocalizedMessage());
		}

	}

	public void loadFromRestAndSaveToDb(String url) throws ClientProtocolException, IOException, XmlPullParserException {
		// check url and find out which rest method it matches.
		if (RestClient.isRestUrlForRoutelist(url)) {
			List<Route> routes = RestClient.getRoutes();
			String[] colNames = new String[] { Route.KEY_TAG, Route.KEY_TITLE, Route.KEY_AGENCY__TAG };
			if (routes.size() > 0) {
				for (Route r : routes) {
					String[] rowValues = new String[] { r.getTag(), r.getTitle(), r.getAgency__Tag() };
					String conditiona = colNames[0] + "='" + rowValues[0] + "'";
					saveToDB(colNames, rowValues, conditiona, C.TABLE_ROUTES, C.CONTENT_URI_ROUTE);
				}
				Intent i = new Intent(C.BROADCAST_ROUTE_LIST_UPDATED);
				LocalBroadcastManager.getInstance(this).sendBroadcast(i);
			}
		}

		else if (RestClient.isRestUrlForRouteDetail(url)) {
			/* save Route */
			Route route = new Route();
			route = RestClient.getRouteDetail(url);
			String[] columnNamesRoutes = new String[] { Route.KEY_TAG, Route.KEY_TITLE, Route.KEY_SHORT_TITLE,
					Route.KEY_COLOR, Route.KEY_OPPOSITE_COLOR, Route.KEY_LAT_MAX, Route.KEY_LAT_MIN, Route.KEY_LON_MAX,
					Route.KEY_LON_MIN };

			String[] rowValuesRoutes = new String[] { route.getTag(), route.getTitle(), route.getShortTitle(),
					route.getColor(), route.getOppositeColor(), route.getLatMax(), route.getLatMin(),
					route.getLonMax(), route.getLonMin() };

			String conditionb = columnNamesRoutes[0] + "='" + rowValuesRoutes[0] + "'";
			saveToDB(columnNamesRoutes, rowValuesRoutes, conditionb, C.TABLE_ROUTES,
					C.CONTENT_URI_ROUTE);
			/* save route's Stop */
			List<Stop> stops = route.getStops();
			String[] colNamesStops, rowValuesStops;
			if (stops.size() > 0) {
				colNamesStops = new String[] { Stop.KEY_TAG, Stop.KEY_LAT, Stop.KEY_LON, Stop.KEY_STOPID,
						Stop.KEY_TITLE, Stop.KEY_ROUTE__TAG };
				for (Stop s : stops) {
					rowValuesStops = new String[] { s.getTag(), s.getLat(), s.getLon(), s.getStopId(), s.getTitle(),
							route.getTag() };
					String conditionc = colNamesStops[0] + "='" + rowValuesStops[0] + "'";
					saveToDB(colNamesStops, rowValuesStops, conditionc, C.TABLE_STOPS,
							C.CONTENT_URI_STOP);
				}
			}

			/* save Direction and the Stops in the direction */
			List<Direction> directions = route.getDirections();
			for (Direction d : directions) {
				String[] columnNamesDirections = new String[] { Direction.KEY_TAG, Direction.KEY_TITLE,
						Direction.KEY_NAME, Direction.KEY_USEFORUI, Direction.KEY_ROUTE__TAG };
				String[] rowValuesDirections = new String[] { d.getTag(), d.getTitle(), d.getName(), d.getUseForUI(),
						route.getTag() };
				String conditionz = columnNamesDirections[0] + "='" + rowValuesDirections[0] + "'";
				saveToDB(columnNamesDirections, rowValuesDirections, conditionz, C.TABLE_DIRECTIONS,
						C.CONTENT_URI_DIRECTION);
				List<Stop> stopsInDirection = d.getStops();
				for (Stop sd : stopsInDirection) {
					String conditionx = Stop.KEY_TAG + "='" + sd.getTag() + "'";
					saveToDB(new String[] { Stop.KEY_TAG, Stop.KEY_DIRECTION__TAG },
							new String[] { sd.getTag(), d.getTag() }, conditionx, C.TABLE_STOPS,
							C.CONTENT_URI_STOP);
				}

			}
			/* save path and points */
			List<Pathz> paths = route.getPaths();
			for (Pathz pa : paths) {
				String conditionxe = Pathz.KEY_ROUTE__TAG + "='" + route.getTag() + "'";
				int pathid = saveToDB(new String[] { Pathz.KEY_ROUTE__TAG }, new String[] { route.getTag() },
						conditionxe, C.TABLE_PATHS, C.CONTENT_URI_PATH);
				for (Pointz po : pa.getPoints()) {
					// lat,lon,path__id
					String conditionee = Pointz.KEY_PATHS__ID + "=" + pathid;
					saveToDB(new String[] { Pointz.KEY_LAT, Pointz.KEY_LON, Pointz.KEY_PATHS__ID },
							new String[] { po.getLat(), po.getLon(), String.valueOf(pathid) },
							conditionee, C.TABLE_POINTS,
							C.CONTENT_URI_POINT);
				}
			}
			Intent i = new Intent(C.BROADCAST_ROUTE_DETAIL_UPDATED);
			i.putExtra(Route.KEY_TAG, route.getTag());
			LocalBroadcastManager.getInstance(this).sendBroadcast(i);

		}
		else if (RestClient.isRestUrlForSchedule(url)) {

		}
		else if (RestClient.isRestUrlForMessageForRoutes(url)) {

		}
		else if (RestClient.isRestUrlForPredictionswithStopArgument(url)) {

		}
		else if (RestClient.isRestUrlForPredictionswithStopAndRoutetagArguments(url)) {

		}
		else if (RestClient.isRestUrlForPredictionsForMultiStops(url)) {

		}

	}

	// returns one of these
	// id of the inserted or updated row if single row.
	// -1 if multiple rows affected
	private int saveToDB(String[] columnNames, String[] rowValues, String condition, String tableName, Uri contentUri) {
		ContentValues values = new ContentValues();
		for (int i = 0; i < columnNames.length; i++) {
			String key = columnNames[i];
			String value = rowValues[i];
			if (value != null) {
				values.put(key, value);
			}
		}
		// if record exist update or else
		// insert in table with tagname eg route, values from attributesNames
		// and values
		int resultid = -1;
		Uri contenturi = contentUri;
		String[] projection = { "_id" };

		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(contenturi, projection, condition, null, null);
		try {
			if (cursor.getCount() > 0) {
				LogCursor.d("dump", cursor);
				int rowsAffected = cr.update(contenturi, values, condition, null);
				if (rowsAffected == 1) {
					cursor = cr.query(contenturi, new String[] { "_id" }, condition, null, null);
					if (cursor != null) {
						cursor.moveToFirst();
						resultid = cursor.getInt(0);
					}
				}
				cr.notifyChange(contenturi, null);
				Log.i("DatabaseUpdatingService", String.valueOf(rowsAffected));
			}
			else {
				Uri insertedUri = cr.insert(contenturi, values);
				String insertedId = insertedUri.getPathSegments().get(1);
				resultid = Integer.parseInt(insertedId);
				cr.notifyChange(insertedUri, null);
				Log.i("DatabaseUpdatingService", insertedUri.toString());
			}
		} finally {
			cursor.close();
		}
		return resultid;
	}

}

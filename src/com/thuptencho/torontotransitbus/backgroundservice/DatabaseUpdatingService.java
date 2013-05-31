package com.thuptencho.torontotransitbus.backgroundservice;

import java.io.IOException;
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.thuptencho.torontotransitbus.C;
import com.thuptencho.torontotransitbus.models.Direction;
import com.thuptencho.torontotransitbus.models.Pathz;
import com.thuptencho.torontotransitbus.models.Pointz;
import com.thuptencho.torontotransitbus.models.Route;
import com.thuptencho.torontotransitbus.models.Stop;
import com.thuptencho.torontotransitbus.provider.MySQLiteOpenHelper;

/*Used by Content Providers*/
public class DatabaseUpdatingService extends IntentService {
	private SQLiteDatabase db;
	private static UriMatcher uriMatcher;

	private static final int ROUTES = 10, ROUTES_SINGLE = 11, DIRECTIONS = 20, DIRECTIONS_SINGLE = 21, STOPS = 30,
			STOPS_SINGLE = 31, PATHS = 40, PATHS_SINGLE = 41, POINTS = 50, POINTS_SINGLE = 51, SCHEDULES = 60,
			SCHEDULES_SINGLE = 61;
	private MySQLiteOpenHelper mySQLiteOpenHelper = null;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(C.ContentUri.AUTHORITY, "routes", ROUTES);
		uriMatcher.addURI(C.ContentUri.AUTHORITY, "routes/#", ROUTES_SINGLE);
		uriMatcher.addURI(C.ContentUri.AUTHORITY, "directions", DIRECTIONS);
		uriMatcher.addURI(C.ContentUri.AUTHORITY, "directions/#", DIRECTIONS_SINGLE);
		uriMatcher.addURI(C.ContentUri.AUTHORITY, "stops", STOPS);
		uriMatcher.addURI(C.ContentUri.AUTHORITY, "stops/#", STOPS_SINGLE);
		uriMatcher.addURI(C.ContentUri.AUTHORITY, "paths", PATHS);
		uriMatcher.addURI(C.ContentUri.AUTHORITY, "paths/#", PATHS_SINGLE);
		uriMatcher.addURI(C.ContentUri.AUTHORITY, "points", POINTS);
		uriMatcher.addURI(C.ContentUri.AUTHORITY, "points/#", POINTS_SINGLE);
		uriMatcher.addURI(C.ContentUri.AUTHORITY, "points", SCHEDULES);
		uriMatcher.addURI(C.ContentUri.AUTHORITY, "points/#", SCHEDULES_SINGLE);
	}

	public DatabaseUpdatingService() {
		super("DatabaseUpdatingService");
	}

	public DatabaseUpdatingService(String name) {
		super(name);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mySQLiteOpenHelper = new MySQLiteOpenHelper(getApplicationContext(), C.Db.DATABASE_NAME, null, C.Db.DATABASE_VERSION);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			String extraQuery = intent.getStringExtra("task");
			if (extraQuery.equals("query")) {
				try {
					String extraUri_string = intent.getStringExtra("uri_string");
					db = mySQLiteOpenHelper.getWritableDatabase();
					this.loadFromRestAndSaveToDb(extraUri_string);
				} finally {
					db.close();
				}
			}

		} catch (ClientProtocolException e) {

			Log.e("onHandle", e.getLocalizedMessage());
		} catch (IOException e) {

			Log.e("onHandle", e.getLocalizedMessage());
		} catch (XmlPullParserException e) {

			Log.e("onHandle", e.getLocalizedMessage());
		} catch (SQLDataException e) {
			Log.e("onHandle", e.getMessage());
			;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void loadFromRestAndSaveToDb(String uriString) throws Exception {
		Uri uri = Uri.parse(uriString);
		int match = uriMatcher.match(uri);

		switch (match) {
		case ROUTES:
			ArrayList<Route> routes = (ArrayList<Route>) RestClient.getRoutes();
			if (routes.size() > 0) {
				ContentValues contentValues = new ContentValues();
				for (Route r : routes) {
					contentValues.put(Route.KEY_TAG, r.mTag);
					contentValues.put(Route.KEY_TITLE, r.mTitle);
					contentValues.put(Route.KEY_AGENCY__TAG, r.mAgencyTag);

					int update = db.update(C.Db.TABLE_ROUTES, contentValues, "tag='" + r.mTag + "'", null);
					if (update == 0) {
						db.insert(C.Db.TABLE_ROUTES, null, contentValues);
					}

				}
				Intent i = new Intent(C.Broadcast.ROUTES_UPDATED_ACTION);
				i.putParcelableArrayListExtra("routes", routes);
				LocalBroadcastManager.getInstance(this).sendBroadcast(i);
			}
			break;
		case ROUTES_SINGLE:
			// get the id
			String routeTag = uri.getPathSegments().get(1);

			Route route = new Route();
			route = RestClient.getRouteDetail(routeTag);
			ContentValues contentValuesRoute = new ContentValues();
			contentValuesRoute.put(Route.KEY_TAG, route.mTag);
			contentValuesRoute.put(Route.KEY_TITLE, route.mTitle);
			contentValuesRoute.put(Route.KEY_SHORT_TITLE, route.mShortTitle);
			contentValuesRoute.put(Route.KEY_COLOR, route.nColor);
			contentValuesRoute.put(Route.KEY_OPPOSITE_COLOR, route.mOppositeColor);
			contentValuesRoute.put(Route.KEY_LAT_MAX, route.mLatMax);
			contentValuesRoute.put(Route.KEY_LAT_MIN, route.mLatMin);
			contentValuesRoute.put(Route.KEY_LON_MAX, route.mLonMax);
			contentValuesRoute.put(Route.KEY_LON_MIN, route.mLonMin);
			contentValuesRoute.put(Route.KEY_AGENCY__TAG, C.AGENCY);

			int update = db.update(C.Db.TABLE_ROUTES, contentValuesRoute, "tag=?", new String[] { routeTag });
			if (update == 0) {
				db.insert(C.Db.TABLE_ROUTES, null, contentValuesRoute);
			}

			Intent messageIntent = new Intent();
			messageIntent = new Intent(C.Broadcast.ROUTES_SINGLE_UPDATED_ACTION);
			messageIntent.putExtra("route_tag", route.mTag);
			LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

			/* save route's Stop */
			List<Stop> stops = route.mStops;
			if (stops.size() > 0) {
				for (Stop s : stops) {
					// save the Stop in Stop table.
					ContentValues contentValuesStop = new ContentValues();
					contentValuesStop.put(Stop.KEY_TAG, s.mTag);
					contentValuesStop.put(Stop.KEY_LAT, s.mLat);
					contentValuesStop.put(Stop.KEY_LON, s.mLon);
					contentValuesStop.put(Stop.KEY_STOPID, s.mStopId);
					contentValuesStop.put(Stop.KEY_TITLE, s.mTitle);

					// int update1 = db.update(C.TABLE_STOPS, contentValuesStop,
					// "tag=?", new String[] { s.mTag });
					int update1 = db.update(C.Db.TABLE_STOPS, contentValuesStop, "tag='" + s.mTag + "'", null);
					if (update1 == 0) {
						db.insert(C.Db.TABLE_STOPS, null, contentValuesStop);
					}

					// entry in routes_stops table.
					ContentValues contentValuesRouteStop = new ContentValues();
					contentValuesRouteStop.put("Route__tag", route.mTag);
					contentValuesRouteStop.put("Stop__tag", s.mTag);
					int update2 = db.update(C.Db.TABLE_ROUTES_STOPS, contentValuesRouteStop,
							"(Route__tag=?) AND (Stop__tag=?)", new String[] { route.mTag, s.mTag });
					if (update2 == 0) {
						db.insert(C.Db.TABLE_ROUTES_STOPS, null, contentValuesRouteStop);
					}

				}
				messageIntent = new Intent(C.Broadcast.R_STOPS_UPDATED_ACTION);
				messageIntent.putExtra("route_tag", route.mTag);
				LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
			}

			/* save Direction and the Stops in the direction */
			List<Direction> directions = route.mDirections;

			for (Direction d : directions) {
				ContentValues contentValuesDirection = new ContentValues();
				contentValuesDirection.put(Direction.KEY_TAG, d.mTag);
				contentValuesDirection.put(Direction.KEY_TITLE, d.mTitle);
				contentValuesDirection.put(Direction.KEY_NAME, d.mName);
				contentValuesDirection.put(Direction.KEY_USEFORUI, d.mUseForUI);
				contentValuesDirection.put(Direction.KEY_ROUTE__TAG, route.mTag);
				int update3 = db.update(C.Db.TABLE_DIRECTIONS, contentValuesDirection, "(tag=?) AND (route__tag=?)",
						new String[] { d.mTag, route.mTag });
				if (update3 == 0) {
					db.insert(C.Db.TABLE_DIRECTIONS, null, contentValuesDirection);
				}

				List<Stop> stopsInDirection = d.mStops;

				for (Stop sd : stopsInDirection) {
					ContentValues contentValuesDirectionStop = new ContentValues();
					contentValuesDirectionStop.put("Direction__tag", d.mTag);
					contentValuesDirectionStop.put("Stop__tag", sd.mTag);
					int update4 = db.update(C.Db.TABLE_DIRECTIONS_STOPS, contentValuesDirectionStop,
							"(Direction__tag=?) AND (Stop__tag=?)", new String[] { d.mTag, sd.mTag });
					if (update4 == 0) {
						db.insert(C.Db.TABLE_DIRECTIONS_STOPS, null, contentValuesDirectionStop);
					}
				}
				messageIntent = new Intent(C.Broadcast.D_STOPS_UPDATED_ACTION);
				messageIntent.putExtra("route_tag", route.mTag);
				messageIntent.putExtra("direction_tag", d.mTag);
				LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

			}
			messageIntent = new Intent(C.Broadcast.DIRECTIONS_UPDATED_ACTION);
			messageIntent.putExtra("route_tag", route.mTag);
			LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

			/* save path and points */
			List<Pathz> paths = route.mPaths;
			for (Pathz pa : paths) {
				ContentValues contentValuesPath = new ContentValues();
				contentValuesPath.put(Pathz.KEY_ROUTE__TAG, route.mTag);
				// if the path and get its id, if not found insert new.
				long pathid = -1;
				Cursor cursor = db.query(C.Db.TABLE_PATHS, new String[] { "_id" }, "route__tag='" + route.mTag + "'",
						null, null, null, null);
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					pathid = (long) cursor.getInt(0);
				}
				cursor.close();

				if (pathid == -1) {
					pathid = db.insert(C.Db.TABLE_PATHS, null, contentValuesPath);
				}

				for (Pointz po : pa.mPoints) {
					// lat,lon,path__id
					ContentValues contentValuesPoint = new ContentValues();
					contentValuesPoint.put(Pointz.KEY_LAT, po.mLat);
					contentValuesPoint.put(Pointz.KEY_LON, po.mLon);
					contentValuesPoint.put(Pointz.KEY_PATHS__ID, pathid);
					int update6 = db.update(C.Db.TABLE_POINTS, contentValuesPoint, "paths__id=?",
							new String[] { String.valueOf(pathid) });
					if (update6 == 0) {
						db.insert(C.Db.TABLE_POINTS, null, contentValuesPoint);
					}
				}
				messageIntent = new Intent(C.Broadcast.POINTS_UPDATED_ACTION);
				messageIntent.putExtra("path_id", pathid);
				LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
			}
			messageIntent = new Intent(C.Broadcast.PATHS_UPDATED_ACTION);
			messageIntent.putExtra("route_key", route.mTag);
			LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
			break;
		case DIRECTIONS:
		case DIRECTIONS_SINGLE:
			//todo
			break;
			
		case STOPS:
		case STOPS_SINGLE:
			//todo
			break;
		default:
			throw new IllegalArgumentException("illegal argument here. did'nt match anythign with " + uri.toString());

		}

	}

}

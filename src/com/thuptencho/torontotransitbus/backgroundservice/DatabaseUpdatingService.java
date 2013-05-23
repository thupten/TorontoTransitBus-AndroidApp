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
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.thuptencho.torontotransitbus.C;
import com.thuptencho.torontotransitbus.models.Direction;
import com.thuptencho.torontotransitbus.models.Pathz;
import com.thuptencho.torontotransitbus.models.Pointz;
import com.thuptencho.torontotransitbus.models.Route;
import com.thuptencho.torontotransitbus.models.Stop;
import com.thuptencho.torontotransitbus.provider.MySQLiteOpenHelper;
import com.thuptencho.torontotransitbus.utilities.LogCursor;

/*Used by Content Providers*/
public class DatabaseUpdatingService extends IntentService {

	private static UriMatcher uriMatcher;

	private static final int ROUTES = 10, ROUTES_SINGLE = 11, DIRECTIONS = 20, DIRECTIONS_SINGLE = 21, STOPS = 30,
			STOPS_SINGLE = 31, PATHS = 40, PATHS_SINGLE = 41, POINTS = 50, POINTS_SINGLE = 51, SCHEDULES = 60,
			SCHEDULES_SINGLE = 61;
	private MySQLiteOpenHelper mySQLiteOpenHelper = null;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(C.AUTHORITY, "routes", ROUTES);
		uriMatcher.addURI(C.AUTHORITY, "routes/#", ROUTES_SINGLE);
		uriMatcher.addURI(C.AUTHORITY, "directions", DIRECTIONS);
		uriMatcher.addURI(C.AUTHORITY, "directions/#", DIRECTIONS_SINGLE);
		uriMatcher.addURI(C.AUTHORITY, "stops", STOPS);
		uriMatcher.addURI(C.AUTHORITY, "stops/#", STOPS_SINGLE);
		uriMatcher.addURI(C.AUTHORITY, "paths", PATHS);
		uriMatcher.addURI(C.AUTHORITY, "paths/#", PATHS_SINGLE);
		uriMatcher.addURI(C.AUTHORITY, "points", POINTS);
		uriMatcher.addURI(C.AUTHORITY, "points/#", POINTS_SINGLE);
		uriMatcher.addURI(C.AUTHORITY, "points", SCHEDULES);
		uriMatcher.addURI(C.AUTHORITY, "points/#", SCHEDULES_SINGLE);
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
		mySQLiteOpenHelper = new MySQLiteOpenHelper(getApplicationContext(), C.DATABASE_NAME, null, C.DATABASE_VERSION);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			String extraQuery = intent.getStringExtra("task");
			if (extraQuery.equals("query")) {
				String extraUri_string = intent.getStringExtra("uri_string");
				this.loadFromRestAndSaveToDb(extraUri_string);
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

	public void loadFromRestAndSaveToDb(String uriString) throws Exception{
		Uri uri = Uri.parse(uriString);
		int match = uriMatcher.match(uri);
		switch (match) {
		case ROUTES:
			ArrayList<Route> routes = (ArrayList<Route>)RestClient.getRoutes();
			if (routes.size() > 0) {
				ContentValues contentValues = new ContentValues();
				for (Route r : routes) {
					contentValues.put(Route.KEY_TAG, r.mTag);
					contentValues.put(Route.KEY_TITLE, r.mTitle);
					contentValues.put(Route.KEY_AGENCY__TAG, r.mAgencyTag);
					saveToDB(contentValues, null, C.TABLE_ROUTES, SQLiteDatabase.CONFLICT_ABORT);
				}
				Intent i = new Intent(C.BROADCAST_ROUTES_UPDATED_ACTION);
				i.putParcelableArrayListExtra("routes", routes);
				LocalBroadcastManager.getInstance(this).sendBroadcast(i);
			}
			break;
		case ROUTES_SINGLE:
			// get the id
			int selectedRouteId = Integer.valueOf(uri.getPathSegments().get(1));
			Intent messageIntent = new Intent();

			Cursor c = findFirstFromDb(new String[] { Route.KEY_TAG }, "_id=" + selectedRouteId, C.TABLE_ROUTES);
			c.moveToFirst();
			String routeTag = c.getString(0);

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

			saveToDB(contentValuesRoute, null, C.TABLE_ROUTES, SQLiteDatabase.CONFLICT_REPLACE);

			messageIntent = new Intent(C.BROADCAST_ROUTES_SINGLE_UPDATED_ACTION);
			messageIntent.putExtra("route_tag", route.mTag);
			LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

			/* save route's Stop */
			List<Stop> stops = route.mStops;
			if (stops.size() > 0) {
				for (Stop s : stops) {
					ContentValues contentValuesStop = new ContentValues();
					contentValuesStop.put(Stop.KEY_TAG, s.mTag);
					contentValuesStop.put(Stop.KEY_LAT, s.mLat);
					contentValuesStop.put(Stop.KEY_LON, s.mLon);
					contentValuesStop.put(Stop.KEY_STOPID, s.mStopId);
					contentValuesStop.put(Stop.KEY_TITLE, s.mTitle);
					contentValuesStop.put(Stop.KEY_TAG, route.mTag);
					saveToDB(contentValuesStop, null, C.TABLE_STOPS, SQLiteDatabase.CONFLICT_NONE);
				}
				messageIntent = new Intent(C.BROADCAST_STOPS_UPDATED_ACTION);
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

				saveToDB(contentValuesDirection, null, C.TABLE_DIRECTIONS, SQLiteDatabase.CONFLICT_REPLACE);
				List<Stop> stopsInDirection = d.mStops;

				for (Stop sd : stopsInDirection) {
					ContentValues contentValuesStop = new ContentValues();
					String conditionx = Stop.KEY_TAG + "='" + sd.mTag + "'";
					contentValuesStop.put(Stop.KEY_TAG, sd.mTag);
					contentValuesStop.put(Stop.KEY_DIRECTION__TAG, d.mTag);
					saveToDB(contentValuesStop, conditionx, C.TABLE_STOPS, SQLiteDatabase.CONFLICT_NONE);
				}
				messageIntent = new Intent(C.BROADCAST_STOPS_UPDATED_ACTION);
				messageIntent.putExtra("route_tag", route.mTag);
				messageIntent.putExtra("direction_tag", d.mTag);
				LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

			}
			messageIntent = new Intent(C.BROADCAST_DIRECTIONS_UPDATED_ACTION);
			messageIntent.putExtra("route_tag", route.mTag);
			LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

			/* save path and points */
			List<Pathz> paths = route.mPaths;
			for (Pathz pa : paths) {
				ContentValues contentValuesPath = new ContentValues();
				contentValuesPath.put(Pathz.KEY_ROUTE__TAG, route.mTag);
				long pathid = saveToDB(contentValuesPath, null, C.TABLE_PATHS, SQLiteDatabase.CONFLICT_REPLACE);
				for (Pointz po : pa.mPoints) {
					// lat,lon,path__id
					String conditionee = Pointz.KEY_PATHS__ID + "=" + pathid;
					ContentValues contentValuesPoint = new ContentValues();
					contentValuesPoint.put(Pointz.KEY_LAT, po.mLat);
					contentValuesPoint.put(Pointz.KEY_LAT, po.mLon);
					contentValuesPoint.put(Pointz.KEY_LAT, pathid);
					saveToDB(contentValuesPoint, conditionee, C.TABLE_POINTS, SQLiteDatabase.CONFLICT_REPLACE);
				}
				messageIntent = new Intent(C.BROADCAST_POINTS_UPDATED_ACTION);
				messageIntent.putExtra("path_id", pathid);
				LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
			}
			messageIntent = new Intent(C.BROADCAST_PATHS_UPDATED_ACTION);
			messageIntent.putExtra("route_key", route.mTag);
			LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
			break;
			
		default:
			throw new IllegalArgumentException("illegal argument here");

		}

	}

	private Cursor findFirstFromDb(String[] columns, String condition, String tableName) {
		SQLiteDatabase db = mySQLiteOpenHelper.getReadableDatabase();
		return db.query(tableName, columns, condition, null, null, null, null, "1");
	}

	// returns one of these
	// id of the inserted or updated row if single row.
	// -1 if multiple rows affected
	private long saveToDB(ContentValues contentValues, String condition, String tableName, int conflictAlgorithm)
			throws Exception {
		long id = -1;
		SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
		// if conflict is passed. just the insert. if its CONFLICT_NONE the do
		// update.
		if (conflictAlgorithm != SQLiteDatabase.CONFLICT_NONE) {
			id = db.insertWithOnConflict(tableName, null, contentValues, conflictAlgorithm);
		}
		else {
			int count = db.update(tableName, contentValues, condition, null);
			if (count == 1) {
				Cursor cursor = db.query(tableName, new String[] { "_id" }, condition, null, null, null, null, "1");
				if (cursor != null) {
					cursor.moveToFirst();
					LogCursor.d("dump", cursor);
					id = cursor.getInt(0);
				}
			}
			else {
				throw new SQLDataException("duplicate records found in table " + tableName + "for " + contentValues.toString());
			}
		}
		return id;

	}

}

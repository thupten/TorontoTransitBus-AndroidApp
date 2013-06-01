package com.thuptencho.torontotransitbus.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.thuptencho.torontotransitbus.C;
import com.thuptencho.torontotransitbus.backgroundservice.DatabaseUpdatingService;
import com.thuptencho.torontotransitbus.backgroundservice.ServiceHelper;

/**
 * Created by thupten on 5/20/13.
 */

public class TheContentProvider extends ContentProvider {
	private static UriMatcher uriMatcher;

	private static final int ROUTES = 10, ROUTES_SINGLE = 11, DIRECTIONS = 20, DIRECTIONS_SINGLE = 21, STOPS = 30,
					STOPS_SINGLE = 31, PATHS = 40, PATHS_SINGLE = 41, POINTS = 50, POINTS_SINGLE = 51, SCHEDULES = 60,
					SCHEDULES_SINGLE = 61, PREDICTIONS = 70;

	private MySQLiteOpenHelper mySqliteOpenHelper;

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
		uriMatcher.addURI(C.ContentUri.AUTHORITY, "predictions", PREDICTIONS);
	}

	@Override
	public boolean onCreate() {
		
		mySqliteOpenHelper = new MySQLiteOpenHelper(getContext(), C.Db.DATABASE_NAME, null, C.Db.DATABASE_VERSION);
		if (mySqliteOpenHelper == null) return false;
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case ROUTES:
			return "vnd.android.cursor.dir/vnd.thuptencho.torontotransitbus.routes";
		case ROUTES_SINGLE:
			return "vnd.android.cursor.item/vnd.thuptencho.torontotransitbus.routes";
		case DIRECTIONS:
			return "vnd.android.cursor.dir/vnd.thuptencho.torontotransitbus.directions";
		case DIRECTIONS_SINGLE:
			return "vnd.android.cursor.item/vnd.thuptencho.torontotransitbus.directions";
		case STOPS:
			return "vnd.android.cursor.dir/vnd.thuptencho.torontotransitbus.stops";
		case STOPS_SINGLE:
			return "vnd.android.cursor.item/vnd.thuptencho.torontotransitbus.stops";
		case PATHS:
			return "vnd.android.cursor.dir/vnd.thuptencho.torontotransitbus.paths";
		case PATHS_SINGLE:
			return "vnd.android.cursor.item/vnd.thuptencho.torontotransitbus.paths";
		case POINTS:
			return "vnd.android.cursor.dir/vnd.thuptencho.torontotransitbus.points";
		case POINTS_SINGLE:
			return "vnd.android.cursor.item/vnd.thuptencho.torontotransitbus.points";
		case SCHEDULES:
			return "vnd.android.cursor.dir/vnd.thuptencho.torontotransitbus.schedules";
		case SCHEDULES_SINGLE:
			return "vnd.android.cursor.item/vnd.thuptencho.torontotransitbus.schedules";
		case PREDICTIONS:
			return "vnd.android.cursor.item/vnd.thuptencho.torontotransitbus.predictions";
		default:
			throw new IllegalArgumentException("invalid uri %@#$");
		}
	}

	@Override
	public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArguments, String sortOrder) {

		Cursor cursor = null;
		String id;
		switch (uriMatcher.match(uri)) {
		case ROUTES:
			cursor = queryRoutes(uri, columns, selection, selectionArguments, sortOrder);
			break;
		case ROUTES_SINGLE:
			String routeTag;
			routeTag = uri.getPathSegments().get(1);
			if (selection == null) {
				selection = "tag='" + routeTag + "'";
			}
			else {
				selection = selection + "AND (tag='" + routeTag + "')";
			}
			cursor = queryRoutes(uri, columns, selection, selectionArguments, sortOrder);
			break;
		case DIRECTIONS:
			cursor = queryDirections(uri, columns, selection, selectionArguments, sortOrder);
			break;
		case DIRECTIONS_SINGLE:
			id = uri.getPathSegments().get(1);
			if (selection == null) {
				selection = "_id='" + id + "'";
			}
			else {
				selection = selection + "AND (_id='" + id + "')";
			}
			cursor = queryDirections(uri, columns, selection, selectionArguments, sortOrder);
			break;
		case STOPS:
		case STOPS_SINGLE:
			cursor = queryStopsForDirection(uri, columns, selection, selectionArguments, sortOrder);
			break;
		case PATHS:
		case PATHS_SINGLE:
			cursor = queryPaths(uri, columns, selection, selectionArguments, sortOrder);
			break;
		case POINTS:
		case POINTS_SINGLE:
			cursor = queryPoints(uri, columns, selection, selectionArguments, sortOrder);
			break;
		case SCHEDULES:
		case SCHEDULES_SINGLE:
			cursor = querySchedules(uri, columns, selection, selectionArguments, sortOrder);
			break;
		case PREDICTIONS:
			cursor = queryPredictions(uri, columns, selection, selectionArguments, sortOrder);
		default:
			throw new IllegalArgumentException("invalid uri");
		}
		/*
		 * todo set parameter for intentForService. what do i want the service
		 * to do? goto the web get the data and updateOrInsert in the database,
		 * then broadcast its change to activity.
		 */
		Intent intentForService = new Intent(getContext(), DatabaseUpdatingService.class);
		intentForService.putExtra("task", "query");
		intentForService.putExtra("uri_string", uri.toString());
		ServiceHelper.getInstance(getContext()).start(intentForService);
		return cursor;
	}

	private Cursor queryPredictions(Uri uri, String[] columns, String selection, String[] selectionArguments,
					String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	private Cursor queryRoutes(Uri uri, String[] columns, String selection, String[] selectionArguments,
					String sortOrder) {
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
		return db.query(C.Db.TABLE_ROUTES, columns, selection, selectionArguments, null, null, sortOrder);
	}

	private Cursor queryDirections(Uri uri, String[] columns, String selection, String[] selectionArguments,
					String sortOrder) {
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
		return db.query(C.Db.TABLE_DIRECTIONS, columns, selection, selectionArguments, null, null, sortOrder);
	}

	private Cursor queryStopsForDirection(Uri uri, String[] columns, String selection, String[] selectionArguments,
					String sortOrder) {
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(C.Db.TABLE_DIRECTIONS_STOPS + " INNER JOIN " + C.Db.TABLE_STOPS + " ON("
						+ C.Db.TABLE_DIRECTIONS_STOPS + ".stop__tag=" + C.Db.TABLE_STOPS + ".tag)");
		// String[] projectionIn = new String[] { "Stops." + Stop.KEY_ID,
		// "Stops." + Stop.KEY_TITLE };
		String groupBy = null, having = null;
		return qb.query(db, columns, selection, selectionArguments, groupBy, having, sortOrder);

	}

	@SuppressWarnings("unused")
	private Cursor queryStopsForRoute(Uri uri, String[] columns, String selection, String[] selectionArguments,
					String sortOrder) {
		return null;
	}

	private Cursor queryPaths(Uri uri, String[] columns, String selection, String[] selectionArguments, String sortOrder) {
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
		return db.query(C.Db.TABLE_PATHS, columns, selection, selectionArguments, null, null, sortOrder, null);

	}

	private Cursor queryPoints(Uri uri, String[] columns, String selection, String[] selectionArguments,
					String sortOrder) {
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();

		return db.query(C.Db.TABLE_POINTS, columns, selection, selectionArguments, null, null, sortOrder, null);
	}

	private Cursor querySchedules(Uri uri, String[] columns, String selection, String[] selectionArguments,
					String sortOrder) {
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();

		return db.query(C.Db.TABLE_SCHEDULES, columns, selection, selectionArguments, null, null, sortOrder, null);

	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		return null;
	}

	@Override
	public int delete(Uri uri, String s, String[] strings) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
		return 0;
	}
}

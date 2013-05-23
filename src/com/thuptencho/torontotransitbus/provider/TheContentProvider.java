package com.thuptencho.torontotransitbus.provider;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import com.thuptencho.torontotransitbus.C;
import com.thuptencho.torontotransitbus.backgroundservice.DatabaseUpdatingService;
import com.thuptencho.torontotransitbus.backgroundservice.ServiceHelper;
import com.thuptencho.torontotransitbus.models.Direction;

/**
 * Created by thupten on 5/20/13.
 */

public class TheContentProvider extends ContentProvider {
	private static UriMatcher uriMatcher;
	private static final int ROUTES = 10, ROUTES_SINGLE = 11, DIRECTIONS = 20, DIRECTIONS_SINGLE = 21, STOPS = 30,
			STOPS_SINGLE = 31, PATHS = 40, PATHS_SINGLE = 41, POINTS = 50, POINTS_SINGLE = 51, SCHEDULES = 60,
			SCHEDULES_SINGLE = 61;
	private MySQLiteOpenHelper mySqliteOpenHelper;

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

	@Override
	public boolean onCreate() {
		mySqliteOpenHelper = new MySQLiteOpenHelper(getContext(), C.DATABASE_NAME, null, C.DATABASE_VERSION);
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
		default:
			throw new IllegalArgumentException("invalid uri");
		}
	}

	@Override
	public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArguments, String sortOrder) {
		Cursor cursor = null;
		switch (uriMatcher.match(uri)) {
		case ROUTES:
			cursor = queryRoutes(uri, columns, selection, selectionArguments, sortOrder);
			break;
		case ROUTES_SINGLE:
			cursor = queryRoute(uri, columns, selection, selectionArguments, sortOrder);
			break;
		case DIRECTIONS:
		case DIRECTIONS_SINGLE:
			cursor = queryDirections(uri, columns, selection, selectionArguments, sortOrder);
			break;
		case STOPS:
		case STOPS_SINGLE:
			cursor = queryStops(uri, columns, selection, selectionArguments, sortOrder);
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

	private Cursor queryRoute(Uri uri, String[] columns, String selection, String[] selectionArguments, String sortOrder) {
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(C.TABLE_ROUTES + "," + C.TABLE_DIRECTIONS + "," + C.TABLE_STOPS);
		String groupBy = "";
		
		queryBuilder.query(db, columns, selection, selectionArguments, groupBy, having, sortOrder);
		return null;
	}

	private Cursor queryRoutes(Uri uri, String[] columns, String selection, String[] selectionArguments,
			String sortOrder) {
		Cursor c = null;
		SQLiteDatabase db = mySqliteOpenHelper.getWritableDatabase();
		c = db.query(C.TABLE_ROUTES, columns, selection, selectionArguments, null, null, sortOrder);
		return c;
	}

	private Cursor queryDirections(Uri uri, String[] columns, String selection, String[] selectionArguments,
			String sortOrder) {
		SQLiteDatabase db = mySqliteOpenHelper.getWritableDatabase();
		Cursor c = null;
		c = db.query(C.TABLE_DIRECTIONS, columns, selection, selectionArguments, null, null, sortOrder);
		return c;
	}

	private Cursor queryStops(Uri uri, String[] columns, String selection, String[] selectionArguments, String sortOrder) {
		Cursor c = null;
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
		c = db.query(C.TABLE_STOPS, columns, selection, selectionArguments, null, null, sortOrder, null);
		return c;
	}

	private Cursor queryPaths(Uri uri, String[] columns, String selection, String[] selectionArguments, String sortOrder) {
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
		Cursor c = null;
		c = db.query(C.TABLE_PATHS, columns, selection, selectionArguments, null, null, sortOrder, null);
		return c;
	}

	private Cursor queryPoints(Uri uri, String[] columns, String selection, String[] selectionArguments,
			String sortOrder) {
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
		Cursor c = null;
		c = db.query(C.TABLE_POINTS, columns, selection, selectionArguments, null, null, sortOrder, null);
		return c;
	}

	private Cursor querySchedules(Uri uri, String[] columns, String selection, String[] selectionArguments,
			String sortOrder) {
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
		Cursor c = null;
		c = db.query(C.TABLE_SCHEDULES, columns, selection, selectionArguments, null, null, sortOrder, null);
		return c;
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

package com.thuptencho.torontotransitbus;

import android.net.Uri;

public class C {
	public static final String DATABASE_NAME = "com.thuptencho.torontotransitbus.db";
	public static final int DATABASE_VERSION = 5;
	public static final String TABLE_ROUTES = "routes";
	public static final String TABLE_DIRECTIONS = "directions";
	public static final String TABLE_STOPS = "stops";
	public static final String TABLE_PATHS = "paths";
	public static final String TABLE_POINTS = "points";
	public static final String TABLE_SCHEDULES = "schedules";
	
	public static final String URL_STRING_KEY = "urlString";
    public static final String AUTHORITY = "com.thuptencho.torontotransitbus.provider";
    private static final Uri CONTENT_URI_BASE = Uri.parse("content://" + AUTHORITY);

	public static final Uri CONTENT_URI_ROUTE = Uri.withAppendedPath(C.CONTENT_URI_BASE, "routes");
	public static final Uri CONTENT_URI_DIRECTION = Uri.withAppendedPath(C.CONTENT_URI_BASE, "directions");
	public static final Uri CONTENT_URI_STOP = Uri.withAppendedPath(C.CONTENT_URI_BASE, "stops");
	public static final Uri CONTENT_URI_POINT = Uri.withAppendedPath(C.CONTENT_URI_BASE, "points");
	public static final Uri CONTENT_URI_PATH = Uri.withAppendedPath(C.CONTENT_URI_BASE, "paths");

	public static final String BROADCAST_PATHS_UPDATED_ACTION = "com.thuptencho.torontotransitbus.paths.updated";
	public static final String BROADCAST_ROUTES_UPDATED_ACTION = "com.thuptencho.torontotransitbus.routes.updated";
	public static final String BROADCAST_ROUTES_SINGLE_UPDATED_ACTION = "com.thuptencho.torontotransitbus.route.single.updated";
	public static final String BROADCAST_STOPS_UPDATED_ACTION = "com.thuptencho.torontotransitbus.stops.updated";
	public static final String BROADCAST_DIRECTIONS_UPDATED_ACTION = "com.thuptencho.torontotransitbus.directions.updated";
	public static final String BROADCAST_POINTS_UPDATED_ACTION = "com.thuptencho.torontotransitbus.points.updated";

}

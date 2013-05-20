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
    /*public static final String AUTHORITY_ROUTE = "com.thuptenchoephel.provider.route";
	public static final String AUTHORITY_DIRECTION = "com.thuptenchoephel.provider.direction";
	public static final String AUTHORITY_STOP = "com.thuptenchoephel.provider.stop";
	public static final String AUTHORITY_POINT = "com.thuptenchoephel.provider.point";
	public static final String AUTHORITY_PATH = "com.thuptenchoephel.provider.path";
*/
    public static final Uri CONTENT_URI_BASE = Uri.parse("content://" + AUTHORITY);

	/*public static final Uri CONTENT_URI_BASE_DIRECTION = Uri.parse("content://" + AUTHORITY_DIRECTION);
	public static final Uri CONTENT_URI_BASE_STOP = Uri.parse("content://" + AUTHORITY_STOP);
	public static final Uri CONTENT_URI_BASE_PATH = Uri.parse("content://" + AUTHORITY_PATH);
	public static final Uri CONTENT_URI_BASE_POINT = Uri.parse("content://" + AUTHORITY_POINT);*/

	public static final Uri CONTENT_URI_ROUTE = Uri.withAppendedPath(C.CONTENT_URI_BASE, "routes");
	public static final Uri CONTENT_URI_DIRECTION = Uri.withAppendedPath(C.CONTENT_URI_BASE, "directions");
	public static final Uri CONTENT_URI_STOP = Uri.withAppendedPath(C.CONTENT_URI_BASE, "stops");
	public static final Uri CONTENT_URI_POINT = Uri.withAppendedPath(C.CONTENT_URI_BASE, "points");
	public static final Uri CONTENT_URI_PATH = Uri.withAppendedPath(C.CONTENT_URI_BASE, "paths");

	public static final String BROADCAST_ROUTE_LIST_UPDATED = "com.thuptencho.torontotransitbus.route_list_updated_broadcast";
	public static final String BROADCAST_ROUTE_DETAIL_UPDATED = "com.thuptencho.torontotransitbus.route_detail_updated_broadcast";

}

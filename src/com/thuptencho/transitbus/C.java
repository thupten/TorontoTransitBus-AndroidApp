package com.thuptencho.transitbus;

import android.net.Uri;
import android.os.StrictMode;

public class C {
	
	public static final boolean DEVELOPER_MODE = false;
	public static final String AGENCY = "ttc";
	public static final long CHOOSE_ONE = -99999;

	public static final class Db {
		public static final String DATABASE_NAME = "appdb.db";
		public static final int DATABASE_VERSION = 5;
		public static final String TABLE_ROUTES = "routes";
		public static final String TABLE_DIRECTIONS = "directions";
		public static final String TABLE_STOPS = "stops";

		public static final String TABLE_ROUTES_STOPS = "routes_stops";
		public static final String TABLE_DIRECTIONS_STOPS = "directions_stops";

		public static final String TABLE_PATHS = "paths";
		public static final String TABLE_POINTS = "points";
		public static final String TABLE_SCHEDULES = "schedules";

	}

	public static final class ContentUri {
		public static final String URL_STRING_KEY = "urlString";
		public static final String AUTHORITY = "com.thuptencho.torontotransitbus.provider";
		private static final Uri BASE = Uri.parse("content://" + AUTHORITY);
		public static final Uri ROUTE = Uri.withAppendedPath(BASE, "routes");
		public static final Uri DIRECTION = Uri.withAppendedPath(BASE, "directions");
		public static final Uri STOP = Uri.withAppendedPath(BASE, "stops");
		public static final Uri POINT = Uri.withAppendedPath(BASE, "points");
		public static final Uri PATH = Uri.withAppendedPath(BASE, "paths");
	}

	public static final class Broadcast {
		public static final String PATHS_UPDATED_ACTION = "com.thuptencho.torontotransitbus.paths.updated";
		public static final String ROUTES_UPDATED_ACTION = "com.thuptencho.torontotransitbus.routes.updated";
		public static final String ROUTES_SINGLE_UPDATED_ACTION = "com.thuptencho.torontotransitbus.route.single.updated";
		public static final String D_STOPS_UPDATED_ACTION = "com.thuptencho.torontotransitbus.dstops.updated";
		public static final String R_STOPS_UPDATED_ACTION = "com.thuptencho.torontotransitbus.rstops.updated";
		public static final String DIRECTIONS_UPDATED_ACTION = "com.thuptencho.torontotransitbus.directions.updated";
		public static final String POINTS_UPDATED_ACTION = "com.thuptencho.torontotransitbus.points.updated";
	}

	public static final class Fragment {
		public static final class Tags {
			public static final String ROUTES_LIST_FRAGMENT = "com.dfjkldf.dfkldjflkdjf.dsflksdfoier";
			public static final String ROUTE_DETAIL_FRAGMENT = "kasdfljkasdf.zxvlkasdfljksd.sdfkljsdf";
		}
	}
	
	public static void activateStrictDebug(){
		if (DEVELOPER_MODE) {
	         StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
	                 .detectDiskReads()
	                 .detectDiskWrites()
	                 .detectNetwork()   // or .detectAll() for all detectable problems
	                 .penaltyLog()
	                 .build());
	         /*StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
	                 .detectLeakedSqlLiteObjects()
	                 .detectLeakedClosableObjects()
	                 .penaltyLog()
	                 .penaltyDeath()
	                 .build());*/
	     }
	}

}

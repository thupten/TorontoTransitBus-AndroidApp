package com.thuptencho.torontotransitbus.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import com.thuptencho.torontotransitbus.C;

/**
 * Created by thupten on 5/20/13.
 */


public class TheContentProvider extends ContentProvider {
    private static UriMatcher uriMatcher;
    private static final int ROUTES = 10, ROUTES_SINGLE = 11,
            DIRECTIONS = 20, DIRECTIONS_SINGLE = 21,
            STOPS = 30, STOPS_SINGLE = 31,
            PATHS = 40, PATHS_SINGLE = 41,
            POINTS = 50, POINTS_SINGLE = 51,
            SCHEDULES = 60, SCHEDULES_SINGLE = 61;
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
        if (mySqliteOpenHelper == null) {
            return false;
        }
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
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        switch (uriMatcher.match(uri)) {
            case ROUTES:
            case ROUTES_SINGLE:
                return queryRoutes(uri, strings, s, strings2, s2);
            case DIRECTIONS:
            case DIRECTIONS_SINGLE:
                return queryDirections(uri, strings, s, strings2, s2);
            case STOPS:
            case STOPS_SINGLE:
                return queryStops(uri, strings, s, strings2, s2);
            case PATHS:
            case PATHS_SINGLE:
                return queryPaths(uri, strings, s, strings2, s2);
            case POINTS:
            case POINTS_SINGLE:
                return queryPoints(uri, strings, s, strings2, s2);
            case SCHEDULES:
            case SCHEDULES_SINGLE:
                return querySchedules(uri, strings, s, strings2, s2);
            default:
                throw new IllegalArgumentException("invalid uri");
        }
    }

    private Cursor queryRoutes(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        Cursor c = null;
        //todo
        

        return c;
    }

    private Cursor queryDirections(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        Cursor c = null;
        //todo

        return c;
    }

    private Cursor queryStops(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        Cursor c = null;
        //todo

        return c;
    }

    private Cursor queryPaths(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        Cursor c = null;
        //todo

        return c;
    }

    private Cursor queryPoints(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        Cursor c = null;
        //todo

        return c;
    }

    private Cursor querySchedules(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        Cursor c = null;
        //todo

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

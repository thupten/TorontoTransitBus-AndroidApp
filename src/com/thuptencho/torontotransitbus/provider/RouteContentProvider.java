package com.thuptencho.torontotransitbus.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.thuptencho.torontotransitbus.Constants;
import com.thuptencho.torontotransitbus.models.Route;

public class RouteContentProvider extends ContentProvider {
	private static final int ROUTES = 0;
	private static final int ROUTE_SINGLE = 1;

	private MySQLiteOpenHelper mySqLiteOpenHelper;
	private static UriMatcher uriMatcher;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(Constants.AUTHORITY_ROUTE, "routes", ROUTES);
		uriMatcher.addURI(Constants.AUTHORITY_ROUTE, "routes/#", ROUTE_SINGLE);
	}

	@Override
	public boolean onCreate() {
		mySqLiteOpenHelper = new MySQLiteOpenHelper(getContext(),
				Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
		if (mySqLiteOpenHelper == null)
			return false;

		return true;

	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case ROUTES:
			return "vnd.android.cursor.dir/vnd.thuptencho.torontotransitbus.routes";
		default:
			throw new IllegalArgumentException("Unsupported URI:" + uri);
		}
	}

	@Override
	/* ServiceHelper > Service > Rest > ContentResolver */
	public Uri insert(Uri uri, ContentValues values) {
		Log.d("contentvalues", values.toString());
		SQLiteDatabase db = mySqLiteOpenHelper.getWritableDatabase();
		long insertedId = db.insert(Constants.TABLE_ROUTES, null, values);
		Uri insertedUri = ContentUris.withAppendedId(Constants.CONTENT_URI_ROUTE, insertedId);
		return insertedUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mySqLiteOpenHelper.getWritableDatabase();
		int count = db.update(Constants.TABLE_ROUTES, values, selection, selectionArgs);
		return count;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mySqLiteOpenHelper.getWritableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		//Intent intent = new Intent(getContext(), DatabaseUpdatingService.class);
		Cursor cursor = null;
		String inTables = "";
		String groupBy = null;
		String having = null;
		switch (uriMatcher.match(uri)) {
		case (ROUTES):
			inTables = Constants.TABLE_ROUTES;
			qb.setTables(inTables);

			cursor = qb.query(db, projection, selection, selectionArgs,
					groupBy, having, sortOrder);
			if (cursor.getCount() > 0) {
				getContext().getContentResolver().notifyChange(uri, null);
			}
			return cursor;
		case (ROUTE_SINGLE):
			int rowId = Integer.parseInt(uri.getPathSegments().get(1)) + 1;//add one because databas id start with 1
			inTables = Constants.TABLE_ROUTES;
			qb.setTables(inTables);
			qb.appendWhere(Route.KEY_ID + "=" + rowId);
			
			groupBy = null;
			having = null;
			cursor = qb.query(db, projection, selection, selectionArgs,
					groupBy, having, sortOrder);
			if (cursor.getCount() > 0) {
				getContext().getContentResolver().notifyChange(uri, null);
			}
			return cursor;
		default:
			throw new IllegalArgumentException("Invalid uri");
		}
	}
}

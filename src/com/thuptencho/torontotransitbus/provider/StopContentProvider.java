package com.thuptencho.torontotransitbus.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.thuptencho.torontotransitbus.Constants;
import com.thuptencho.torontotransitbus.models.Stop;

public class StopContentProvider extends ContentProvider {
	private static final int STOPS = 0;
	private static final int STOP_SINGLE = 1;

	private MySQLiteOpenHelper mySqLiteOpenHelper;
	private static UriMatcher uriMatcher;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(Constants.AUTHORITY_STOP, "stops", STOPS);
		uriMatcher
				.addURI(Constants.AUTHORITY_STOP, "stops/#", STOP_SINGLE);
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
		case STOPS:
			return "vnd.android.cursor.dir/vnd.torontotransitbus.stops";
		case STOP_SINGLE:
			return "vnd.android.cursor.item/vnd.torontotransitbus.stops";
		default:
			throw new IllegalArgumentException("Unsupported URI:" + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mySqLiteOpenHelper.getWritableDatabase();
		long id = db.insert(Constants.TABLE_STOPS, null, values);
		if (id != -1) {
			Uri insertedIdUri = ContentUris.withAppendedId(uri, id);
			getContext().getContentResolver().notifyChange(insertedIdUri, null);
			return insertedIdUri;
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mySqLiteOpenHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
		case STOPS:
			return db.delete(Constants.TABLE_STOPS, selection,
					selectionArgs);

		case STOP_SINGLE:
			String id = uri.getPathSegments().get(1);
			selection = selection + " AND (" + Stop.KEY_ID + "=" + id
					+ ")";
			return db.delete(Constants.TABLE_STOPS, selection,
					selectionArgs);
		}
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mySqLiteOpenHelper.getWritableDatabase();
		int rowsAffected = 0;
		switch (uriMatcher.match(uri)) {
		case STOPS:
			rowsAffected = db.update(Constants.TABLE_STOPS, values,
					selection, selectionArgs);
			break;
		case STOP_SINGLE:
			String id = uri.getPathSegments().get(1);
			selection = selection + " AND(" + Stop.KEY_ID + "=" + id + ")";
			rowsAffected = db.update(Constants.TABLE_STOPS, values,
					selection, selectionArgs);
			break;
		}
		if (rowsAffected > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return 0;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mySqLiteOpenHelper.getWritableDatabase();
		Cursor cursor = null;
		switch (uriMatcher.match(uri)) {
		case (STOPS):
			cursor = db.query(Constants.TABLE_STOPS, projection,
					selection, selectionArgs, null, null, null);
			if (cursor.getCount() > 0) {
				getContext().getContentResolver().notifyChange(uri, null);
			}
			return cursor;
		case (STOP_SINGLE):
			String rowId = uri.getPathSegments().get(1);
			selection = selection + " AND (" + Stop.KEY_ID + "=" + rowId
					+ ")";
			cursor = db.query(Constants.TABLE_STOPS, projection,
					selection, selectionArgs, null, null, sortOrder);
			if (cursor.getCount() > 0) {
				getContext().getContentResolver().notifyChange(uri, null);
			}
			return cursor;
		default:
			throw new IllegalArgumentException("Invalid uri");
		}
	}
}

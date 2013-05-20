package com.thuptencho.torontotransitbus.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.thuptencho.torontotransitbus.C;
import com.thuptencho.torontotransitbus.models.Direction;

public class DirectionContentProvider extends ContentProvider {
	private static final int DIRECTIONS = 0;
	private static final int DIRECTION_SINGLE = 1;

	private MySQLiteOpenHelper mySqLiteOpenHelper;
	private static UriMatcher uriMatcher;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(C.AUTHORITY_DIRECTION, "directions", DIRECTIONS);
		uriMatcher
				.addURI(C.AUTHORITY_DIRECTION, "directions/#", DIRECTION_SINGLE);
	}

	@Override
	public boolean onCreate() {
		mySqLiteOpenHelper = new MySQLiteOpenHelper(getContext(),
				C.DATABASE_NAME, null, C.DATABASE_VERSION);
		if (mySqLiteOpenHelper == null)
			return false;

		return true;

	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case DIRECTIONS:
			return "vnd.android.cursor.dir/vnd.torontotransitbus.directions";
		case DIRECTION_SINGLE:
			return "vnd.android.cursor.item/vnd.torontotransitbus.directions";
		default:
			throw new IllegalArgumentException("Unsupported URI:" + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mySqLiteOpenHelper.getWritableDatabase();
		long id = db.insert(C.TABLE_DIRECTIONS, null, values);
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
		case DIRECTIONS:
			return db.delete(C.TABLE_DIRECTIONS, selection,
					selectionArgs);

		case DIRECTION_SINGLE:
			String id = uri.getPathSegments().get(1);
			selection = selection + " AND (" + Direction.KEY_ID + "=" + id
					+ ")";
			return db.delete(C.TABLE_DIRECTIONS, selection,
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
		case DIRECTIONS:
			rowsAffected = db.update(C.TABLE_DIRECTIONS, values,
					selection, selectionArgs);
			break;
		case DIRECTION_SINGLE:
			String id = uri.getPathSegments().get(1);
			selection = selection + " AND(" + Direction.KEY_ID + "=" + id + ")";
			rowsAffected = db.update(C.TABLE_DIRECTIONS, values,
					selection, selectionArgs);
			break;
		}
		if (rowsAffected > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
			return rowsAffected;
		}
		return 0;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mySqLiteOpenHelper.getWritableDatabase();
		Cursor cursor = null;
		switch (uriMatcher.match(uri)) {
		case (DIRECTIONS):
			cursor = db.query(C.TABLE_DIRECTIONS, projection,
					selection, selectionArgs, null, null, null);
			if (cursor.getCount() > 0) {
				getContext().getContentResolver().notifyChange(uri, null);
			}
			return cursor;
		case (DIRECTION_SINGLE):
			String rowId = uri.getPathSegments().get(1);
			selection = selection + " AND (" + Direction.KEY_ID + "=" + rowId
					+ ")";
			cursor = db.query(C.TABLE_DIRECTIONS, projection,
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

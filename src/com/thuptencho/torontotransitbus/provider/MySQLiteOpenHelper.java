package com.thuptencho.torontotransitbus.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

//@formatter:off;
	
	private static final String CREATE_TABLE_AGENCIES = 		"CREATE  TABLE Agencies (" + 
			"			  _id INTEGER PRIMARY KEY AUTOINCREMENT," + 
			"			  tag VARCHAR(45) NOT NULL UNIQUE," + 
			"			  title VARCHAR(145) NULL ," + 
			"			  regionTitle VARCHAR(145) NULL );";		  
	
	private static final String CREATE_TABLE_ROUTES = 	"CREATE  TABLE  Routes (" + 
			"			  _id INTEGER PRIMARY KEY AUTOINCREMENT," + 
			"			  tag VARCHAR(45) NOT NULL UNIQUE," + 
			"			  title VARCHAR(145) NULL ," + 
			"			  shortTitle VARCHAR(145) NULL ," + 
			"			  color VARCHAR(45) NULL ," + 
			"			  oppositeColor VARCHAR(45) NULL ," + 
			"			  latMin VARCHAR(45) NULL ," + 
			"			  latMax VARCHAR(45) NULL ," + 
			"			  lonMin VARCHAR(45) NULL ," + 
			"			  lonMax VARCHAR(45) NULL ," + 
			"			  agencies__Tag VARCHAR(45) NOT NULL);";
				
	private static final String CREATE_TABLE_DIRECTIONS = 	"CREATE  TABLE  Directions (" + 
			"			  _id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"			  tag VARCHAR(45) NOT NULL UNIQUE," +
			"			  title VARCHAR(145) NULL ," + 
			"			  name VARCHAR(145) NULL ," + 
			"			  useForUI TINYINT(1) NULL ," + 
			"			  route__tag VARCHAR(45) NOT NULL);";
				
	private static final String CREATE_TABLE_STOPS = 	"CREATE  TABLE  Stops (" + 
			"			  _id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"			  tag VARCHAR(45) NOT NULL UNIQUE," +
			"			  title VARCHAR(145) NULL ," + 
			"			  shortTitle VARCHAR(145) NULL ," + 
			"			  lat VARCHAR(45) NULL ," + 
			"			  lon VARCHAR(45) NULL ," + 
			"			  stopId VARCHAR(45) NULL ," + 
			"			  direction__tag VARCHAR(45) NULL ," +  
			"			  route__tag VARCHAR(45) NULL); ";

	private static final String CREATE_TABLE_PATHS = 	"CREATE  TABLE  Paths (" + 
			"			  _id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"			  route__tag VARCHAR(45) NOT NULL);";


	private static final String CREATE_TABLE_POINTS = 	"CREATE  TABLE  Points (" + 
			"			  _id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"			  lat VARCHAR(45) NOT NULL ," + 
			"			  lon VARCHAR(45) NOT NULL ," + 
			"			  Paths__id INTEGER NOT NULL,"+
			"UNIQUE (lat, lon) ON CONFLICT REPLACE);";
	
	private static final String CREATE_TABLE_SCHEDULES = "CREATE  TABLE  Schedules (" +
			"			_id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"			tag VARCHAR(45) NULL ," +
			"			title VARCHAR(145) NULL ," +
			"			scheduleClass VARCHAR(45) NULL ," +
			"			direction VARCHAR(145) NULL ," +
			"			serviceClass VARCHAR(45) NULL,"+
			"			route__tag VARCHAR NOT NULL);";
//@formatter:on;	

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly())
			db.execSQL("PRAGMA foreign_keys=ON;");
	}

	public MySQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int databaseVersion) {
		super(context, name, factory, databaseVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_AGENCIES);
		db.execSQL(CREATE_TABLE_ROUTES);
		db.execSQL(CREATE_TABLE_DIRECTIONS);
		db.execSQL(CREATE_TABLE_STOPS);
		db.execSQL(CREATE_TABLE_PATHS);
		db.execSQL(CREATE_TABLE_POINTS);
		db.execSQL(CREATE_TABLE_SCHEDULES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists Agencies");
		db.execSQL("drop table if exists Routes");
		db.execSQL("drop table if exists Directions");
		db.execSQL("drop table if exists Stops");
		db.execSQL("drop table if exists Paths");
		db.execSQL("drop table if exists Points");
		db.execSQL("drop table if exists Schedules");
		this.onCreate(db);
	}

}
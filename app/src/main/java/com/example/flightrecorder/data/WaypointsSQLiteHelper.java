package com.example.flightrecorder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WaypointsSQLiteHelper extends SQLiteOpenHelper
{
	private static final String TAG = "WaypointsSQLiteHelper";
	
	public static final String TABLE_WAYPOINTS = "waypoints";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_FLIGHT_ID = "flightId";
	public static final String COLUMN_TIMESTAMP = "timeStamp";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_ALTITUDE = "altitude";
	public static final String COLUMN_SPEED = "speed";
	
	private static final String DATABASE_NAME = "flights.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_WAYPOINTS + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_FLIGHT_ID + " INTEGER NOT NULL, "
			+ COLUMN_TIMESTAMP + " INTEGER NOT NULL, "
			+ COLUMN_LONGITUDE + " REAL NOT NULL, "
			+ COLUMN_LATITUDE + " REAL NOT NULL, "
			+ COLUMN_ALTITUDE + " REAL NOT NULL, "
			+ COLUMN_SPEED + " REAL NOT NULL, "
			+ "FOREIGN KEY (" + COLUMN_FLIGHT_ID + ") REFERENCES flights(_id)"
			+ ");";
	
	private static final String DATABASE_CREATE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_WAYPOINTS + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_FLIGHT_ID + " INTEGER NOT NULL, "
			+ COLUMN_TIMESTAMP + " INTEGER NOT NULL, "
			+ COLUMN_LONGITUDE + " REAL NOT NULL, "
			+ COLUMN_LATITUDE + " REAL NOT NULL, "
			+ COLUMN_ALTITUDE + " REAL NOT NULL, "
			+ COLUMN_SPEED + " REAL NOT NULL, "
			+ "FOREIGN KEY (" + COLUMN_FLIGHT_ID + ") REFERENCES flights(_id)"
			+ ");";
	
	public WaypointsSQLiteHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase database)
	{
		database.execSQL(DATABASE_CREATE);
	}
	
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
	{
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to " 
				+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_WAYPOINTS);
		onCreate(database);
	}
	
	public void createIfNotExists(SQLiteDatabase database)
	{
		database.execSQL(DATABASE_CREATE_IF_NOT_EXISTS);
	}
}

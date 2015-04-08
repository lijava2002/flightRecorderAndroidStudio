package com.example.flightrecorder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FlightSQLiteHelper extends SQLiteOpenHelper
{
	private static final String TAG = "FlightSQLightHelper";
	
	public static final String TABLE_FLIGHTS = "flights";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_DEPARTURE = "departure";
	public static final String COLUMN_DESTINATION = "destination";
	public static final String COLUMN_AIRPLANE = "airplane";
	public static final String COLUMN_RECORDING = "recording";
	//public static final String COLUMN_WAYPOINTS = "waypoints";
	
	private static final String DATABASE_NAME = "flights.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ TABLE_FLIGHTS + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_DATE + " TEXT NOT NULL, "
			+ COLUMN_DEPARTURE + " TEXT NOT NULL, " 
			+ COLUMN_DESTINATION + " TEXT NOT NULL, "
			+ COLUMN_AIRPLANE + " TEXT NOT NULL, "
			+ COLUMN_RECORDING + "  TEXT NOT NULL);";
	
	private static final String DATABASE_CREATE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_FLIGHTS + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_DATE + " TEXT NOT NULL, "
			+ COLUMN_DEPARTURE + " TEXT NOT NULL, " 
			+ COLUMN_DESTINATION + " TEXT NOT NULL, "
			+ COLUMN_AIRPLANE + " TEXT NOT NULL, "
			+ COLUMN_RECORDING + "  TEXT NOT NULL);";
	
	public FlightSQLiteHelper(Context context)
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
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_FLIGHTS);
		onCreate(database);
	}
	
	public void createIfNotExists(SQLiteDatabase database)
	{
		database.execSQL(DATABASE_CREATE_IF_NOT_EXISTS);
	}
	
	public void addFlight(SQLiteDatabase database, String date, String departure, String destination, String airplane)
	{
		String createFlight = "INSERT INTO " + TABLE_FLIGHTS + "("
				+ COLUMN_DATE + ", " + COLUMN_DEPARTURE + ", " + COLUMN_DESTINATION + ", " + COLUMN_AIRPLANE + ") "
				+ "VALUES ('" + date + "', '" + departure + "', '" + destination + "', '" + airplane + "');";
		
		database.execSQL(createFlight);
	}
	
	public void updateFlight(SQLiteDatabase database, int flightId, String departure, String destination, String airplane)
	{
		String updateFlight = "UPDATE " + TABLE_FLIGHTS
				+ " SET "
				+ COLUMN_DEPARTURE + " = '" + departure + "', "
				+ COLUMN_DESTINATION + " = '" + destination + "', "
				+ COLUMN_AIRPLANE + " = '" + airplane + "' "
				+ " WHERE " + COLUMN_ID + " = " + flightId + ";";
		
		database.execSQL(updateFlight);
	}
}

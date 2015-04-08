package com.example.flightrecorder.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.flightrecorder.Flight;
import com.example.flightrecorder.Flight.BaseWaypoint;
import com.example.flightrecorder.Flight.Waypoint;
import com.example.flightrecorder.Flight.AccelerationPoint;

import java.util.ArrayList;

public class FlightsDataSource
{
	private final static String TAG = "FLIGHTS_DATA_SOURCE";
	
	private SQLiteDatabase database;
	private FlightSQLiteHelper flightsHelper;
	private WaypointsSQLiteHelper waypointsHelper;
    private AccelerationPointSQLiteHelper accelerationPointHelper;
	private String[] flightColumns = {FlightSQLiteHelper.COLUMN_ID, FlightSQLiteHelper.COLUMN_DATE,
								FlightSQLiteHelper.COLUMN_DEPARTURE, FlightSQLiteHelper.COLUMN_DESTINATION,
								FlightSQLiteHelper.COLUMN_AIRPLANE, FlightSQLiteHelper.COLUMN_RECORDING};
	private String[] waypointColumns = {WaypointsSQLiteHelper.COLUMN_ID, WaypointsSQLiteHelper.COLUMN_FLIGHT_ID,
								WaypointsSQLiteHelper.COLUMN_LATITUDE, WaypointsSQLiteHelper.COLUMN_LONGITUDE,
								WaypointsSQLiteHelper.COLUMN_ALTITUDE, WaypointsSQLiteHelper.COLUMN_SPEED,
								WaypointsSQLiteHelper.COLUMN_TIMESTAMP};
    private String[] accelerationPointColums = {AccelerationPointSQLiteHelper.COLUMN_ID, AccelerationPointSQLiteHelper.COLUMN_FLIGHT_ID,
                                AccelerationPointSQLiteHelper.COLUMN_X, AccelerationPointSQLiteHelper.COLUMN_Y,
                                AccelerationPointSQLiteHelper.COLUMN_Z, AccelerationPointSQLiteHelper.COLUMN_TIMESTAMP};
	
	public FlightsDataSource(Context context)
	{
		flightsHelper = new FlightSQLiteHelper(context);
		waypointsHelper = new WaypointsSQLiteHelper(context);
        accelerationPointHelper = new AccelerationPointSQLiteHelper(context);

		open();
			flightsHelper.createIfNotExists(database);
			waypointsHelper.createIfNotExists(database);
            accelerationPointHelper.createIfNotExists(database);
		close();
	}
	
	public void open() throws SQLException
	{
		database = flightsHelper.getWritableDatabase();
	}
	
	public void close()
	{
		flightsHelper.close();
	}
	
	public int createFlight(String date, String departure, String destination, String airplaneType)
	{
		int id = -1;
		
		if(database != null && flightsHelper != null)
		{
			ContentValues values = new ContentValues();
			values.put(FlightSQLiteHelper.COLUMN_DATE, date);
			values.put(FlightSQLiteHelper.COLUMN_DEPARTURE, departure);
			values.put(FlightSQLiteHelper.COLUMN_DESTINATION, destination);
			values.put(FlightSQLiteHelper.COLUMN_AIRPLANE, airplaneType);
			values.put(FlightSQLiteHelper.COLUMN_RECORDING, false);
			id = (int)database.insert(FlightSQLiteHelper.TABLE_FLIGHTS, null, values);
		}
		
		return id;
	}
	
	public void setFlightRecording(int flightId, Boolean recording)
	{
		try
		{
			String statement = "UPDATE " + FlightSQLiteHelper.TABLE_FLIGHTS + " SET " + FlightSQLiteHelper.COLUMN_RECORDING + "='" + recording.toString() + "'"
					+ " WHERE " + FlightSQLiteHelper.COLUMN_ID + " LIKE " + flightId;

			database.execSQL(statement);
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
	}
	
	public int createWaypoint(int flightId, int timeStamp, double latitude, double longitude, double altitude, float speed)
	{
		int id = -1;
		
		if(database != null && waypointsHelper != null)
		{
			ContentValues values = new ContentValues();
			values.put(WaypointsSQLiteHelper.COLUMN_FLIGHT_ID, flightId);
			values.put(WaypointsSQLiteHelper.COLUMN_TIMESTAMP, timeStamp);
			values.put(WaypointsSQLiteHelper.COLUMN_LATITUDE, latitude);
			values.put(WaypointsSQLiteHelper.COLUMN_LONGITUDE, longitude);
			values.put(WaypointsSQLiteHelper.COLUMN_ALTITUDE, altitude);
			values.put(WaypointsSQLiteHelper.COLUMN_SPEED, speed);
			id = (int)database.insert(WaypointsSQLiteHelper.TABLE_WAYPOINTS, null, values);
		}
		
		return id;
	}

    public int createAccelerationPoint(int flightId, int timeStamp, double x, double y, double z)
    {
        int id = -1;

        if(database != null && accelerationPointHelper != null)
        {
            ContentValues values = new ContentValues();
            values.put(AccelerationPointSQLiteHelper.COLUMN_FLIGHT_ID, flightId);
            values.put(AccelerationPointSQLiteHelper.COLUMN_TIMESTAMP, timeStamp);
            values.put(AccelerationPointSQLiteHelper.COLUMN_X, x);
            values.put(AccelerationPointSQLiteHelper.COLUMN_Y, y);
            values.put(AccelerationPointSQLiteHelper.COLUMN_Z, z);
            id = (int)database.insert(AccelerationPointSQLiteHelper.TABLE_ACCELERATION_POINTS, null, values);
        }

        return id;
    }
	
	public Flight getFlight(int flightId)
	{
		Flight flight = null;
		
		try
		{
			Cursor cursor = database.query(FlightSQLiteHelper.TABLE_FLIGHTS, flightColumns, FlightSQLiteHelper.COLUMN_ID + " = " + flightId, null, null, null, null);
			
			cursor.moveToFirst();
			
			flight = new Flight();
			
			while(!cursor.isAfterLast())
			{
				flight = cursorToFlight(cursor);

				flight.setWaypoints(getBaseWaypoints(flightId));
				
				cursor.moveToNext();
			}
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		return flight;
	}
	
	public ArrayList<Flight> getFlights()
	{
		ArrayList<Flight> flights = new ArrayList<Flight>();
		
		try
		{
			Cursor cursor = database.query(FlightSQLiteHelper.TABLE_FLIGHTS, flightColumns, null, null, null, null, null);
			
			if(cursor != null)
			{
				cursor.moveToFirst();
			
				while(!cursor.isAfterLast())
				{
					Flight flight = cursorToFlight(cursor); 
					
					flight.setWaypoints(getBaseWaypoints(flight.getId()));
					
					flights.add(flight);
					
					cursor.moveToNext();
				}
			}
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		return flights;
	}

    public ArrayList<BaseWaypoint> getBaseWaypoints(int flightId)
    {
        ArrayList<BaseWaypoint> baseWaypoints = new ArrayList<BaseWaypoint>();

        ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
        ArrayList<AccelerationPoint> accelerationPoints = new ArrayList<AccelerationPoint>();

        waypoints = getWaypoints(flightId);
        accelerationPoints = getAccelerationPoints(flightId);

        //waypoints.get;

        int waypointIdx = 0;
        int accelerationPointIdx = 0;

        while(waypointIdx < waypoints.size() || accelerationPointIdx < accelerationPoints.size())
        {
            if(waypointIdx < waypoints.size() && accelerationPointIdx < accelerationPoints.size())
            {
                if(waypoints.get(waypointIdx)._t <= accelerationPoints.get(accelerationPointIdx)._t)
                {
                    baseWaypoints.add(waypoints.get(waypointIdx));
                    waypointIdx++;
                }
                else
                {
                    baseWaypoints.add(accelerationPoints.get(accelerationPointIdx));
                    accelerationPointIdx++;
                }
            }
            else if(waypointIdx < waypoints.size())
            {
                baseWaypoints.add(waypoints.get(waypointIdx));
                waypointIdx++;
            }
            else if(accelerationPointIdx < accelerationPoints.size())
            {
                baseWaypoints.add(accelerationPoints.get(accelerationPointIdx));
                accelerationPointIdx++;
            }
        }

        return baseWaypoints;
    }

	public ArrayList<Waypoint> getWaypoints(int flightId)
	{
		ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
		
		try
		{
			Cursor cursor = null;
			
			try
			{
				if(flightId >= 0)
				{
					cursor = database.query(WaypointsSQLiteHelper.TABLE_WAYPOINTS, waypointColumns,
							WaypointsSQLiteHelper.COLUMN_FLIGHT_ID + " = " + flightId, null, null, null, null);
				}
				else
				{
					cursor = database.query(WaypointsSQLiteHelper.TABLE_WAYPOINTS, waypointColumns, null, null, null, null, null);
				}
			}
			catch(Exception e)
			{
				Log.e(TAG, e.toString());
				cursor = null;
			}
			
			if(cursor != null)
			{
				cursor.moveToFirst();
				
				while(!cursor.isAfterLast())
				{
					waypoints.add(cursorToWaypoint(cursor));
					
					cursor.moveToNext();
				}
			}
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		return waypoints;
	}

    public ArrayList<AccelerationPoint> getAccelerationPoints(int flightId)
    {
        ArrayList<AccelerationPoint> accelerationPoints = new ArrayList<AccelerationPoint>();

        try
        {
            Cursor cursor = null;

            try
            {
                if(flightId >= 0)
                {
                    cursor = database.query(AccelerationPointSQLiteHelper.TABLE_ACCELERATION_POINTS, accelerationPointColums,
                            AccelerationPointSQLiteHelper.COLUMN_FLIGHT_ID + " = " + flightId, null, null, null, null);
                }
                else
                {
                    cursor = database.query(AccelerationPointSQLiteHelper.TABLE_ACCELERATION_POINTS, accelerationPointColums, null, null, null, null, null);
                }
            }
            catch(Exception e)
            {
                Log.e(TAG, e.toString());
                cursor = null;
            }

            if(cursor != null)
            {
                cursor.moveToFirst();

                while(!cursor.isAfterLast())
                {
                    accelerationPoints.add(cursorToAccelerationPoint(cursor));

                    cursor.moveToNext();
                }
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, e.toString());
        }

        return accelerationPoints;
    }
	
	public void deleteFlight(int flightId, Boolean cascade)
	{
		try
		{
			if(database != null)
			{
				database.delete(FlightSQLiteHelper.TABLE_FLIGHTS, FlightSQLiteHelper.COLUMN_ID + " = " + flightId, null);
				
				if(cascade)
				{
					database.delete(WaypointsSQLiteHelper.TABLE_WAYPOINTS, WaypointsSQLiteHelper.COLUMN_FLIGHT_ID + " = " + flightId, null);
                    database.delete(AccelerationPointSQLiteHelper.TABLE_ACCELERATION_POINTS, AccelerationPointSQLiteHelper.COLUMN_FLIGHT_ID + " = " + flightId, null);
				}
			}
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
	}
	
	public void printAllFlights()
	{
		if(database != null && flightsHelper != null)
		{
			try
			{
				Cursor cursor = database.query(FlightSQLiteHelper.TABLE_FLIGHTS, flightColumns, null, null, null, null, null);
				
				cursor.moveToFirst();
				
				while(!cursor.isAfterLast())
				{
					int id = cursor.getInt(0);
					String date = cursor.getString(1);
					String dep = cursor.getString(2);
					String dest = cursor.getString(3);
					
					Log.d(TAG, "flight " + id + ": " + date + ", " + dep + ", " + dest);
					
					cursor.moveToNext();
				}
			}
			catch(Exception e)
			{
				Log.e(TAG, e.toString());
			}
		}
	}
	
	public void printAllWaypoints()
	{
		if(database != null && waypointsHelper != null)
		{
			try
			{
				Cursor cursor = database.query(WaypointsSQLiteHelper.TABLE_WAYPOINTS, waypointColumns, null, null, null, null, null);
				
				cursor.moveToFirst();
				
				while(!cursor.isAfterLast())
				{
					int id = cursor.getInt(0);
					int flightId = cursor.getInt(1);
					int timeStamp = cursor.getInt(6);
					double latitude = cursor.getDouble(2);
					double longitude = cursor.getDouble(3);
					double altitude = cursor.getDouble(4);
					double speed = cursor.getDouble(5);
					
					Log.d(TAG, "waypoint " + id + ": " + flightId + ", " + timeStamp + ", " + latitude + ", " + longitude + ", " + altitude + ", " + speed);
					
					cursor.moveToNext();
				}
			}
			catch(Exception e)
			{
				Log.e(TAG, e.toString());
			}
		}
	}
	
	public void updateFlight(Flight flight)
	{
		if(database != null && flightsHelper != null)
		{
			try
			{
				flightsHelper.updateFlight(database,  flight.getId(),  flight.getDeparture(), flight.getDestination(), flight.getAirplaneType());
			}
			catch(Exception e)
			{
				Log.e(TAG, e.toString());
			}
		}
	}
	
	//this deletes everything, only use if absolutely positively sure
	public void resetDataBase()
	{
		flightsHelper.onUpgrade(database, 1, 1);
		waypointsHelper.onUpgrade(database, 1, 1);
        accelerationPointHelper.onUpgrade(database, 1, 1);
	}

	private Flight cursorToFlight(Cursor cursor)
	{
		Flight flight = new Flight();
		
		try
		{
			int id = cursor.getInt(0);
			String date = cursor.getString(1);
			String dep = cursor.getString(2);
			String dest = cursor.getString(3);
			String airplane = cursor.getString(4);
			String recording = cursor.getString(5);
			
			flight.setId(id);
			flight.setDate(date);
			flight.setDeparture(dep);
			flight.setDestination(dest);
			flight.setAirplaneType(airplane);
			flight.setRecording(Boolean.parseBoolean(recording));
		}
		catch(Exception e)
		{
			Log.e(TAG + "_cursorToFlight", e.toString());
		}
		
		return flight;
	}
	
	private Waypoint cursorToWaypoint(Cursor cursor)
	{
		Waypoint waypoint = new Waypoint(-1, 0.0, 0.0, 0.0, 0.0f);
		
		try
		{
			//int id = cursor.getInt(0);
			//int flightId = cursor.getInt(1);
			int timeStamp = cursor.getInt(6);
			double lat = cursor.getDouble(2);
			double lon = cursor.getDouble(3);
			double alt = cursor.getDouble(4);
			float speed = cursor.getFloat(5);
			
			waypoint._t = timeStamp;
			waypoint._latitude = lat;
			waypoint._longitude = lon;
			waypoint._altitude = alt;
			waypoint._speed = speed;
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		return waypoint;
	}

    private AccelerationPoint cursorToAccelerationPoint(Cursor cursor)
    {
        AccelerationPoint accelerationPoint = new AccelerationPoint(-1, 0.0, 0.0, 0.0);

        try
        {
            int timeStamp = cursor.getInt(5);
            double x = cursor.getDouble(2);
            double y = cursor.getDouble(3);
            double z = cursor.getDouble(4);

            accelerationPoint._t = timeStamp;
            accelerationPoint._x = x;
            accelerationPoint._y = y;
            accelerationPoint._z = z;
        }
        catch(Exception e)
        {
            Log.e(TAG, e.toString());
        }

        Log.d(TAG, accelerationPoint.serialize());

        return accelerationPoint;
    }
}

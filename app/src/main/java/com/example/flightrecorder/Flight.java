package com.example.flightrecorder;

import android.util.Log;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class Flight
{
	private static String TAG = "FLIGHT";
	
	private int _id = -1;
	private Date _date;
	private String _departure = "";
	private String _destination = "";
	private String _airplaneType = "";
	private Boolean _recording = false;
	
	private List<Waypoint> _waypoints = new ArrayList<Waypoint>();
	
	public static class Date
	{
		//private static String TAG = "FLIGHT.DATE";
		
		public int _year = 0;
		public int _month = 0;
		public int _day = 0;
		
		public int _hour = 0;
		public int _minute = 0;
		public int _second = 0;
		
		public Date(int year, int month, int day, int hour, int minute, int second)
		{
			_year = year;
			_month = month;
			_day = day;
			
			_hour = hour;
			_minute = minute;
			_second = second;
		}
		
		public Date(String date)
		{
			_year = 0;
			_month = 0;
			_day = 0;
			
			_hour = 0;
			_minute = 0;
			_second = 0;
			
			set(date);
		}
		
		public String toString()
		{
			String result = "";
			
			result += Integer.toString(_year) + ".";
			result += Integer.toString(_month) + ".";
			result += Integer.toString(_day) + " - ";
			result += Integer.toString(_hour) + ":";
			result += Integer.toString(_minute) + ":";
			result += Integer.toString(_second);
			
			return result;
		}
		
		public void set(String date)
		{
			int idx = date.indexOf(".");
			
			if(idx >= 0)
			{
				String tmp = date.substring(0, idx);
				_year = Integer.parseInt(tmp);
				date = date.substring(idx+1);
				
				idx = date.indexOf(".");
				if(idx >= 0)
				{
					tmp = date.substring(0, idx);
					_month = Integer.parseInt(tmp);
					date = date.substring(idx+1);
					
					idx = date.indexOf(" - ");
					if(idx >= 0)
					{
						tmp = date.substring(0, idx);
						_day = Integer.parseInt(tmp);
						date = date.substring(idx+3);
						
						idx = date.indexOf(":");
						if(idx >= 0)
						{
							tmp = date.substring(0, idx);
							_hour = Integer.parseInt(tmp);
							date = date.substring(idx+1);
							
							idx = date.indexOf(":");
							if(idx >= 0)
							{
								tmp = date.substring(0, idx);
								_minute = Integer.parseInt(tmp);
								date = date.substring(idx+1);
								
								if(date.length() > 0)
								{
									_second = Integer.parseInt(date);
								}
							}
						}
					}
				}
			}
		}
		
		public String serializeToHttp()
		{
			String result = "";

			//2005-08-15T15%3A52%3A01%2B00%3A00
			//%3A :
			//%2B +
			
			//year
			result += Integer.toString(_year) + "-";
			if(_month < 10)
				result += "0";
			result += Integer.toString(_month) + "-";
			if(_day < 10)
				result += "0";
			result += Integer.toString(_day) + "T";
			
			//daytime
			if(_hour < 10)
				result += "0";
			result += _hour + "%3A";
			if(_minute < 10)
				result += "0";
			result += _minute + "%3A";
			if(_second < 10)
				result += "0";
			result += _second + "%2B";
			result += "00%3A00";
			
			//TODO: get correct time zone
			//TimeZone.getDefault();
			
			return result;
		}
		
		public String serialize()
		{
			String result = "";
			
			result += Integer.toString(_year) + ".";
			result += Integer.toString(_month) + ".";
			result += Integer.toString(_day) + " ";
			result += Integer.toString(_hour) + ":";
			result += Integer.toString(_minute) + ":";
			result += Integer.toString(_second);
			
			return result;
		}
	};
	
	public static class Waypoint
	{
		public int _t;
		public double _latitude;
		public double _longitude;
		public double _altitude;
		public float _speed;
		
		public Waypoint(int t, double latitude, double longitude, double altitude, float speed)
		{
			_t = t;
			_latitude = latitude;
			_longitude = longitude;
			_altitude = altitude;
			_speed = speed;
		}
		
		public String serialize()
		{
			String result = "";
			JSONObject object = new JSONObject();
			
			try
			{
				object.put("t", _t);
				object.put("lat", _latitude);
				object.put("long", _longitude);
				object.put("alt", _altitude);
				object.put("speed", _speed);
				
				result = object.toString();
			}
			catch(JSONException e)
			{
				Log.e(TAG, e.getMessage());
			}
			
			return result;
		}
	};
	
	public Flight()
	{
	}
	
	public int getId()
	{
		return _id;
	}
	
	public void setId(int id)
	{
		_id = id;
	}
	
	public Date getDate()
	{
		return _date;
	}
	
	public void setDate(Date date)
	{
		_date = date;
	}
	
	public void setDate(String date)
	{
		if(_date == null)
		{
			_date = new Date(date);
		}
		else
		{
			_date.set(date);
		}
	}
	
	public String getDeparture()
	{
		return _departure;
	}
	
	public void setDeparture(String departure)
	{
		//if(departure.length() > 0)
			_departure = departure;
	}
	
	public String getDestination()
	{
		return _destination;
	}
	
	public void setDestination(String destination)
	{
		//if(destination.length() > 0)
			_destination = destination;
	}
	
	public String getAirplaneType()
	{
		return _airplaneType;
	}
	
	public void setAirplaneType(String airplaneType)
	{
		//if(airplaneType.length() > 0)
			_airplaneType = airplaneType;
	}
	
	public Boolean getRecording()
	{
		return _recording;
	}
	
	public void setRecording(Boolean recording)
	{
		_recording = recording;
	}
	
	public void addWaypoint(Waypoint waypoint)
	{
		_waypoints.add(waypoint);
	}
	
	public int getWaypointCount()
	{
		return _waypoints.size();
	}
	
	public Waypoint getWaypoint(int index)
	{
		if(index >= _waypoints.size() || index < 0)
		{
			Log.e(TAG, "getWaypoint: index out of range");
			return null;
		}
		
		return _waypoints.get(index);
	}
	
	public void setWaypoints(ArrayList<Waypoint> waypoints)
	{
		_waypoints = waypoints;
	}
	
	public String serialize()
	{
		String result = "";
		
		JSONObject object = new JSONObject();
		JSONArray array = new JSONArray();
		
		try
		{
			object.put("date", _date);
			object.put("departure", _departure);
			object.put("destination", _destination);

			for(Waypoint waypoint: _waypoints)
			{
				array.put(waypoint.serialize());
			}
			
			object.put("waypoints", array);
			
			result = object.toString();
		}
		catch(JSONException e)
		{
			Log.e(TAG, e.getMessage());
		}
		
		return result;
	}
	
	public String serializeToHttp()
	{
		String result = "";
		
		try
		{
			/*
			if(_departure.length() <= 0)
			{
				_departure = "nA";
			}
			if(_destination.length() <= 0)
			{
				_destination = "nA";
			}
			*/
			
			result += "dateW3C=" + _date.serializeToHttp();//"2005-08-15T15%3A52%3A01%2B00%3A00";
			if(_departure.length() > 0)
			{
				result += "&departure=" + _departure;
			}
			if(_destination.length() > 0)
			{
				result += "&arrival=" + _destination;
			}
			if(_airplaneType.length() > 0)
			{
				result += "&aircraft=" + _airplaneType;
			}
			result += "&waypoints=";
			
			//result += "%5B%0D%0A%7B%22t%22%3A54%2C%22lat%22%3A48.15853714942932%2C%22long%22%3A14.190693497657776%2C%22alt%22%3A676.0%2C%22speed%22%3A56.502213%7D%0D%0A%0D%0A%2C%7B%22t%22%3A55%2C%22lat%22%3A48.15804898738861%2C%22long%22%3A14.190768599510193%2C%22alt%22%3A676.0%2C%22speed%22%3A56.5%7D%5D";
			
			String waypoints = URLEncoder.encode(serializeWaypointsToHttp(), "ISO-8859-1");
			//waypoints = WebInterface.compress(waypoints);
			
			if(waypoints.length() > 0)
			{
				result += waypoints;
			}
			else
			{
				result += "%5B%5D";
			}
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		return result;
	}
	
	private String serializeWaypointsToHttp()
	{
		String result = "[";
		
		for(int i = 0; i < _waypoints.size(); i++)
		{
			Waypoint waypoint = _waypoints.get(i);
			
			result += waypoint.serialize();
			
			if(i < (_waypoints.size()-1))
			{
				result += ",";
			}
		}
		
		result += "]";
		
		return result;
	}
}

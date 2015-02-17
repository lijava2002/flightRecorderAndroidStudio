package com.example.flightrecorder;

import java.util.Calendar;
import java.lang.Thread;

import com.example.flightrecorder.math.Vector3f;
import com.example.flightrecorder.math.Matrix33f;

import com.example.flightrecorder.R;
import com.example.flightrecorder.LogWriter;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.os.PowerManager;

public class ServicePathLog extends Service implements SensorEventListener
{
	private String TAG = "ServicePathLog";
	
	private LocationManager m_locationManager = null;
	private LocationProvider m_locationProvider = null;
	private int m_startTime = 0;
	
	private String m_departure = "";
	private String m_destination = "";
	private String m_airplaneType = "";
	
	private Flight _flight = null;
	
	private PowerManager.WakeLock _wakeLock = null;
	
	private Thread.UncaughtExceptionHandler _uncaughtExceptionHandler = null;
	
	private int _dbId = -1;
	
	private int _waypointsCount = 0;
	
	private Messenger _messenger = null;
	
	private LogWriter _logWriter = new LogWriter();
	private String _fileName = "";
	
	private SensorManager _sensorManager;
	private Sensor _accelerometer;
    private Sensor _rotationSensor;

    private double _minGForce = 1.0;
    private double _maxGForce = 1.0;

    private Matrix33f _pitchRotation = null;
    private Matrix33f _bankRotation = null;
    private Matrix33f _azimuthRotation = null;
	
	private Thread.UncaughtExceptionHandler _exceptionHandler = new Thread.UncaughtExceptionHandler()
	{
		@Override
		public void uncaughtException(Thread thread, Throwable ex)
		{
			try
			{
				if(_wakeLock != null)
				{
					if(_wakeLock.isHeld())
					{
						_wakeLock.release();
					}
				}
			}
			catch(Exception e)
			{
				Log.e(TAG, "Uncaught Exception: " + ex.getMessage());
			}
			finally
			{
				Thread.setDefaultUncaughtExceptionHandler(_uncaughtExceptionHandler);
				_uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), ex);
			}
		}
	};
	
	private ServicePathLog _this = null;
	
	private final LocationListener m_locationListener = new LocationListener()
	{
		@Override
		public void onProviderEnabled(String provider)
		{
		}
		
		@Override
	    public void onLocationChanged(Location location)
		{
			Calendar c = Calendar.getInstance();
			int t = (c.get(Calendar.HOUR_OF_DAY) * 60 * 60) + (c.get(Calendar.MINUTE) * 60) + c.get(Calendar.SECOND);
			t -= m_startTime;
			
			//_flight.addWaypoint(new Flight.Waypoint(t, location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getSpeed()));
			
			String jsonMessage = "";
			
			if(_waypointsCount > 0)
			{
				jsonMessage += ",";
			}
			
			jsonMessage += "\"waypoint\":{";
			jsonMessage += "\"t\":\"" + t +"\",";
			jsonMessage += "\"lat\":\"" + location.getLatitude() + "\",";
			jsonMessage += "\"long\":\"" + location.getLongitude() + "\",";
			jsonMessage += "\"alt\":\"" + location.getAltitude() + "\",";
			jsonMessage += "\"speed\":\"" + location.getSpeed() + "\"}";
			
			_logWriter.writeToFile(jsonMessage);
			
			if(_this != null)
			{
				_waypointsCount++;
			}
		}
		
		@Override
		public void onProviderDisabled(String provider)
		{
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}
	};
	
	
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public void onCreate()
	{
		_this = this;
		
		_uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(_exceptionHandler);
		
		_sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		_accelerometer = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		_sensorManager.registerListener(this,  _accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        _rotationSensor = _sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        _sensorManager.registerListener(this, _rotationSensor, SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	@Override
	public void onStart(Intent intent, int startId)
	{
		m_departure = intent.getStringExtra(getString(R.string.log_departure));
		m_destination = intent.getStringExtra(getString(R.string.log_destination));
		m_airplaneType = intent.getStringExtra(getString(R.string.log_airplane_type));
		
		//_messenger = (Messenger)intent.getExtras().get(getString(R.string.log_path_log_handler));
		
		PowerManager p = (PowerManager) getSystemService(Context.POWER_SERVICE);
		_wakeLock = p.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		_wakeLock.acquire();
		
		setupLocationProvider();
	}
	
	@Override
	public void onDestroy()
	{
		try
		{
			if(_wakeLock.isHeld())
				_wakeLock.release();
		}
		catch(Error error)
		{
			Log.e(TAG, error.getMessage());
		}

		stopLocationProvider();
        _sensorManager.unregisterListener(this);
	}
	
	protected void setupLocationProvider()
	{
		try
		{
			if(m_locationManager == null)
			{
				m_locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			}
			
			boolean gpsEnabled = m_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			
			if(!gpsEnabled)
			{
				this.stopSelf();
			}
			
			m_locationProvider = m_locationManager.getProvider(LocationManager.GPS_PROVIDER);
			m_locationProvider.getName();
			
			m_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, m_locationListener);
			
			initFlight();
			Calendar c = Calendar.getInstance();
			m_startTime = (c.get(Calendar.HOUR_OF_DAY) * 60 * 60) + (c.get(Calendar.MINUTE) * 60) + c.get(Calendar.SECOND);
			//String date = c.get(Calendar.YEAR) + "_" + c.get(Calendar.MONTH) + "_" + c.get(Calendar.DAY_OF_MONTH) + "-"
			//			+ c.get(Calendar.HOUR_OF_DAY) + "_" + c.get(Calendar.MINUTE) + "_" + c.get(Calendar.SECOND);
		}
		catch(Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
	}
	
	private void stopLocationProvider()
	{
		if(m_locationManager != null)
		{
			m_locationManager.removeUpdates(m_locationListener);
		}
		
		Message msg = Message.obtain();
		
		//delete flight when no waypoints where recorded
		
		String jsonMessage = "]}";
		
		_logWriter.writeToFile(jsonMessage);
		_logWriter.closeFile();
		
		if(_waypointsCount <= 1)
		{
			
		}
		else
		{
			
		}
	}
	
	private void initFlight()
	{
		_flight = new Flight();
		
		Calendar c = Calendar.getInstance();
		//String date = c.get(Calendar.DAY_OF_MONTH) + "." + c.get(Calendar.MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
		
		_fileName = "flight_" + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.MONTH) + "_" + c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE) + ".txt";
		_logWriter.openFile(_fileName);
		
		_flight.setDate(new Flight.Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
										c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND)));
		
		_flight.setDeparture(m_departure);
		_flight.setDestination(m_destination);
		_flight.setAirplaneType(m_airplaneType);
		
		String jsonMessage = "\"flight\": {";
		jsonMessage += "\"departure\":\"" + m_departure + "\",";
		jsonMessage += "\"destination\":\"" + m_departure + "\",";
		jsonMessage += "\"airplane\":\"" + m_departure + "\",";
		jsonMessage += "\"date\":\"" + _flight.getDate().serialize() + "\"\",";
		jsonMessage += "\"waypoints\":[";
		
		_logWriter.writeToFile(jsonMessage);
		
		_waypointsCount = 0;
	}
	
	@Override
	public void onSensorChanged(SensorEvent sensorEvent)
	{
		Sensor sensor = sensorEvent.sensor;
		
		if(sensor.getType() == Sensor.TYPE_ACCELEROMETER && _pitchRotation != null && _bankRotation != null)
		{
			float x = sensorEvent.values[0];
			float y = sensorEvent.values[1];
			float z = sensorEvent.values[2];

            Vector3f vec = new Vector3f(x, y, z);
            vec = _pitchRotation.multiplyVector3f(vec);
            //vec = _bankRotation.multiplyVector3f(vec);
            //vec = _azimuthRotation.multiplyVector3f(vec);

            int t = getSecondsSinceStart();
			
			String jsonMessage = "";
			
			if(_waypointsCount > 0)
			{
				jsonMessage += ",";
			}
			
			jsonMessage += "\"acceleration\":{";
			jsonMessage += "\"t\":\"" + t + "\",";
			jsonMessage += "\"x\":\"" + vec.getX() + "\",";
			jsonMessage += "\"y\":\"" + vec.getY() + "\",";
			jsonMessage += "\"z\":\"" + vec.getZ() + "\"}";
			
			_logWriter.writeToFile(jsonMessage);
			
			_waypointsCount++;

            //Log.d(TAG, "acceleration: " + x + ", " + y + ", " + z);

            float foo = x*x + y*y + z*z;
            double bar = foo;
            double gForce = Math.sqrt(bar);
            gForce /= 9.81;

            if(gForce > _maxGForce)
            {
                _maxGForce = gForce;
            }

            if(gForce < _minGForce)
            {
                _minGForce = gForce;
            }

            double[] values = {gForce, _maxGForce, _minGForce};
            String[] keys = {getString(R.string.broadcast_extra_gforce),
                            getString(R.string.broadcast_extra_max_gforce),
                            getString(R.string.broadcast_extra_min_gforce)};

            broadcastMessage(values, keys);

            //Log.d(TAG, "source: " + x + ", " + y + ", " + z);
            //Log.d(TAG, "acceleration0: " + vec.getX() + ", " + vec.getY() + ", " + vec.getZ());
        }
        else if(sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
        {
            /*
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            Log.d(TAG, "rotation: " + x + ", " + y + ", " + z);
            */

            float[] rotationMatrix = new float[4 * 4];
            float[] orientation = new float[3];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, rotationMatrix);
            SensorManager.getOrientation(rotationMatrix, orientation);

            //Log.d(TAG, "orientation: " + Math.toDegrees((double)orientation[0]) + ", " + Math.toDegrees((double)orientation[1]) + ", " + Math.toDegrees((double)orientation[2]));

            //orientation[0] = 0.0f* 3.14159f - orientation[0];
            //orientation[1] = 0.0f* 3.14159f - orientation[1];
            //orientation[2] = 0.0f* 3.14159f + orientation[2];

            //orientation[1] = -orientation[1];

            if(orientation[0] < 0.0f)
            {
                orientation[0] = 2.0f*3.14159f + orientation[0];
            }
            if(orientation[1] < 0.0f)
            {
                orientation[1] = 2.0f*3.14159f + orientation[1];
            }
            if(orientation[2] < 0.0f)
            {
                orientation[2] = 2.0f*3.14159f + orientation[2];
            }

            //Log.d(TAG, "orientation: " + Math.toDegrees((double)orientation[0]) + ", " + Math.toDegrees((double)orientation[1]) + ", " + Math.toDegrees((double)orientation[2]));
            Log.d(TAG, "orientation: " + (double)orientation[0] + ", " + (double)orientation[1] + ", " + (double)orientation[2]);

            float[] pitch0 = {1.0f, 0.0f, 0.0f};
            float[] pitch1 = {0.0f, (float)Math.cos((double)orientation[1]), (float)-Math.sin((double)orientation[1])};
            float[] pitch2 = {0.0f, (float)Math.sin((double) orientation[1]), (float)Math.cos((double) orientation[1])};
            _pitchRotation = new Matrix33f(pitch0, pitch1, pitch2);

            float[] bank0 = {(float)Math.cos((double)orientation[2]), 0.0f, (float)Math.sin((double)orientation[2])};
            float[] bank1 = {0.0f, 1.0f, 0.0f};
            float[] bank2 = {(float)-Math.sin((double)orientation[2]), 0.0f, (float)Math.cos((double)orientation[2])};
            _bankRotation = new Matrix33f(bank0, bank1, bank2);

            float[] azimuth0 = {(float)Math.cos((double)orientation[0]), (float)-Math.sin((double)orientation[0]), 0.0f};
            float[] azimuth1 = {(float)Math.sin((double)orientation[0]), (float)Math.cos((double)orientation[0]), 0.0f};
            float[] azimuth2 = {0.0f, 0.0f, 1.0f};
            _azimuthRotation = new Matrix33f(azimuth0, azimuth1, azimuth2);

            //Vector3f vec = new Vector3f(0.0f, 0.0f, 1.0f);
            //Log.d(TAG, "source: " + vec.getX() + ", " + vec.getY() + ", " + vec.getZ());

            //vec = _bankRotation.multiplyVector3f(vec);
            //Log.d(TAG, "bank: " + vec.getX() + ", " + vec.getY() + ", " + vec.getZ());

            //vec = _pitchRotation.multiplyVector3f(vec);
            //Log.d(TAG, "pitch: " + vec.getX() + ", " + vec.getY() + ", " + vec.getZ());
        }
	}

    private void rotateVector3d(float[] vector, float[] eulerAngles)
    {
        if(vector.length != 3)
        {
            Log.e(TAG, "rotateVector3d: Vector does not have 3 dimensions");
            return;
        }
        else if(eulerAngles.length != 3)
        {
            Log.e(TAG, "rotateVector3d: No 3 angles provided");
            return;
        }


    }

    private int getSecondsSinceStart()
    {
        Calendar c = Calendar.getInstance();
        int t = (c.get(Calendar.HOUR_OF_DAY) * 60 * 60) + (c.get(Calendar.MINUTE) * 60) + c.get(Calendar.SECOND);
        t -= m_startTime;

        return t;
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
	}

    private void broadcastMessage(double[] values, String[] keys)
    {
        if(values.length != keys.length)
        {
            Log.w(TAG, "broadcastMessage: Number of keys and values is different!");
            return;
        }
        else if(values.length <= 0)
        {
            Log.w(TAG, "broadcastMessage: No values provided for broadcast");
        }

        Intent intent = new Intent(getString(R.string.intent_gforce_update));

        for(int i = 0; i < values.length; i++)
        {
            intent.putExtra(keys[i], values[i]);
        }

        sendBroadcast(intent);
    }
}

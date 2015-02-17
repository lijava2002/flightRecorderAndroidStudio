package com.example.flightrecorder;

import com.example.flightrecorder.ServicePathLog;
import com.example.flightrecorder.dialogs.DialogGPSOff;
import com.example.flightrecorder.R;
import com.example.flightrecorder.customViews.GIndicator;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends FragmentActivity
{
	private static String TAG = "MainActivity";
	
	private static final String PREFERENCES = "flightRecorder_preferences";
	private static final String B_LOGGING = "flightRecorder_isLogging";
	
	private Boolean m_logging = false;

    private BroadcastReceiver m_broadcastReceiver = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		SharedPreferences settings = this.getSharedPreferences(PREFERENCES, 0);
		if(settings != null)
		{
			m_logging = settings.getBoolean(B_LOGGING, false);
		}

        registerBroadcastReceiver();


	}

    @Override
    public void onStop()
    {
        super.onStop();

        unregisterBroadcastReceiver();
    }

	public void toggleRecording(View view)
	{
		if(m_logging)
		{
			stopPositionLogging();
		}
		else
		{
			startPositionLogging();
		}
	}
	
	private void startPositionLogging()
    {
		LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			String departure = "";
			String arrival = "";
			String airplaneType = "";
			
	    	Intent intent = new Intent(this, ServicePathLog.class);
	    	intent.putExtra(getString(R.string.log_departure), departure);
			intent.putExtra(getString(R.string.log_destination), arrival);
			intent.putExtra(getString(R.string.log_airplane_type), airplaneType);
			this.startService(intent);
			
			m_logging = !m_logging;
			SharedPreferences settings = this.getSharedPreferences(PREFERENCES, 0);
			if(settings != null)
			{
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(B_LOGGING, m_logging);
				editor.commit();
			}

            GIndicator gIndicator = (GIndicator)findViewById(R.id.gIndicator);
            if(gIndicator != null)
            {
                gIndicator.setIndicating(true);
            }
		}
		else
		{
			DialogGPSOff dialog = new DialogGPSOff();
			dialog.show(this.getSupportFragmentManager(), TAG);
		}
    }
	
	private void stopPositionLogging()
	{
		Intent intent = new Intent(this, ServicePathLog.class);
		this.stopService(intent);
		
		m_logging = !m_logging;
		SharedPreferences settings = this.getSharedPreferences(PREFERENCES, 0);
		if(settings != null)
		{
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(B_LOGGING, m_logging);
			editor.commit();
		}

        GIndicator gIndicator = (GIndicator)findViewById(R.id.gIndicator);
        if(gIndicator != null)
        {
            gIndicator.setIndicating(false);
        }
	}


    private void registerBroadcastReceiver()
    {
        Log.d(TAG, "registerBroadcastReceiver");

        try
        {
            m_broadcastReceiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    double gForce = 1.0;
                    gForce = intent.getDoubleExtra(getString(R.string.broadcast_extra_gforce), gForce);

                    double maxGForce = 1.0;
                    maxGForce = intent.getDoubleExtra(getString(R.string.broadcast_extra_max_gforce), maxGForce);

                    double minGForce = 1.0;
                    minGForce = intent.getDoubleExtra(getString(R.string.broadcast_extra_min_gforce), minGForce);

                    updateGIndication(gForce, maxGForce, minGForce);
                }
            };

            IntentFilter accelerationUpdateFilter = new IntentFilter(getString(R.string.intent_gforce_update));
            registerReceiver(m_broadcastReceiver, accelerationUpdateFilter);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    private void unregisterBroadcastReceiver()
    {
        Log.d(TAG, "unregisterBroadcastReceiver");

        try
        {
            if (m_broadcastReceiver != null)
            {
                unregisterReceiver(m_broadcastReceiver);
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    private void updateGIndication(double currentG, double maxG, double minG)
    {
        setTextViewText(R.id.textView_currentGForce, getString(R.string.textView_current_gForce) + currentG);
        setTextViewText(R.id.textView_maxGForce, getString(R.string.textView_max_gForce) + maxG);
        setTextViewText(R.id.textView_minGForce, getString(R.string.textView_min_gForce) + minG);

        GIndicator gIndicator = (GIndicator)findViewById(R.id.gIndicator);
        if(gIndicator != null)
        {
            gIndicator.setIndicating(m_logging);
            gIndicator.setCurrentG((float)currentG);
            gIndicator.setMaxG((float)maxG);
            gIndicator.setMinG((float)minG);
        }
    }

    private void setTextViewText(int textViewId, String text)
    {
        TextView view = (TextView)this.findViewById(textViewId);
        if(view != null)
        {
            view.setText(text);
        }
        else
        {
            Log.e(TAG, "setTextViewText: failed to find text view with id " + textViewId);
        }
    }
}

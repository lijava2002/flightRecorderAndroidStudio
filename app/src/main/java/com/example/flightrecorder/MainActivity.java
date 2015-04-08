package com.example.flightrecorder;

import com.example.flightrecorder.ServicePathLog;
import com.example.flightrecorder.dialogs.DialogGPSOff;
import com.example.flightrecorder.dialogs.DialogHelp;
import com.example.flightrecorder.R;
import com.example.flightrecorder.customViews.GIndicator;
import com.example.flightrecorder.customViews.TextBox;
import com.example.flightrecorder.customViews.DataItem;
import com.example.flightrecorder.dialogs.DialogNoRecording;
import com.example.flightrecorder.dialogs.DialogSendDeleteFlight;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import java.io.File;

public class MainActivity extends FragmentActivity
{
	private static String TAG = "MainActivity";
	
	private static final String PREFERENCES = "flightRecorder_preferences";
	private static final String B_LOGGING = "flightRecorder_isLogging";
	
	private Boolean m_logging = false;

    private BroadcastReceiver m_broadcastReceiver = null;

    private final int SELECT_FLIGHT = 0;
	
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_send_flight:
                doSendFlight();
                break;
            case R.id.menu_manual:
                doShowHelp();
                break;
        }

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

        setRecordButtonState(m_logging);
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

    public void sendFlight(View view)
    {
        doSendFlight();
    }

    public void showHelp(View view)
    {
        doShowHelp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {
        Log.d(TAG, "onActivityResult");

        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode)
        {
            case SELECT_FLIGHT:
                if(resultCode == RESULT_OK)
                {
                    final Uri flightUri = imageReturnedIntent.getData();

                    Log.d(TAG, flightUri.toString());

                    MailUtility.sendEmail(this, getString(R.string.email_address),
                            getString(R.string.mail_subject), getString(R.string.mail_message), flightUri);
                }
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

            TextBox textBox = (TextBox)findViewById(R.id.infoBox);
            if(textBox != null)
            {
                textBox.setIndicating(true);
            }

            setRecordButtonState(true);
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

        TextBox textBox = (TextBox)findViewById(R.id.infoBox);
        if(textBox != null)
        {
            textBox.setIndicating(false);
        }

        setRecordButtonState(false);
	}


    private void registerBroadcastReceiver()
    {
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

                    int gpsWaypointCount = 0;
                    gpsWaypointCount = intent.getIntExtra(getString(R.string.broadcast_extra_gps_waypoint_count), gpsWaypointCount);

                    updateGIndication(gForce, maxGForce, minGForce, gpsWaypointCount);
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

    private void updateGIndication(double currentG, double maxG, double minG, int waypointCount)
    {
        GIndicator gIndicator = (GIndicator)findViewById(R.id.gIndicator);
        if(gIndicator != null)
        {
            gIndicator.setIndicating(m_logging);
            gIndicator.setCurrentG((float)currentG);
            gIndicator.setMaxG((float)maxG);
            gIndicator.setMinG((float)minG);
        }

        TextBox textBox = (TextBox)findViewById(R.id.infoBox);
        if(textBox != null)
        {
            textBox.setIndicating(m_logging);
            textBox.addDataItem(new DataItem(getString(R.string.infobox_current_g), (float)currentG));
            textBox.addDataItem(new DataItem(getString(R.string.infobox_max_g), (float)maxG));
            textBox.addDataItem(new DataItem(getString(R.string.infobox_min_g), (float)minG));
            textBox.addDataItem(new DataItem(getString(R.string.infobox_gps_waypoint_count), (float)waypointCount));
            textBox.invalidate();
        }

        LinearLayout rightColumn = (LinearLayout)findViewById(R.id.rightColumn);
        if(rightColumn != null)
        {
            // Log.d(TAG, "refreshing right column");
            rightColumn.invalidate();
        }
        else
        {
            Log.e(TAG, "unable to find right column");
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

    private void setRecordButtonState(Boolean recording)
    {
        Button recordingButton = (Button)findViewById(R.id.button_record);
        if(recordingButton != null)
        {
            if(recording)
            {
                recordingButton.setText(getString(R.string.button_stop_record));
                recordingButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stop_record_icon, 0, 0, 0);
            }
            else
            {
                recordingButton.setText(getString(R.string.button_record));
                recordingButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.start_record_icon, 0, 0, 0);
            }
        }
    }

    private void doSendFlight()
    {
        DialogSendDeleteFlight dialog = new DialogSendDeleteFlight();
        dialog.show(this.getSupportFragmentManager(), TAG);

        /*
        try
        {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("file/json");
            startActivityForResult(intent, SELECT_FLIGHT);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
        */
    }

    private void doShowHelp()
    {
        DialogHelp dialog = new DialogHelp();
        dialog.show(this.getSupportFragmentManager(), TAG);
    }
}

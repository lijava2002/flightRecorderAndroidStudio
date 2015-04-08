package com.example.flightrecorder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import android.util.Log;

import android.os.Environment;

public class LogWriter
{
	private static String TAG = "LogWriter";
	private FileWriter _writer = null;
	
	public void openFile(String fileName)
	{
		if(isExternalStorageWritable())
		{
			try
			{
				File dir = new File(Environment.getExternalStorageDirectory(), "/FlightRecorder/"); //Environment.getExternalStorageDirectory();
                dir.mkdir();
				_writer = new FileWriter(new File(dir, fileName), true);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
        else
        {
            try
            {
                //File dir = new File("/root/FlightRecorder/"); //Environment.getExternalStorageDirectory();
                //dir.mkdir();
                //_writer = new FileWriter(new File(dir, fileName), true);

                File dir = Environment.getRootDirectory(); //new File(Environment.getDataDirectory(), "/FlightRecorder/");
                dir.mkdir();
                Log.d(TAG, dir.toString());

                _writer = new FileWriter(new File(dir, fileName), true);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
	}
	
	public void closeFile()
	{
		if(_writer != null)
		{
			try
			{
				_writer.close();
				_writer = null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void writeToFile(String message)
	{
		if(_writer != null)
		{
			try
			{
				_writer.append(message + "\n");
				_writer.flush();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/* Checks if external storage is available for read and write */
	private boolean isExternalStorageWritable()
	{
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state))
	    {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	/*private boolean isExternalStorageReadable()
	{
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
	    {
	        return true;
	    }
	    return false;
	}*/
	
	/*private File getAlbumStorageDir(String albumName)
	{
	    // Get the directory for the user's public pictures directory. 
	    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
	    file.mkdirs();
	    return file;
	}*/
}

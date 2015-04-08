package com.example.flightrecorder.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.example.flightrecorder.Flight;
import com.example.flightrecorder.R;
import com.example.flightrecorder.data.FlightsDataSource;
import com.example.flightrecorder.MailUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

public class DialogSendOrDelete extends DialogFragment
{
    final String TAG = "DialogSendOrDelete";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Activity activity = getActivity();

        final int flightId = getArguments().getInt("flightId");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.dialog_send_or_delete_flight));
        builder.setPositiveButton(getString(R.string.button_send), new OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    onSendClicked(flightId);
                }
            });

        builder.setNegativeButton(getString(R.string.button_delete), new OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    onDeleteClicked(flightId);
                }
            });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    public void onSendClicked(int flightId)
    {
        FlightsDataSource dataSource = new FlightsDataSource(this.getActivity());
        dataSource.open();
        Flight flight = dataSource.getFlight(flightId);
        dataSource.close();

        File file = null;
        Uri uri = null;
        try
        {
            FileOutputStream outputStream;

            try
            {
                String fileName = getString(R.string.file_tmp_file_name) + getString(R.string.file_tmp_file_ending);
                file = new File(getActivity().getFilesDir(), fileName);
                if (file.exists())
                {
                    file.delete();
                }

                file.createNewFile();

                //file.setReadable(true, false);
                FileWriter writer = new FileWriter(file, true);
                writer.append(flight.serialize());
                writer.flush();
                writer.close();

                try
                {
                    MailUtility.sendEmail(getActivity(), getString(R.string.email_address),
                            getString(R.string.mail_subject), getString(R.string.mail_message), file);

                    DialogThanks dialog = new DialogThanks();
                    dialog.show(this.getFragmentManager(), TAG);
                }
                catch(Exception e)
                {
                    Log.e(TAG, e.getMessage());
                }
            }
            catch(Exception e)
            {
                Log.e(TAG, e.getMessage());
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    public void onDeleteClicked(int flightId)
    {
        /*
        FlightsDataSource dataSource = new FlightsDataSource(this.getActivity());
        dataSource.open();
        dataSource.deleteFlight(flightId, true);
        dataSource.close();
        */

        Bundle args = new Bundle();
        args.putInt("flightId", flightId);

        DialogDelete dialog = new DialogDelete();
        dialog.setArguments(args);
        dialog.show(this.getFragmentManager(), TAG);
    }

    public static void createCachedFile(Context context, String fileName, String content) throws IOException
    {
        File cacheFile = new File(context.getCacheDir() + File.separator + fileName);
        cacheFile.createNewFile();

        FileOutputStream fos = new FileOutputStream(cacheFile);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
        PrintWriter pw = new PrintWriter(osw);

        //pw.println(content);

        pw.flush();
        pw.close();
    }
}

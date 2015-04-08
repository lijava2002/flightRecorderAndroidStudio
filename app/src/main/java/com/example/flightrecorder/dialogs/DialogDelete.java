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

public class DialogDelete extends DialogFragment
{
    final String TAG = "DialogDelete";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Activity activity = getActivity();

        final int flightId = getArguments().getInt("flightId");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.dialog_delete));
        builder.setMessage(getString(R.string.dialog_delete_message));
        builder.setPositiveButton(getString(R.string.button_delete), new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                onDeleteClicked(flightId);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.button_cancel), new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    public void onDeleteClicked(int flightId)
    {
        FlightsDataSource dataSource = new FlightsDataSource(this.getActivity());
        dataSource.open();
        dataSource.deleteFlight(flightId, true);
        dataSource.close();
    }
}
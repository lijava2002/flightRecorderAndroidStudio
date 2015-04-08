package com.example.flightrecorder.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

import com.example.flightrecorder.Flight;
import com.example.flightrecorder.R;
import com.example.flightrecorder.data.FlightsDataSource;

import java.util.ArrayList;

public class DialogSendDeleteFlight extends DialogFragment
{
    final String TAG = "DialogSendDeleteFlight";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Activity activity = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.dialog_send_or_delete_flight));

        String[] items = getFlightDateList();
        final int[] flightIds = getFlightIds();

        builder.setItems(items, new OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    onItemSelected(flightIds[which]);
                }
            });

        builder.setPositiveButton(getString(R.string.button_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface d, int id) {
                        d.cancel();
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    public void onItemSelected(int flightId)
    {
        Bundle args = new Bundle();
        args.putInt("flightId", flightId);

        DialogSendOrDelete dialog = new DialogSendOrDelete();
        dialog.setArguments(args);
        dialog.show(this.getFragmentManager(), TAG);
    }

    private String[] getFlightDateList()
    {
        FlightsDataSource dataSource = new FlightsDataSource(this.getActivity());
        dataSource.open();
        ArrayList<Flight> flights = dataSource.getFlights();
        dataSource.close();

        int flightCount = 0;
        for(int i = 0; i < flights.size(); i++)
        {
            if(flights.get(i).getRecording() == false)
            {
                flightCount++;
            }
        }

        String[] dates = new String[flightCount];

        int j = 0;
        for(int i = 0; i < flights.size(); i++)
        {
            if(flights.get(i).getRecording() == false)
            {
                dates[j] = flights.get(i).getDate().toString();
                j++;
            }
        }

        return dates;
    }

    private int[] getFlightIds()
    {
        FlightsDataSource dataSource = new FlightsDataSource(this.getActivity());
        dataSource.open();
        ArrayList<Flight> flights = dataSource.getFlights();
        dataSource.close();

        int[] ids = new int[flights.size()];

        for(int i = 0; i < flights.size(); i++)
        {
            ids[i] = flights.get(i).getId();
        }

        return ids;
    }
}
package com.example.flightrecorder.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;

import com.example.flightrecorder.R;

public class DialogHelp extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Activity activity = getActivity();

        SpannableString recordHeadline = new SpannableString(getString(R.string.dialog_help_record));
        recordHeadline.setSpan(new StyleSpan(Typeface.BOLD), 0, getString(R.string.dialog_help_record).length()-1, 0);

        SpannableString dataHeadline = new SpannableString(getString(R.string.dialog_help_send));
        dataHeadline.setSpan(new RelativeSizeSpan(2.0f), 0, getString(R.string.dialog_help_send).length()-1, 0);

        SpannableString weatherHeadline = new SpannableString(getString(R.string.dialog_help_weather));
        weatherHeadline.setSpan(new RelativeSizeSpan(2.0f), 0, getString(R.string.dialog_help_weather).length()-1, 0);

        Spanned message = Html.fromHtml("<h2>" + recordHeadline + "</h2>"
                + "<b>" + getString(R.string.dialog_help_record_message_0) + "</b><br/>"
                + "-" + getString(R.string.dialog_help_press) + " <b>" + getString(R.string.button_record) + "</b> " + getString(R.string.dialog_help_button) + "<br/>"
                + "<b>" + getString(R.string.dialog_help_record_message_1) + "</b><br/>"
                + "-" + getString(R.string.dialog_help_press) + " <b>" + getString(R.string.button_stop_record) + "</b> " + getString(R.string.dialog_help_button) + "<br/>"
                //+ getString(R.string.dialog_help_record_message_2) + "<br/>"
                + "<h2>" + dataHeadline + "</h2>"
                + "-" + getString(R.string.dialog_help_press) + " <b>" + getString(R.string.button_send_delete_flight) + "</b> " + getString(R.string.dialog_help_button) + "<br/>"
                + "-" + getString(R.string.dialog_help_select) + " " + getString(R.string.dialog_help_send_message_0) + "<br/>"
                + "-" + getString(R.string.dialog_help_press) + " <b>" + getString(R.string.button_send) + "</b> " + getString(R.string.dialog_help_button) + "<br/>"
                + "-" + getString(R.string.dialog_help_send_message_1) + "<br/>"
                + "<h2>" + weatherHeadline + "</h2>"
                + getString(R.string.dialog_help_weather_message_0) + " <b>" + getString(R.string.email_address) + "</b>. " + getString(R.string.dialog_help_weather_message_1));

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.dialog_help));
        /*
        builder.setMessage(recordHeadline + "\n"
                + getString(R.string.dialog_help_record_message_0) + " " + getString(R.string.button_record)
                + " " + getString(R.string.dialog_help_record_message_1) + " " + getString(R.string.button_stop_record)
                + " " + getString(R.string.dialog_help_record_message_2) + "\n"
                + dataHeadline + "\n"
                + getString(R.string.dialog_help_send_message_0) + " " + getString(R.string.button_send_flight)
                + " " + getString(R.string.dialog_help_send_message_1) + " " + getString(R.string.email_address) + "\n"
                + weatherHeadline + "\n"
                + getString(R.string.dialog_help_weather_message));
        */

        builder.setMessage(message);

        builder.setPositiveButton("OK",
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
}

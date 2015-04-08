package com.example.flightrecorder;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.List;

public class MailUtility
{
    static final String TAG = "MailUtility";

    public static void sendEmail(FragmentActivity sender, String receiver, String subject, String message)
    {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{receiver});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        //Intent shareIntent = new Intent();
        //shareIntent.setAction(Intent.ACTION_SEND);
        //shareIntent.setType("application/xml");



        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try
        {
            sender.startActivityForResult(Intent.createChooser(emailIntent, "Send Mail"), 42);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void sendEmail(FragmentActivity sender, String receiver, String subject, String message, String filePath)
    {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{receiver});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" + filePath));

        try
        {
            sender.startActivityForResult(Intent.createChooser(emailIntent, "Send Mail"), 42);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void sendEmail(FragmentActivity sender, String receiver, String subject, String message, Uri fileUri)
    {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{receiver});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);

        try
        {
            sender.startActivityForResult(Intent.createChooser(emailIntent, "Send Mail"), 42);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void sendEmail(FragmentActivity sender, String receiver, String subject, String message, File file)
    {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("application/txt");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{receiver});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        Uri uri = FileProvider.getUriForFile(sender, "com.example.flightrecorder.dialogs.DialogSendOrDelete", file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        // evil hack to manually grant any app permission that can use the data
        List<ResolveInfo> resInfoList = sender.getPackageManager().queryIntentActivities(emailIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList)
        {
            String packageName = resolveInfo.activityInfo.packageName;
            sender.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        try
        {
            sender.startActivityForResult(Intent.createChooser(emailIntent, "Send Mail"), 42);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }
}

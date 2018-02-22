package com.androideasily.olachallenge;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by Anand on 16-12-2017.
 */

/*
* Permission Manager Class Written to manage all the permisison handling process in API 23+
*
*/
class PermissionManager {
    private final Context context;
    private final ModifyPreference storedPreference;

    //Constructor to get the context and also to initialize the sharedpreference handling class
    //Whether the user check dont ask again for the permission is stored in shared perference file to redirect to settings page
    PermissionManager(Context context) {
        this.context = context;
        storedPreference = new ModifyPreference(context);
    }

    //Method which checks whether permissio has been granted or not. API version check is also performed
    int checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return PackageManager.PERMISSION_GRANTED;
    }

    //Funciton To ask for permission
    void getPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            //Showing a dialog to the user so as to why the app needs storage permission
            showDetails();
        else
            //Actual permission requesting method
            performGetPermission();
    }

    //requesting the permission
    private void performGetPermission() {
        //checking whether to request permission or to go to settings page (user checked dont ask again)
        if (storedPreference.checkPreference()) {
            requestSinglePermission();
        } else {
            Intent permissionIntent = new Intent();
            permissionIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            permissionIntent.setData(uri);
            context.startActivity(permissionIntent);
            Toast.makeText(context, "This Application Needs " + android.Manifest.permission.WRITE_EXTERNAL_STORAGE + " Permission", Toast.LENGTH_LONG).show();
        }
    }

    //method to display an alert dialog so as to why the app needs storage permission
    private void showDetails() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Storage Permission");
        builder.setMessage("OLA Play Needs Storage Permission To Save The Downloaded Songs");
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestSinglePermission();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    //requesting the permission
    private void requestSinglePermission() {
        ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
    }

    //updating the shared preference file about the users choice
    private void updatePreference(int requestCode, boolean b) {
        storedPreference.updatePreference(requestCode, b);
    }

    //method which is called in the onRequestPermissionsResult callback.
    //here the permission is validated and true or false returned to the user. updation of the sharedpreference is triggered from here
    boolean updatePermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            updatePreference(requestCode, true);
            return true;
        } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
            //checking whether dont ask again was checked or not
            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissions[0]))
                updatePreference(requestCode, false);
        return false;
    }
}

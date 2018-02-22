package com.androideasily.permissionmanager;

/**
 * Created by Anand on 15-12-2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;

public class PermissionManager {
    private final Context context;
    private final ModifyPreference storedPreference;

    public PermissionManager(Context context) {
        this.context = context;
        storedPreference = new ModifyPreference(context);
    }

    public int checkPermission(String permission) {
        return context.checkSelfPermission(permission);
    }

    public void getPermission(int permissionCode, String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission))
            showDetails(permissionCode, permission);
        else
            performGetPermission(permissionCode, permission);
    }

    public void getPermission(int permissionCode, String permission, String message) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission))
            showDetails(permissionCode, permission, message);
        else
            performGetPermission(permissionCode, permission);
    }

    private void performGetPermission(int permissionCode, String permission) {
        if (storedPreference.checkPreference(permissionCode)) {
            requestSinglePermission(permissionCode, permission);
        } else {
            Intent permissionIntent = new Intent();
            permissionIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            permissionIntent.setData(uri);
            context.startActivity(permissionIntent);
            Toast.makeText(context, "This Application Needs " + permission + " Permission", Toast.LENGTH_LONG).show();
        }
    }

    private void showDetails(final int permissionCode, final String permission, final String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(permission);
        builder.setMessage(message);
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestSinglePermission(permissionCode, permission);
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

    private void showDetails(final int permissionCode, final String permission) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(permission);
        builder.setMessage("Certain modules of this applicaiton needs this permission to work!");
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestSinglePermission(permissionCode, permission);
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

    private void requestSinglePermission(int permissionCode, String permission) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, permissionCode);
    }

    private void updatePreference(int requestCode, boolean b) {
        storedPreference.updatePreference(requestCode, b);
    }

    public void requestGroupPermission(int groupRequestCode, String[] permissions) {
        ArrayList<String> permissionNeeded = new ArrayList<>();
        int permissionId = 0;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED && storedPreference.checkPreference(permissionId))
                permissionNeeded.add(permission);
            permissionId++;
        }
        String[] groupRequest = new String[permissionNeeded.size()];
        permissionNeeded.toArray(groupRequest);
        if (groupRequest.length == 0)
            Toast.makeText(context, "No Permission Can Be Requested In Group", Toast.LENGTH_SHORT).show();
        else
            ActivityCompat.requestPermissions((Activity) context, groupRequest, groupRequestCode);
    }

    public boolean updatePermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            updatePreference(requestCode, true);
            return true;
        } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissions[0]))
                updatePreference(requestCode, false);
        return false;
    }
}

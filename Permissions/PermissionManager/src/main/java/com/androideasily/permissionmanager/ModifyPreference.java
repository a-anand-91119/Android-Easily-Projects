package com.androideasily.permissionmanager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Anand on 15-12-2017.
 */

public class ModifyPreference {
    private final SharedPreferences sharedPreferences;

    ModifyPreference(Context context) {
        sharedPreferences = context.getSharedPreferences("PERMISSION_STATUS", Context.MODE_PRIVATE);
    }
    boolean checkPreference(int permission) {
        return sharedPreferences.getBoolean(String.valueOf(permission), true);
    }
    void updatePreference(int permission, boolean permissionStatus) {
        SharedPreferences.Editor sharedPreferenceEditor = sharedPreferences.edit();
        sharedPreferenceEditor.putBoolean(String.valueOf(permission), permissionStatus);
        sharedPreferenceEditor.apply();
    }
}

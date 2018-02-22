package com.androideasily.olachallenge;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Anand on 16-12-2017.
 */

//Shared preference modification class
class ModifyPreference {
    private final SharedPreferences sharedPreferences;

    ModifyPreference(Context context) {
        sharedPreferences = context.getSharedPreferences("PERMISSION_STATUS", Context.MODE_PRIVATE);
    }

    /*
    * Checkign the permission status.
    * */
    boolean checkPreference() {
        return sharedPreferences.getBoolean(String.valueOf(200), true);
    }

    void updatePreference(int permission, boolean permissionStatus) {
        SharedPreferences.Editor sharedPreferenceEditor = sharedPreferences.edit();
        sharedPreferenceEditor.putBoolean(String.valueOf(permission), permissionStatus);
        sharedPreferenceEditor.apply();
    }
}

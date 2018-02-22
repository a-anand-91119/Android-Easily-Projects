package com.androideasily.olachallenge;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Anand on 16-12-2017.
 */

//Shared preference modification class
public class FavouritePreference {
    private final SharedPreferences sharedPreferences;

    FavouritePreference(Context context) {
        sharedPreferences = context.getSharedPreferences("FAVOURITES", Context.MODE_PRIVATE);
    }

    //checking the status of song.
    //true- user has made it favourite
    //false - not favourite
    boolean checkPreference(String name) {
        return sharedPreferences.getBoolean(name, false);
    }

    //updating the permission
    void updatePreference(String name, boolean permissionStatus) {
        SharedPreferences.Editor sharedPreferenceEditor = sharedPreferences.edit();
        sharedPreferenceEditor.putBoolean(name, permissionStatus);
        sharedPreferenceEditor.apply();
    }

}

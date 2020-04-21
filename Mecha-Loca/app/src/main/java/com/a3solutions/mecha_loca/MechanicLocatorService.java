package com.a3solutions.mecha_loca;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Anand on 31-05-2017.
 */

public class MechanicLocatorService extends Service {

    DatabaseReference dbLatitude, dbLongitude, customerNode;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    LocationListener locationListener;
    LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("SERVICE LIFE CYCLE", "INSIDE onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("SERVICE LIFE CYCLE", "INSIDE onStartCommand");
        customerNode = databaseReference.child(intent.getStringExtra("user_id"));
        Log.e("LOCATION SERVICE", "user_id: " + intent.getStringExtra("user_id"));
        dbLatitude = customerNode.child("latitude");
        dbLongitude = customerNode.child("longitude");
        dbLatitude.setValue(0.0);
        dbLongitude.setValue(0.0);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e("SERVICE LIFE CYCLE", "INSIDE onCreate");
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("SERVICE LIFE CYCLE", "INSIDE onLocationChanged");
                updateDB(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
        super.onCreate();
    }
    private void updateDB(Location location){
        Log.e("SERVICE LIFE CYCLE","INSIDE updateDB");
        dbLatitude.setValue(location.getLatitude());
        dbLongitude.setValue(location.getLongitude());
    }

    @Override
    public void onDestroy() {
        Log.e("STATUS SERVICE","onDestroy");
        SharedPreferences.Editor writeStatus;
        writeStatus = getSharedPreferences("SERVICING", Context.MODE_PRIVATE).edit();
        writeStatus.putBoolean("servicing", false);
        writeStatus.putBoolean("alreadyServicing", false);
        writeStatus.apply();
        locationManager.removeUpdates(locationListener);
        customerNode.setValue(null);
        stopSelf();
        super.onDestroy();
    }
}

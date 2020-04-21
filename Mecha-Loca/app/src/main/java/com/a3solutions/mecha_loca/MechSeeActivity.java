package com.a3solutions.mecha_loca;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MechSeeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    Double latitude, longitude;
    String user_id;
    Toolbar trackToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        latitude = Double.valueOf(getIntent().getStringExtra("latitude"));
        longitude = Double.valueOf(getIntent().getStringExtra("longitude"));
        user_id = getIntent().getStringExtra("user_id");
        trackToolbar = (Toolbar) findViewById(R.id.tool_bar2);
        trackToolbar.setTitle("Customer Location");
        setSupportActionBar(trackToolbar);
        trackToolbar.setTitleTextColor(Color.WHITE);
        if (googleServicesAvailable()) {
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
            mapFragment.getMapAsync(MechSeeActivity.this);
            Intent startTracking = new Intent("com.a3solutions.mecha_loca.MechanicLocatorService");
            startTracking.putExtra("user_id", user_id);
            startTracking.setPackage(MechSeeActivity.this.getPackageName());
            MechSeeActivity.this.startService(startTracking);
        }
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int isAvailable = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS)
            return true;
        else if (googleApiAvailability.isUserResolvableError(isAvailable)) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else
            Toast.makeText(this, "Couldn't Connect To Google Play Services", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void displayUser(Double latitude, Double longitude) {
        LatLng userLatlng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLatlng, 15);
        mGoogleMap.moveCamera(cameraUpdate);
        MarkerOptions userMarker = new MarkerOptions().title("You").position(userLatlng);
        mGoogleMap.addMarker(userMarker);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        displayUser(latitude, longitude);
    }
}

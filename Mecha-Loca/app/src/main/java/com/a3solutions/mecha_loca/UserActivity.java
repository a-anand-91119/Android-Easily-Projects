package com.a3solutions.mecha_loca;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class UserActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Double latitude = null, longitude = null, latitudeMech = null, longitudeMech = null;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private MechanicConfig mechanicConfig;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRoot = firebaseDatabase.getReference();
    private DatabaseReference trackNode, latNode, lonNode;
    private ValueEventListener valueEventListenerTrackNode, valueEventListenerLatNode, valueEventListenerLonNode;
    private RecyclerView recyclerView;
    private Marker marker;

    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog progressDialog;
    private GoogleMap mGoogleMap;
    private RecyclerView.Adapter adapter;
    String user_id, mechanicName, mech_id, mechanicMobile;
    Toolbar trackToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(getIntent().getStringExtra("name"));
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setTitle(getIntent().getStringExtra("name"));
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        initialize();
        user_id = getIntent().getStringExtra("user_id");
        checkRequest();
    }

    private void initialize() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        progressDialog = new ProgressDialog(UserActivity.this);
    }

    private void checkRequest() {
        progressDialog.setMessage("Checking For Existing Requests");
        progressDialog.show();
        CheckRequest check = new CheckRequest(user_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RESPONSE", response);
                progressDialog.dismiss();
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("present")) {
                        findViewById(R.id.button2).setVisibility(View.INVISIBLE);
                        if (googleServicesAvailable()) {
                            setContentView(R.layout.activity_track);
                            trackToolbar = (Toolbar) findViewById(R.id.tool_bar2);
                            trackToolbar.setTitle(getIntent().getStringExtra("name"));
                            setSupportActionBar(trackToolbar);
                            trackToolbar.setTitleTextColor(Color.WHITE);
                            mech_id = jsonObject.getString("mech_id");
                            setData(mech_id);
                        }
                    } else {
                        Toast.makeText(UserActivity.this, "No Pending Requests", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Toast.makeText(UserActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(UserActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(UserActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(UserActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }
            }
        });
        RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
        requestQueue.add(check);
    }

    private void setData(String mech_id) {
        GetMechanicRequest getMechanicRequest = new GetMechanicRequest(mech_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RESPONSE FETCH MECH", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("success")) {
                        Toast.makeText(UserActivity.this, "Error While Fetching Mechanic Data", Toast.LENGTH_SHORT).show();
                    } else {
                        mechanicName = jsonObject.getString("name");
                        mechanicMobile = jsonObject.getString("mobile");

                        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
                        mapFragment.getMapAsync(UserActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Toast.makeText(UserActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(UserActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(UserActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(UserActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }
            }
        });
        RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
        requestQueue.add(getMechanicRequest);
    }

    public void fab_clicked(View view) {
        fetchMechanic();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        if (valueEventListenerLatNode != null & valueEventListenerLonNode != null && valueEventListenerTrackNode != null) {
            trackNode.removeEventListener(valueEventListenerTrackNode);
            latNode.removeEventListener(valueEventListenerLatNode);
            lonNode.removeEventListener(valueEventListenerLonNode);
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            startLocationUpdates();
        }
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else {
            Toast.makeText(this, "Have You  Turned On GPS?", Toast.LENGTH_SHORT).show();
        }
        Log.i("latitude", String.valueOf(latitude));
        Log.i("longitude", String.valueOf(longitude));
    }

    protected void startLocationUpdates() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("LOCATION", "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    private void parseJSON(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int length = jsonObject.length();
            mechanicConfig = new MechanicConfig(length - 1);
            for (int i = 0; i < length - 1; i++) {
                JSONObject j = jsonObject.getJSONObject(String.valueOf(i));
                MechanicConfig.id[i] = getId(j);
                MechanicConfig.distance[i] = getDistance(j);
                MechanicConfig.latitude[i] = getLatitude(j);
                MechanicConfig.longitude[i] = getLongitude(j);
                MechanicConfig.rating[i] = getRating(j);
                MechanicConfig.mechanicName[i] = getName(j);
                MechanicConfig.mechId[i] = getMechId(j);
                MechanicConfig.nrating[i] = getNrating(j);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getId(JSONObject jsonObject) {
        String id = null;
        try {
            id = jsonObject.getString(MechanicConfig.ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }

    private String getNrating(JSONObject jsonObject) {
        String nrating = null;
        try {
            nrating = jsonObject.getString(MechanicConfig.NRATING);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nrating;
    }

    private String getRating(JSONObject jsonObject) {
        String rating = null;
        try {
            rating = jsonObject.getString(MechanicConfig.RATING);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rating;
    }

    private String getName(JSONObject jsonObject) {
        String name = null;
        try {
            name = jsonObject.getString(MechanicConfig.MECHANIC_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    private String getLatitude(JSONObject jsonObject) {
        String latitude = null;
        try {
            latitude = jsonObject.getString(MechanicConfig.LATITUDE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return latitude;
    }

    private String getLongitude(JSONObject jsonObject) {
        String longitude = null;
        try {
            longitude = jsonObject.getString(MechanicConfig.LONGITUDE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return longitude;
    }

    private String getDistance(JSONObject jsonObject) {
        String distance = null;
        try {
            distance = jsonObject.getString(MechanicConfig.DISTANCE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return distance;
    }

    private String getMechId(JSONObject jsonObject) {
        String mechId = null;
        try {
            mechId = jsonObject.getString(MechanicConfig.MECHID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mechId;
    }

    public void showData() {
        adapter = new CardAdapter(MechanicConfig.nrating, MechanicConfig.rating, latitude, longitude, user_id, MechanicConfig.mechId, MechanicConfig.id, MechanicConfig.distance, MechanicConfig.mechanicName, MechanicConfig.longitude, MechanicConfig.latitude, getIntent().getStringExtra("name"), UserActivity.this);
        recyclerView.setAdapter(adapter);
        progressDialog.hide();
        progressDialog.dismiss();
    }

    private void fetchMechanic() {
        if (validateCoordinates()) {
            progressDialog.setMessage("Looking For Nearby Mechanics");
            progressDialog.setCancelable(false);
            progressDialog.show();
            FetchMechanic fetchMechanicRequest = new FetchMechanic(String.valueOf(latitude), String.valueOf(longitude), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            parseJSON(response);
                            showData();
                        } else {
                            progressDialog.hide();
                            progressDialog.dismiss();
                            Toast.makeText(UserActivity.this, "No Mechanics Found!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.hide();
                    progressDialog.dismiss();
                    Log.e("VOLLEY ERROR CUSTOM", error.toString());
                    if (error instanceof TimeoutError) {
                        Toast.makeText(UserActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(UserActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(UserActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(UserActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
            requestQueue.add(fetchMechanicRequest);
        }
    }

    private boolean validateCoordinates() {
        if (latitude == null || longitude == null) {
            Toast.makeText(this, "Error In Geting Location", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        trackNode = mRoot.child(user_id);
        latNode = trackNode.child("latitude");
        lonNode = trackNode.child("longitude");
        valueEventListenerTrackNode = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    if (marker != null) {
                        AlertDialog.Builder ratingDialog = new AlertDialog.Builder(UserActivity.this);
                        ratingDialog.setTitle("Would You Like To Rate The Mechanic?");
                        LayoutInflater layoutInflater = getLayoutInflater();
                        View ratingView = layoutInflater.inflate(R.layout.rating_layout, null);
                        ratingDialog.setView(ratingView);
                        final RatingBar ratingBar = (RatingBar) ratingView.findViewById(R.id.ratingBar);
                        ratingDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog progress = new ProgressDialog(UserActivity.this);
                                progress.setCancelable(false);
                                progress.setMessage("Submitting Rating");
                                progress.show();
                                RatingRequest ratingRequest = new RatingRequest(mech_id, String.valueOf(ratingBar.getRating()), new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        progress.dismiss();
                                        progress.hide();
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            if (jsonObject.getBoolean("success")) {
                                                Toast.makeText(UserActivity.this, "Successfully Rated", Toast.LENGTH_SHORT).show();
                                                Intent intent = getIntent();
                                                finish();
                                                startActivity(intent);
                                            } else {
                                                progress.dismiss();
                                                progress.hide();
                                                Toast.makeText(UserActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progress.dismiss();
                                        progress.hide();
                                        if (error instanceof TimeoutError) {
                                            Toast.makeText(UserActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                                        } else if (error instanceof NetworkError) {
                                            Toast.makeText(UserActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                                        } else if (error instanceof NoConnectionError) {
                                            Toast.makeText(UserActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                                        } else if (error instanceof ServerError) {
                                            Toast.makeText(UserActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
                                requestQueue.add(ratingRequest);
                            }
                        });
                        ratingDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        });
                        ratingDialog.create().show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        trackNode.addValueEventListener(valueEventListenerTrackNode);
        valueEventListenerLatNode = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    latitudeMech = Double.parseDouble(dataSnapshot.getValue().toString());
                    if (longitudeMech != null)
                        displayMechanic(latitudeMech, longitudeMech, 15);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        latNode.addValueEventListener(valueEventListenerLatNode);
        valueEventListenerLonNode = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    longitudeMech = Double.parseDouble(dataSnapshot.getValue().toString());
                    if (latitudeMech != null)
                        displayMechanic(latitudeMech, longitudeMech, 15);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        lonNode.addValueEventListener(valueEventListenerLonNode);
        displayUser(latitude, longitude);
    }

    private void displayUser(Double latitude, Double longitude) {
        LatLng userLatlng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLatlng, 15);
        mGoogleMap.moveCamera(cameraUpdate);
        MarkerOptions userMarker = new MarkerOptions().title("You").position(userLatlng);
        mGoogleMap.addMarker(userMarker);
    }

    private void displayMechanic(Double latitude, Double longitude, float zoom) {
        LatLng latlong = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlong, zoom);
        mGoogleMap.animateCamera(cameraUpdate);
        if (marker != null)
            marker.remove();
        MarkerOptions markerOptions = new MarkerOptions().title(mechanicName).position(new LatLng(latitudeMech, longitudeMech)).snippet(mechanicMobile)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mechanic_marker));
        marker = mGoogleMap.addMarker(markerOptions);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            SharedPreferences.Editor logout = getSharedPreferences("AUTOIN", Context.MODE_PRIVATE).edit();
            logout.clear().apply();
            Intent intent = new Intent(UserActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
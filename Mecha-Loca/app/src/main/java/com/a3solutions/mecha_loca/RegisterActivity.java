package com.a3solutions.mecha_loca;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    TextInputLayout til_username, til_password, til_confirm_password, til_name, til_email, til_mobile;
    EditText et_username, et_password, et_confirm_password, et_name, et_email, et_mobile;
    Button bt_register;
    Double latitude = null, longitude = null;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    CheckBox cb_user, cb_mechanic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialize();
        cb_user.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_mechanic.setChecked(false);
                }
            }
        });
        cb_mechanic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_user.setChecked(false);
                }
            }
        });
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Registering...");
                progressDialog.show();
                RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
                if (cb_mechanic.isChecked()) {
                    if (validateUserName() && validatePassword() && validateConfirmPassword() & validateEmail() && validateName() && validateMobile() && checkCoordinates()) {
                        Response.Listener<String> mechanicResponse = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("RESPNSE", response);
                                progressDialog.dismiss();
                                progressDialog.hide();
                                if (response.equals("Mechanic Registered Successfully")) {
                                    Toast.makeText(RegisterActivity.this, "Mechanic Registered Successfully!!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Username Already Exist!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        };
                        MechanicRequest mechanicRequest = new MechanicRequest(et_name.getText().toString(),
                                et_username.getText().toString(),
                                et_password.getText().toString(),
                                et_email.getText().toString(),
                                UUID.randomUUID(),
                                latitude,
                                longitude,
                                et_mobile.getText().toString(),
                                mechanicResponse);
                        requestQueue.add(mechanicRequest);
                    } else {
                        progressDialog.dismiss();
                        progressDialog.hide();
                    }
                } else if (cb_user.isChecked()) {
                    if (validateUserName() && validateName() && validatePassword() & validateConfirmPassword() && validateEmail() && validateMobile()) {
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("response", response);
                                progressDialog.dismiss();
                                progressDialog.hide();
                                if (response.equals("User Registered Successfully")) {
                                    Toast.makeText(RegisterActivity.this, "User Registered Successfully!!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Username Already Exist!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        };
                        Log.i("Mobile Number", et_mobile.getText().toString());
                        RegisterRequest registerRequest = new RegisterRequest(et_name.getText().toString(),
                                et_username.getText().toString(),
                                et_password.getText().toString(),
                                et_email.getText().toString(),
                                UUID.randomUUID(),
                                et_mobile.getText().toString(),
                                responseListener);
                        requestQueue.add(registerRequest);
                    } else {
                        progressDialog.dismiss();
                        progressDialog.hide();
                    }
                } else {
                    progressDialog.dismiss();
                    progressDialog.hide();
                    Toast.makeText(RegisterActivity.this, "Please Select Whether User or Mechanic", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkCoordinates() {
        if (latitude == null || longitude == null) {
            Toast.makeText(this, "Error In Getting Location!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initialize() {
        et_username = (EditText) findViewById(R.id.et_reg_username);
        til_mobile = (TextInputLayout) findViewById(R.id.til_phone_reg);
        et_mobile = (EditText) findViewById(R.id.et_reg_phone);
        et_name = (EditText) findViewById(R.id.et_reg_name);
        et_password = (EditText) findViewById(R.id.et_reg_password);
        et_confirm_password = (EditText) findViewById(R.id.et_reg_confirmpassword);
        et_email = (EditText) findViewById(R.id.et_reg_email);
        til_username = (TextInputLayout) findViewById(R.id.til_username_reg);
        cb_user = (CheckBox) findViewById(R.id.checkBox4);
        cb_mechanic = (CheckBox) findViewById(R.id.checkBox);
        til_name = (TextInputLayout) findViewById(R.id.til_name_reg);
        til_password = (TextInputLayout) findViewById(R.id.til_password_reg);
        til_confirm_password = (TextInputLayout) findViewById(R.id.til_password_confirm_reg);
        til_email = (TextInputLayout) findViewById(R.id.til_email_reg);
        bt_register = (Button) findViewById(R.id.bt_signup);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateEmail() {
        if (et_email.getText().toString().trim().isEmpty()) {
            til_email.setError("Enter Your Email Address");
            requestFocus(et_email);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString()).matches()) {
            til_email.setError("Enter A Valid Email Address");
            requestFocus(et_email);
            return false;
        } else if (et_email.getText().toString().length() > 50) {
            til_email.setError("Email Exceeds Limit Of 50 Characters");
            requestFocus(et_email);
            return false;
        } else {
            til_email.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateConfirmPassword() {
        if (et_confirm_password.getText().toString().trim().isEmpty()) {
            til_confirm_password.setError("Retype Your Password");
            requestFocus(et_confirm_password);
            return false;
        } else if (!et_confirm_password.getText().toString().trim().equals(et_password.getText().toString().trim())) {
            til_confirm_password.setError("Password Do Not Match");
            til_password.setError("Password Do Not Match");
            requestFocus(et_confirm_password);
            requestFocus(et_password);
            return false;
        } else {
            til_confirm_password.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (et_password.getText().toString().trim().isEmpty()) {
            til_password.setError("Enter A Password");
            requestFocus(et_password);
            return false;
        } else if (et_password.getText().toString().trim().length() < 8) {
            til_password.setError("Password Must Contain Minimum Of 8 Characters");
            requestFocus(et_password);
            return false;
        } else if (et_password.getText().toString().trim().length() > 32) {
            til_password.setError("Password Exceeds Limit Of 32 Characters");
            requestFocus(et_password);
            return false;
        } else {
            til_password.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateName() {
        if (et_name.getText().toString().trim().isEmpty()) {
            til_name.setError("Enter Your Full Name");
            requestFocus(et_name);
            return false;
        } else if (et_name.getText().toString().trim().length() > 50) {
            til_name.setError("Full Name Exceeds Limit Of 50 Characters");
            requestFocus(et_name);
            return false;
        } else {
            til_name.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateUserName() {
        if (et_username.getText().toString().trim().isEmpty()) {
            til_username.setError("Enter Your Username");
            requestFocus(et_username);
            return false;
        } else if (et_username.getText().toString().trim().length() > 16) {
            til_username.setError("Username Exceeds Limit Of 16 Characters");
            requestFocus(et_username);
            return false;
        } else {
            til_username.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateMobile() {
        if (et_mobile.getText().toString().trim().isEmpty()) {
            til_mobile.setError("Enter Your Mobile Number");
            requestFocus(et_username);
            return false;
        } else if (!android.util.Patterns.PHONE.matcher(et_mobile.getText().toString()).matches()) {
            til_mobile.setError("Enter A Valid Mobile Number");
            requestFocus(et_username);
            return false;
        } else {
            til_username.setErrorEnabled(false);
        }
        return true;
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
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
}

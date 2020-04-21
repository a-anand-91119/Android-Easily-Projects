package com.a3solutions.mecha_loca;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {


    String username, password;
    EditText et_username, et_password;
    TextInputLayout til_username, til_password;
    Button bt_login;
    CheckBox cb_user, cb_mechanic;


    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        til_username = (TextInputLayout) findViewById(R.id.til_username);
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        et_username = (EditText) findViewById(R.id.et_username);
        cb_user = (CheckBox) findViewById(R.id.checkBox2);
        cb_mechanic = (CheckBox) findViewById(R.id.checkBox3);
        et_password = (EditText) findViewById(R.id.et_password);
        bt_login = (Button) findViewById(R.id.bt_login);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        checkPermissions();
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
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = et_username.getText().toString();
                password = et_password.getText().toString();
                if (validateUserName() && validatePassword()) {
                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Logging In");
                    progressDialog.show();
                    RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
                    if (cb_mechanic.isChecked()) {
                        Response.Listener<String> mechanicResponse = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("RESPONSE", response);
                                progressDialog.dismiss();
                                progressDialog.hide();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getBoolean("success")) {
                                        SharedPreferences.Editor saveData = getSharedPreferences("AUTOIN", Context.MODE_PRIVATE).edit();
                                        saveData.putBoolean("already", true);
                                        saveData.putBoolean("mech", true);
                                        saveData.putString("username", et_username.getText().toString());
                                        saveData.putString("password", et_password.getText().toString());
                                        saveData.putString("mech_id", jsonObject.getString("mech_id"));
                                        saveData.putString("name", jsonObject.getString("name"));
                                        saveData.apply();
                                        Intent intent = new Intent(LoginActivity.this, MechanicActivity.class);
                                        intent.putExtra("mech_id", jsonObject.getString("mech_id"));
                                        intent.putExtra("name", jsonObject.getString("name"));
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Mechanic Not Found!!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        MechanicLoginRequest mechanicLoginRequest = new MechanicLoginRequest(et_username.getText().toString(),
                                et_password.getText().toString(),
                                mechanicResponse, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                progressDialog.hide();
                                if (error instanceof TimeoutError) {
                                    Toast.makeText(LoginActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                                } else if (error instanceof NetworkError) {
                                    Toast.makeText(LoginActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                                } else if (error instanceof NoConnectionError) {
                                    Toast.makeText(LoginActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                                } else if (error instanceof ServerError) {
                                    Toast.makeText(LoginActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        requestQueue.add(mechanicLoginRequest);
                    } else if (cb_user.isChecked()) {
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("RESPONSE", response);
                                progressDialog.dismiss();
                                progressDialog.hide();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getBoolean("success")) {
                                        SharedPreferences.Editor saveData = getSharedPreferences("AUTOIN", Context.MODE_PRIVATE).edit();
                                        saveData.putBoolean("already", true);
                                        saveData.putBoolean("mech", false);
                                        saveData.putString("username", et_username.getText().toString());
                                        saveData.putString("password", et_password.getText().toString());
                                        saveData.putString("user_id", jsonObject.getString("user_id"));
                                        saveData.putString("name", jsonObject.getString("name"));
                                        saveData.apply();
                                        Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                                        intent.putExtra("name", jsonObject.getString("name"));
                                        intent.putExtra("user_id", jsonObject.getString("user_id"));
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "User Not Found!!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                progressDialog.hide();
                                if (error instanceof TimeoutError) {
                                    Toast.makeText(LoginActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                                } else if (error instanceof NetworkError) {
                                    Toast.makeText(LoginActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                                } else if (error instanceof NoConnectionError) {
                                    Toast.makeText(LoginActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                                } else if (error instanceof ServerError) {
                                    Toast.makeText(LoginActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                                }
                            }
                        };
                        LoginRequest loginRequest = new LoginRequest(username, password, responseListener, errorListener);
                        requestQueue.add(loginRequest);
                    } else {
                        progressDialog.dismiss();
                        progressDialog.hide();
                        Toast.makeText(LoginActivity.this, "Please Select Whether User or Mechanic", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(LoginActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(LoginActivity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(LoginActivity.this, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[3])) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("Mecha-Loca needs Storage and Location Permissions to get your location and track mechanic locations in Real-Time");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(LoginActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("Mecha-Loca needs Storage and Location Permissions to get your location and track mechanic locations in Real-Time");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage and Location", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                ActivityCompat.requestPermissions(LoginActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.apply();
        } else {
            proceedAfterPermission();
        }
    }

    private boolean validatePassword() {
        if (et_password.getText().toString().trim().isEmpty()) {
            til_password.setError("Enter Your Password");
            requestFocus(et_password);
            return false;
        } else if (et_password.getText().toString().trim().length() < 8) {
            til_password.setError("Password Too Short, Enter Atleast 8 Characters");
            requestFocus(et_password);
            return false;
        } else if (et_password.getText().toString().trim().length() > 32) {
            til_password.setError("Password Too Long, Maximum Length Is 32");
            requestFocus(et_password);
            return false;
        } else {
            til_password.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateUserName() {
        if (et_username.getText().toString().trim().isEmpty()) {
            til_username.setError("Enter Your Username");
            requestFocus(et_username);
            return false;
        } else {
            til_username.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void register_clicked(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[3])) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("Mecha-Loca needs Storage and Location Permissions to get your location and track mechanic locations in Real-Time");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(LoginActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission. Exiting", Toast.LENGTH_LONG).show();
                finish();
                System.exit(0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(LoginActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                proceedAfterPermission();
            }
        }
    }

    private void proceedAfterPermission() {
        Toast.makeText(getBaseContext(), "All Permissions Granted", Toast.LENGTH_LONG).show();
        SharedPreferences sharedPreference = getSharedPreferences("AUTOIN", Context.MODE_PRIVATE);
        if (sharedPreference != null) {
            if (sharedPreference.getBoolean("already", false)) {
                if (sharedPreference.getBoolean("mech", false)) {
                    Intent intent = new Intent(LoginActivity.this, MechanicActivity.class);
                    intent.putExtra("mech_id", sharedPreference.getString("mech_id", ""));
                    intent.putExtra("name", sharedPreference.getString("name", ""));
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                    intent.putExtra("name", sharedPreference.getString("name", ""));
                    intent.putExtra("user_id", sharedPreference.getString("user_id", ""));
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(LoginActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                proceedAfterPermission();
            }
        }
    }
}

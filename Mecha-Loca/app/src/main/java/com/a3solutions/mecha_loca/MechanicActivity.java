package com.a3solutions.mecha_loca;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MechanicActivity extends AppCompatActivity {

    String mech_id;
    ArrayList<String> user_id, latitude, longitude, name, mobile, location;
    ListView listView;
    private boolean servicingStatus = false;
    private SharedPreferences.Editor writeStatus;
    MechanicList mechanicList;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(getIntent().getStringExtra("name"));
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        mech_id = getIntent().getStringExtra("mech_id");
        listView = (ListView) findViewById(R.id.listview);

        sharedPreferences = getSharedPreferences("SERVICING", Context.MODE_PRIVATE);
        writeStatus = getSharedPreferences("SERVICING", Context.MODE_PRIVATE).edit();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                servicingStatus = sharedPreferences.getBoolean("servicing", false);
                if (servicingStatus) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MechanicActivity.this);
                    builder.setTitle("Finished Servicing Request?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final ProgressDialog progressDialog = new ProgressDialog(MechanicActivity.this);
                            progressDialog.setMessage("Please Wait");
                            progressDialog.show();
                            DeleteRequest deleteRequest = new DeleteRequest(user_id.get(position), mech_id, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.e("CANCEL RESPONSE", response);
                                    progressDialog.dismiss();
                                    progressDialog.hide();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (jsonObject.getBoolean("success")) {
                                            writeStatus.putBoolean("servicing", false);
                                            writeStatus.apply();
                                            Intent stopTracking = new Intent(getApplicationContext(), MechanicLocatorService.class);
                                            stopService(stopTracking);
                                            mechanicList.remove(position);
                                            mechanicList.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(MechanicActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        Log.e("CANCEL RESPONSE", response);
                                        e.printStackTrace();
                                    }
                                }
                            });
                            RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
                            requestQueue.add(deleteRequest);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MechanicActivity.this);
                    builder.setTitle("Do You Want To Service This Customer?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            writeStatus.putBoolean("servicing", true);
                            writeStatus.apply();
                            Intent startTracking = new Intent("com.a3solutions.mecha_loca.MechanicLocatorService");
                            startTracking.putExtra("user_id", user_id.get(position));
                            startTracking.setPackage(MechanicActivity.this.getPackageName());
                            MechanicActivity.this.startService(startTracking);
                            Intent intent = new Intent(MechanicActivity.this, MechSeeActivity.class);
                            intent.putExtra("latitude", latitude.get(position));
                            intent.putExtra("longitude", longitude.get(position));
                            intent.putExtra("user_id", user_id.get(position));
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CancelRequest cancelRequest = new CancelRequest(user_id.get(position), mech_id, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.i("RESPONSE",response);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if(jsonObject.getBoolean("success")){
                                            Toast.makeText(MechanicActivity.this, "Reject Success", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(MechanicActivity.this, "Failed. Please Try Again!!", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if (error instanceof TimeoutError) {
                                        Toast.makeText(MechanicActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                                    } else if (error instanceof NetworkError) {
                                        Toast.makeText(MechanicActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                                    } else if (error instanceof NoConnectionError) {
                                        Toast.makeText(MechanicActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                                    } else if (error instanceof ServerError) {
                                        Toast.makeText(MechanicActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
                            requestQueue.add(cancelRequest);
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            }
        });
        checkDB();
    }

    private void checkDB() {
        final ProgressDialog progressDialog = new ProgressDialog(MechanicActivity.this);
        progressDialog.setMessage("Checking For Any Requests");
        progressDialog.setCancelable(false);
        progressDialog.show();
        CheckRequest2 check = new CheckRequest2(mech_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RESPONSE", response);
                progressDialog.dismiss();
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("present")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("row");
                        Log.e("LENGTH", String.valueOf(jsonArray.length()));
                        if (jsonArray.length() < 0) {
                            Toast.makeText(MechanicActivity.this, "No Pending Requests", Toast.LENGTH_SHORT).show();
                            findViewById(R.id.textView3).setVisibility(View.INVISIBLE);
                        } else {
                            findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                            latitude = new ArrayList<String>(jsonArray.length());
                            longitude = new ArrayList<String>(jsonArray.length());
                            user_id = new ArrayList<String>(jsonArray.length());
                            name = new ArrayList<String>(jsonArray.length());
                            mobile = new ArrayList<String>(jsonArray.length());
                            location = new ArrayList<String>(jsonArray.length());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                user_id.add(i, jsonObject1.getString("user_id"));
                                latitude.add(i, jsonObject1.getString("latitude"));
                                longitude.add(i, jsonObject1.getString("longitude"));
                                name.add(i, jsonObject1.getString("name"));
                                mobile.add(i, jsonObject1.getString("mobile"));
                                location.add(i, jsonObject1.getString("location"));
                            }
                            mechanicList = new MechanicList(MechanicActivity.this, user_id, latitude, longitude, name, mobile, location);
                            listView.setAdapter(mechanicList);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Toast.makeText(MechanicActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(MechanicActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(MechanicActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(MechanicActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                }
            }
        });
        RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
        requestQueue.add(check);
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
            Intent intent = new Intent(MechanicActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
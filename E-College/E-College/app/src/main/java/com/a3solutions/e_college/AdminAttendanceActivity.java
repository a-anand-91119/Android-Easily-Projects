package com.a3solutions.e_college;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AdminAttendanceActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    Spinner classDetails;
    RequestQueue requestQueue;
    ArrayAdapter<String> arrayAdapter;
    ExpandableAdapter expandableAdapter;
    String[] list;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_attendance);

        classDetails = (Spinner) findViewById(R.id.spinner3);
        expandableListView = (ExpandableListView) findViewById(R.id.admin_list);
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
        final RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
        final ProgressDialog progressDialog = new ProgressDialog(AdminAttendanceActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        ClassFetchRequest classFetchRequest = new ClassFetchRequest(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                progressDialog.hide();
                Log.e("RESPONSE", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("class");
                        list = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            list[i] = jsonObject1.getString("class");
                        }
                        arrayAdapter = new ArrayAdapter<String>(AdminAttendanceActivity.this, R.layout.support_simple_spinner_dropdown_item, list);
                        classDetails.setAdapter(arrayAdapter);
                    } else {
                        Toast.makeText(AdminAttendanceActivity.this, "Error In Fetching Class Details", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                progressDialog.hide();
                if (error instanceof TimeoutError) {
                    Toast.makeText(AdminAttendanceActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(AdminAttendanceActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(AdminAttendanceActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestQueue.add(classFetchRequest);
        classDetails.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
                final ProgressDialog progressDialog = new ProgressDialog(AdminAttendanceActivity.this);
                progressDialog.setMessage("Fetching Data");
                progressDialog.setCancelable(false);
                progressDialog.show();
                AttendanceAdminFetch attendanceAdminFetch = new AttendanceAdminFetch(classDetails.getSelectedItem().toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        progressDialog.hide();
                        Log.e("RESPONSE", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                listDataHeader = new ArrayList<String>();
                                listDataChild = new HashMap<String, List<String>>();
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Log.e("data length", String.valueOf(jsonArray.length()));
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    Iterator iterator = jsonObject1.keys();
                                    while (iterator.hasNext()) {
                                        String key = (String) iterator.next();
                                        listDataHeader.add(key);
                                        JSONArray jsonArray1 = jsonObject1.getJSONArray(key);
                                        List<String> newList = new ArrayList<String>();
                                        for (int j = 0; j < jsonArray1.length(); j++) {
                                            Log.e("date length", String.valueOf(jsonArray1.length()));
                                            JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                                            newList.add(jsonObject2.getString("studentName") + "////" + jsonObject2.getString("subject") + "////" + jsonObject2.getString("status"));
                                        }
                                        listDataChild.put(listDataHeader.get(i), newList);
                                    }
                                }
                                expandableAdapter = new ExpandableAdapter(AdminAttendanceActivity.this, listDataHeader, listDataChild);
                                expandableListView.setAdapter(expandableAdapter);

                            } else {
                                Toast.makeText(AdminAttendanceActivity.this, "Error In Fetching Attendance Details", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        progressDialog.hide();
                        if (error instanceof TimeoutError) {
                            Toast.makeText(AdminAttendanceActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(AdminAttendanceActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(AdminAttendanceActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                requestQueue.add(attendanceAdminFetch);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}

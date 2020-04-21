package com.a3solutions.e_college;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
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

public class ViewFacultyAttendance extends AppCompatActivity {

    ExpandableListView expandableListView;
    RequestQueue requestQueue;
    ExpandableAdapterFaculty expandableAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_faculty_attendance);

        expandableListView = (ExpandableListView) findViewById(R.id.fac_list);
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        final RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
        final ProgressDialog progressDialog = new ProgressDialog(ViewFacultyAttendance.this);
        progressDialog.setMessage("Fetching Data");
        progressDialog.setCancelable(false);
        progressDialog.show();
        AttendanceFacultyFetch attendanceFacultyFetch = new AttendanceFacultyFetch(getIntent().getStringExtra("class"), getIntent().getStringExtra("subject"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                progressDialog.hide();
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
                                    JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                                    newList.add(jsonObject2.getString("studentName")+ "////" + jsonObject2.getString("status"));
                                }
                                listDataChild.put(listDataHeader.get(i), newList);
                            }
                        }
                        expandableAdapter = new ExpandableAdapterFaculty(ViewFacultyAttendance.this, listDataHeader, listDataChild);
                        expandableListView.setAdapter(expandableAdapter);

                    } else {
                        Toast.makeText(ViewFacultyAttendance.this, "Error In Fetching Attendance Details", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ViewFacultyAttendance.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(ViewFacultyAttendance.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(ViewFacultyAttendance.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestQueue.add(attendanceFacultyFetch);
    }
}

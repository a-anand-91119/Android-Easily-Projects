package com.a3solutions.e_college;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
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

import java.util.Calendar;

public class ViewAttendanceActivity extends AppCompatActivity {

    ListView listView;
    Spinner subject_list;
    ArrayAdapter<String> arrayAdapter;
    String[] list, status, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        listView = (ListView) findViewById(R.id.listattencace);
        subject_list = (Spinner) findViewById(R.id.spinner2);
        final RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
        final ProgressDialog progressDialog = new ProgressDialog(ViewAttendanceActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        SubjectFetch subjectFetch = new SubjectFetch(getIntent().getStringExtra("class"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                progressDialog.hide();
                Log.e("RESPONSE", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("subject");
                        list = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            list[i] = jsonObject1.getString("subject");
                        }
                        arrayAdapter = new ArrayAdapter<String>(ViewAttendanceActivity.this, R.layout.support_simple_spinner_dropdown_item, list);
                        subject_list.setAdapter(arrayAdapter);
                    } else {
                        Toast.makeText(ViewAttendanceActivity.this, "Error In Fetching Subject List", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ViewAttendanceActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(ViewAttendanceActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(ViewAttendanceActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestQueue.add(subjectFetch);
        subject_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final ProgressDialog progressDialog = new ProgressDialog(ViewAttendanceActivity.this);
                progressDialog.setMessage("Fetching Data");
                progressDialog.setCancelable(false);
                progressDialog.show();
                GetSubjectRequest getSubjectRequest = new GetSubjectRequest(getIntent().getStringExtra("class"), subject_list.getSelectedItem().toString(), getIntent().getStringExtra("student_id"), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        progressDialog.hide();
                        Log.e("RESPONSE", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("attendance");
                                status = new String[jsonArray.length()];
                                date = new String[jsonArray.length()];
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                    status[i]=jsonObject1.getString("status");
                                    date[i]=jsonObject1.getString("date");
                                }
                                ViewAttendanceList viewAttendanceList = new ViewAttendanceList(ViewAttendanceActivity.this,subject_list.getSelectedItem().toString(),date,status);
                                listView.setAdapter(viewAttendanceList);
                            } else {
                                Toast.makeText(ViewAttendanceActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ViewAttendanceActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(ViewAttendanceActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(ViewAttendanceActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                requestQueue.add(getSubjectRequest);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}

package com.a3solutions.e_college;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AttendanceActivity extends AppCompatActivity {

    ArrayList<String> studentList = new ArrayList<>();
    ArrayList<String> studentId = new ArrayList<>();
    ListView listView;
    Button upload;
    StudentList studentListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        listView = (ListView) findViewById(R.id.listview);
        upload = (Button) findViewById(R.id.button8);

        final ProgressDialog progressDialog = new ProgressDialog(AttendanceActivity.this);
        progressDialog.setMessage("Fetching Student List");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StudentRequest studentRequest = new StudentRequest(getIntent().getStringExtra("class"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("student");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            studentList.add(i, jsonObject1.getString("name"));
                            studentId.add(i, jsonObject1.getString("studentId"));
                        }
                    } else {
                        Toast.makeText(AttendanceActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                studentListAdapter = new StudentList(AttendanceActivity.this, studentId, studentList);
                listView.setAdapter(studentListAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                progressDialog.hide();
                if (error instanceof TimeoutError) {
                    Toast.makeText(AttendanceActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(AttendanceActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(AttendanceActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
        requestQueue.add(studentRequest);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(AttendanceActivity.this);
                progressDialog.setMessage("Uploading");
                progressDialog.setCancelable(false);
                progressDialog.show();
                JSONObject toSend = new JSONObject();
                ArrayList<String> studentId = studentListAdapter.getStudentId();
                boolean[] status = studentListAdapter.getStatus();
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < status.length; i++) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("id", studentId.get(i));
                        if (status[i])
                            jsonObject.put("status", "true");
                        else
                            jsonObject.put("status", "false");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(jsonObject);
                }
                try {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String formattedDate = df.format(c.getTime());
                    toSend.put("length", status.length);
                    toSend.put("date", formattedDate);
                    toSend.put("class", getIntent().getStringExtra("class"));
                    toSend.put("subject", getIntent().getStringExtra("subject"));
                    toSend.put("students", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AttendanceRequest attendanceRequest = new AttendanceRequest(toSend.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        progressDialog.hide();
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success")) {
                                Toast.makeText(AttendanceActivity.this, "Successfully Uploaded Attendance", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AttendanceActivity.this, "Error!! Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
                requestQueue.add(attendanceRequest);
            }

        });
    }
}

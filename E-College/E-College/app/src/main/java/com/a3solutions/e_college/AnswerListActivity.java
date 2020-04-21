package com.a3solutions.e_college;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class AnswerListActivity extends AppCompatActivity {

    ListView answerforq;
    ArrayList<String> answer= new ArrayList<>();
    ArrayAdapter<String> listArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);
        setTitle("Answers");
        answerforq=(ListView)findViewById(R.id.answerforquestion);
        final ProgressDialog progressDialog = new ProgressDialog(AnswerListActivity.this);
        progressDialog.setMessage("Fetching Answers");
        progressDialog.setCancelable(false);
        progressDialog.show();
        AnswerFetchRequest answerFetchRequest = new AnswerFetchRequest(getIntent().getStringExtra("question_id"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response",response);
                progressDialog.dismiss();
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("answer");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            answer.add(jsonObject1.getString("answer"));
                        }
                    } else {
                        Toast.makeText(AnswerListActivity.this, "No Answers Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listArrayAdapter=new ArrayAdapter<String>(AnswerListActivity.this,android.R.layout.simple_list_item_1,answer);
                answerforq.setAdapter(listArrayAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                progressDialog.hide();
                if (error instanceof TimeoutError) {
                    Toast.makeText(AnswerListActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(AnswerListActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(AnswerListActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
        requestQueue.add(answerFetchRequest);
    }
}

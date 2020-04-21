package com.a3solutions.e_college;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class AnswerActivity extends AppCompatActivity {

    ListView lv_listViewQuestion;
    ArrayList<String> question = new ArrayList<>();
    ArrayList<String> questionId = new ArrayList<>();
    QuestionList questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        lv_listViewQuestion = (ListView) findViewById(R.id.list_question);
        final ProgressDialog progressDialog = new ProgressDialog(AnswerActivity.this);
        progressDialog.setMessage("Fetching Question List");
        progressDialog.setCancelable(false);
        progressDialog.show();
        QuestionFetchRequest questionFetchRequest = new QuestionFetchRequest(getIntent().getStringExtra("class"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response",response);
                progressDialog.dismiss();
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("question");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            questionId.add(jsonObject1.getString("question_id"));
                            question.add(jsonObject1.getString("question"));
                        }
                    } else {
                        Toast.makeText(AnswerActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                questionList = new QuestionList(AnswerActivity.this, questionId, question);
                lv_listViewQuestion.setAdapter(questionList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                progressDialog.hide();
                if (error instanceof TimeoutError) {
                    Toast.makeText(AnswerActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(AnswerActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(AnswerActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
        requestQueue.add(questionFetchRequest);
        lv_listViewQuestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(getIntent().getStringExtra("faculty").equals("false")){
                    Intent intent = new Intent(AnswerActivity.this,AnswerListActivity.class);
                    intent.putExtra("question_id",questionId.get(position));
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(AnswerActivity.this,AnswerToQuestionActivity.class);
                    intent.putExtra("question_id",questionId.get(position));
                    startActivity(intent);
                }
            }
        });
    }
}

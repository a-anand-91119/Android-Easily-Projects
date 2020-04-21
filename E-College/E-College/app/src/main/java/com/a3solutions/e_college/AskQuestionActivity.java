package com.a3solutions.e_college;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class AskQuestionActivity extends AppCompatActivity {

    EditText et_question;
    Button ask;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);

        et_question = (EditText) findViewById(R.id.editText);
        ask = (Button) findViewById(R.id.button3);
        requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateQuestion()) {
                    final ProgressDialog progressDialog = new ProgressDialog(AskQuestionActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Asking Question");
                    progressDialog.show();
                    QuestionRequest questionRequest = new QuestionRequest(et_question.getText().toString(), getIntent().getStringExtra("class"), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            progressDialog.hide();
                            Log.e("response", response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("success"))
                                    Toast.makeText(AskQuestionActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(AskQuestionActivity.this, "Failed. Try Again!!", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    requestQueue.add(questionRequest);
                }
            }
        });
    }

    private boolean validateQuestion() {
        if (et_question.getText().toString().equals("")) {
            Toast.makeText(this, "No Question Found?", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

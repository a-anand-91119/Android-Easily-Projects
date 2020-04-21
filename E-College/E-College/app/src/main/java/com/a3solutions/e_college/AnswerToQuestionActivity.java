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

public class AnswerToQuestionActivity extends AppCompatActivity {

    EditText answer;
    Button sendAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_to_question);

        answer = (EditText) findViewById(R.id.editText2);
        sendAnswer = (Button) findViewById(R.id.button5);
        sendAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAnswer()) {
                    final ProgressDialog progressDialog = new ProgressDialog(AnswerToQuestionActivity.this);
                    progressDialog.setMessage("Uploading Answer");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    UploadAnswerRequest uploadRequest = new UploadAnswerRequest(getIntent().getStringExtra("question_id"), answer.getText().toString(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            progressDialog.hide();
                            Log.e("Response", response);
                            try {
                                JSONObject json = new JSONObject(response);
                                if (json.getBoolean("success")) {
                                    Toast.makeText(AnswerToQuestionActivity.this, "Answer Successfully Submitted", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AnswerToQuestionActivity.this, "Error. Please Try Again!!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    RequestQueue request = GetNet.getInstance(getApplicationContext()).getRequestQueue();
                    request.add(uploadRequest);
                }
            }
        });
    }

    private boolean validateAnswer() {
        if (answer.getText().toString().equals("")) {
            Toast.makeText(this, "Please Provide An Answer", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

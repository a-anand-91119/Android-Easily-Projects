package com.a3solutions.e_college;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class AddStudentActivity extends AppCompatActivity {

    EditText et_name_stu, et_username_stu, et_pass_stu, et_conf_stu, et_class_stu;
    Button bt_sign_stu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        setTitle("Add A New Student");
        et_name_stu = (EditText) findViewById(R.id.et_name_stu);
        et_username_stu = (EditText) findViewById(R.id.et_username_stu);
        et_pass_stu = (EditText) findViewById(R.id.et_pass_stu);
        et_conf_stu = (EditText) findViewById(R.id.et_conf_stu);
        et_class_stu = (EditText) findViewById(R.id.et_class_stu);
        bt_sign_stu = (Button) findViewById(R.id.bt_sign_stu);
        bt_sign_stu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUserName() && validatePassword() && validateName() && validateClass()) {
                    final ProgressDialog progressDialog = new ProgressDialog(AddStudentActivity.this);
                    progressDialog.setMessage("Adding New Student");
                    progressDialog.show();
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("RESPONSE", response);
                            progressDialog.dismiss();
                            progressDialog.hide();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("success")) {
                                    Toast.makeText(AddStudentActivity.this, "Successfully Added " + et_name_stu.getText().toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddStudentActivity.this, "Failed. Try Again!!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    AddStudentRequest addStuRequest = new AddStudentRequest(et_username_stu.getText().toString(),
                            et_pass_stu.getText().toString(),
                            et_name_stu.getText().toString(),
                            et_class_stu.getText().toString(),
                            responseListener);
                    RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
                    requestQueue.add(addStuRequest);
                }
            }
        });
    }

    private boolean validateClass() {
        if (et_class_stu.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter A Valid Class", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateName() {
        if (et_name_stu.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter A Valid Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        if (et_pass_stu.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter A Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!et_pass_stu.getText().toString().equals(et_conf_stu.getText().toString())) {
            Toast.makeText(this, "Passwords Don't Match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateUserName() {
        if (et_username_stu.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter A Valid Username", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

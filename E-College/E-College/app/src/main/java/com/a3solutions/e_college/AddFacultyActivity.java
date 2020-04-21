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

public class AddFacultyActivity extends AppCompatActivity {

    EditText et_name_fac, et_username_fac, et_pass_fac, et_conf_fac, et_class_fac, et_sub_fac;
    Button bt_sign_fac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_faculty);
        setTitle("Add New Faculty");
        et_name_fac = (EditText) findViewById(R.id.et_name_fac);
        et_username_fac = (EditText) findViewById(R.id.et_username_fac);
        et_pass_fac = (EditText) findViewById(R.id.et_pass_fac);
        et_conf_fac = (EditText) findViewById(R.id.et_conf_fac);
        et_class_fac = (EditText) findViewById(R.id.et_class_fac);
        et_sub_fac = (EditText) findViewById(R.id.et_sub_fac);
        bt_sign_fac = (Button) findViewById(R.id.bt_sign_fac);
        bt_sign_fac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUserName() && validatePassword() && validateName() && validateClass() && validateSubject()) {
                    final ProgressDialog progressDialog = new ProgressDialog(AddFacultyActivity.this);
                    progressDialog.setMessage("Adding New Faculty");
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
                                    Toast.makeText(AddFacultyActivity.this, "Successfully Added " + et_name_fac.getText().toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddFacultyActivity.this, "Failed. Try Again!!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    AddFacultyRequest addFacultyRequest = new AddFacultyRequest(et_username_fac.getText().toString(),
                            et_pass_fac.getText().toString(),
                            et_name_fac.getText().toString(),
                            et_class_fac.getText().toString(),
                            et_sub_fac.getText().toString(),
                            responseListener);
                    RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
                    requestQueue.add(addFacultyRequest);
                }
            }
        });
    }

    private boolean validateClass() {
        if (et_class_fac.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter A Valid Class", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateName() {
        if (et_name_fac.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter A Valid Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        if (et_pass_fac.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter A Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!et_pass_fac.getText().toString().equals(et_conf_fac.getText().toString())) {
            Toast.makeText(this, "Passwords Don't Match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateUserName() {
        if (et_username_fac.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter A Valid Username", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateSubject() {
        if (et_sub_fac.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter A Valid Subject", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

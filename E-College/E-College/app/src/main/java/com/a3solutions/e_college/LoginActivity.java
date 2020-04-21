package com.a3solutions.e_college;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class LoginActivity extends AppCompatActivity {

    TextInputLayout til_username, til_password;
    EditText et_username, et_password;
    CheckBox cb_admin, cb_faculty;
    String type;
    Button bt_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        til_username = (TextInputLayout) findViewById(R.id.til_username);
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        bt_login = (Button) findViewById(R.id.bt_login);
        cb_admin = (CheckBox) findViewById(R.id.checkBox);
        cb_faculty = (CheckBox) findViewById(R.id.checkBox2);
        cb_admin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_faculty.setChecked(false);
                    type = "admin";
                }
            }
        });
        cb_faculty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_admin.setChecked(false);
                    type = "faculty";
                }
            }
        });
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUserName() && validatePassword()) {
                    if (!cb_admin.isChecked() && !cb_faculty.isChecked()) {
                        type = "student";
                    }
                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Logging In");
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
                                    switch (type){
                                        case "admin":
                                            Intent intent = new Intent(LoginActivity.this,AdminActivity.class);
                                            intent.putExtra("name",jsonObject.getString("name"));
                                            startActivity(intent);
                                            finish();
                                            break;
                                        case "faculty":
                                            Intent intent2 = new Intent(LoginActivity.this,FacultyActivity.class);
                                            intent2.putExtra("name",jsonObject.getString("name"));
                                            intent2.putExtra("class",jsonObject.getString("class"));
                                            intent2.putExtra("subject",jsonObject.getString("subject"));
                                            startActivity(intent2);
                                            finish();
                                            break;
                                        case "student":
                                            Intent intent3 = new Intent(LoginActivity.this,StudentActivity.class);
                                            intent3.putExtra("name",jsonObject.getString("name"));
                                            intent3.putExtra("class",jsonObject.getString("class"));
                                            intent3.putExtra("student_id",jsonObject.getString("student_id"));
                                            startActivity(intent3);
                                            finish();
                                            break;
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Account Not Found!!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            progressDialog.hide();
                            if (error instanceof TimeoutError) {
                                Toast.makeText(LoginActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(LoginActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof NoConnectionError) {
                                Toast.makeText(LoginActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    LoginRequest loginRequest = new LoginRequest(et_username.getText().toString(), et_password.getText().toString(), responseListener, type, errorListener);
                    RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
                    requestQueue.add(loginRequest);
                }
            }
        });

    }

    private boolean validatePassword() {
        if (et_password.getText().toString().trim().isEmpty()) {
            til_password.setError("Enter Your Password");
            requestFocus(et_password);
            return false;
        } else if (et_password.getText().toString().trim().length() < 8) {
            til_password.setError("Password Too Short, Enter Atleast 8 Characters");
            requestFocus(et_password);
            return false;
        } else if (et_password.getText().toString().trim().length() > 32) {
            til_password.setError("Password Too Long, Maximum Length Is 32");
            requestFocus(et_password);
            return false;
        } else {
            til_password.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateUserName() {
        if (et_username.getText().toString().trim().isEmpty()) {
            til_username.setError("Enter Your Username");
            requestFocus(et_username);
            return false;
        } else {
            til_username.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}

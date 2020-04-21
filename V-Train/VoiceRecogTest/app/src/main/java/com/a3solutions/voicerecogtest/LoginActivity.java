package com.a3solutions.voicerecogtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout til_password, til_username;
    TextInputEditText et_username, et_password;
    Button login;
    TextView register;
    SharedPreferences loginCheck;
    SharedPreferences.Editor loginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.bt_login);
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        til_username = (TextInputLayout) findViewById(R.id.til_username);
        et_username = (TextInputEditText) findViewById(R.id.et_username);
        et_password = (TextInputEditText) findViewById(R.id.et_password);
        register = (TextView) findViewById(R.id.tv_register);
        checkLogin();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkUsername() && checkPassword()) {
                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Logging In");
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    final LoginRequest loginRequest = new LoginRequest(et_username.getText().toString(), et_password.getText().toString(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            progressDialog.hide();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("success")) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    loginStatus = getSharedPreferences("LOGIN", Context.MODE_PRIVATE).edit();
                                    loginStatus.putString("name", jsonObject.getString("name"));
                                    loginStatus.putString("user_id", jsonObject.getString("user_id"));
                                    loginStatus.putBoolean("status", true);
                                    loginStatus.apply();
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid Login Credentials", Toast.LENGTH_SHORT).show();
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
                            if (error instanceof NetworkError) {
                                Toast.makeText(LoginActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof NoConnectionError) {
                                Toast.makeText(LoginActivity.this, "No Connection Available", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof TimeoutError) {
                                Toast.makeText(LoginActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {
                                Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    RequestQueue requestQueue = RequestQueueCreate.getInstance(getApplicationContext()).getRequestQueue();
                    requestQueue.add(loginRequest);
                }
            }
        });
    }

    private void checkLogin() {
        loginCheck = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        if (loginCheck.getBoolean("status", false)) {
            Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean checkPassword() {
        if (et_password.getText().toString().trim().isEmpty()) {
            til_password.setError("Enter Your Password");
            requestFocus(et_password);
            return false;
        } else if (et_password.getText().toString().trim().length() < 8) {
            til_password.setError("Password Too Short, Enter Atleast 8 Characters");
            requestFocus(et_password);
            return false;
        } else {
            til_password.setErrorEnabled(false);
        }
        return true;
    }

    private boolean checkUsername() {
        if (et_username.getText().toString().trim().isEmpty()) {
            til_username.setError("Enter Your Username");
            requestFocus(et_username);
            return false;
        } else {
            til_username.setErrorEnabled(false);
        }
        return true;
    }
}

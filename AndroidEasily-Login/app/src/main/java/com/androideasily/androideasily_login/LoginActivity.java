package com.androideasily.androideasily_login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout til_username, til_password;
    Button bt_login;
    TextView tv_register;
    String username, password;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login"); //set title of the activity
        initialize(); //Initialize all the widgets present in the layout

        requestQueue = Volley.newRequestQueue(LoginActivity.this);//Creating the RequestQueue

        //Login Button Clicked
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = til_username.getEditText().getText().toString();
                password = til_password.getEditText().getText().toString();
                if (validateUsername(username) && validatePassword(password)) { //Username and Password Validation
                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setTitle("Please Wait");
                    progressDialog.setMessage("Logging You In");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    LoginRequest loginRequest = new LoginRequest(username, password, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("Login Response", response);
                            progressDialog.dismiss();
                            // Response from the server is in the form if a JSON, so we need a JSON Object
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("success")) {
                                    Intent loginSuccess = new Intent(LoginActivity.this, MainActivity.class);
                                    //Passing all received data from server to next activity
                                    loginSuccess.putExtra("name", jsonObject.getString("name"));
                                    loginSuccess.putExtra("mobile", jsonObject.getString("mobile"));
                                    loginSuccess.putExtra("email", jsonObject.getString("email"));
                                    loginSuccess.putExtra("url", jsonObject.getString("url"));
                                    startActivity(loginSuccess);
                                    finish();
                                } else {
                                    if(jsonObject.getString("status").equals("INVALID"))
                                        Toast.makeText(LoginActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                                    else{
                                        Toast.makeText(LoginActivity.this, "Passwords Don't Match", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            if (error instanceof ServerError)
                                Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            else if (error instanceof TimeoutError)
                                Toast.makeText(LoginActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                            else if (error instanceof NetworkError)
                                Toast.makeText(LoginActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                    requestQueue.add(loginRequest);
                }
            }
        });

        //Don't Have An Account TextView Clicked
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initialize() {
        til_username = (TextInputLayout) findViewById(R.id.til_username);
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        bt_login = (Button) findViewById(R.id.bt_login);
        tv_register = (TextView) findViewById(R.id.tv_register);
    }

    private boolean validateUsername(String string) {
        //Validating the entered USERNAME
        if (string.equals("")) {
            til_username.setError("Enter a Username");
            return false;
        } else if (string.length() > 50) {
            til_username.setError("Maximum 50 Characters");
            return false;
        } else if (string.length() < 6) {
            til_username.setError("Minimum 6 Characters");
            return false;
        }
        til_username.setErrorEnabled(false);
        return true;
    }

    private boolean validatePassword(String string) {
        //Validating the entered PASSWORD
        if (string.equals("")) {
            til_password.setError("Enter Your Password");
            return false;
        } else if (string.length() > 32) {
            til_password.setError("Maximum 32 Characters");
            return false;
        } else if (string.length() < 8) {
            til_password.setError("Minimum 8 Characters");
            return false;
        }
        til_password.setErrorEnabled(false);
        return true;
    }
}

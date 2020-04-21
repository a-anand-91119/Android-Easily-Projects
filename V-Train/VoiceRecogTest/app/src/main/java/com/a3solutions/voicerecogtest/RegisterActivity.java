package com.a3solutions.voicerecogtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout til_username, til_password, til_confirm, til_mobile, til_name, til_email;
    Button bt_signup;
    EditText et_username, et_password, et_confirm, et_name, et_mobile, et_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        til_confirm = (TextInputLayout) findViewById(R.id.til_password_confirm_reg);
        til_password = (TextInputLayout) findViewById(R.id.til_password_reg);
        til_username = (TextInputLayout) findViewById(R.id.til_username_reg);
        til_name = (TextInputLayout) findViewById(R.id.til_name_reg);
        til_mobile = (TextInputLayout) findViewById(R.id.til_mobile_reg);
        til_email = (TextInputLayout) findViewById(R.id.til_email_reg);
        et_username = (EditText) findViewById(R.id.et_reg_username);
        et_password = (EditText) findViewById(R.id.et_reg_password);
        et_confirm = (EditText) findViewById(R.id.et_reg_confirmpassword);
        et_mobile = (EditText) findViewById(R.id.et_reg_mobileno);
        et_name = (EditText) findViewById(R.id.et_reg_name);
        et_email = (EditText) findViewById(R.id.et_reg_email);
        bt_signup = (Button) findViewById(R.id.bt_signup);

        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateUserName() && validateName() && validatePassword() && validateConfirmPassword() && validateEmail() && validateMobile()) {
                    final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                    progressDialog.setMessage("Registering");
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    RegisterRequest registerRequest = new RegisterRequest(et_username.getText().toString().trim(),
                            et_password.getText().toString().trim(),
                            et_name.getText().toString().trim(),
                            et_email.getText().toString().trim(),
                            et_mobile.getText().toString(),
                            UUID.randomUUID(),
                            new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("response",response);
                            progressDialog.dismiss();
                            progressDialog.hide();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("success")) {
                                    Toast.makeText(RegisterActivity.this, "You Can Now Login", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if(jsonObject.getBoolean("usernamecopy")){
                                    Toast.makeText(RegisterActivity.this, "Username Already Exist", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(RegisterActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    RequestQueue requestQueue = RequestQueueCreate.getInstance(getApplicationContext()).getRequestQueue();
                    requestQueue.add(registerRequest);
                }
            }
        });
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateUserName() {
        if (et_username.getText().toString().trim().isEmpty()) {
            til_username.setError("Enter Your Username");
            requestFocus(et_username);
            return false;
        } else
            til_username.setErrorEnabled(false);
        return true;
    }

    private boolean validateName() {
        if (et_name.getText().toString().trim().isEmpty()) {
            til_name.setError("Enter Your Full Name");
            requestFocus(et_name);
            return false;
        } else
            til_name.setErrorEnabled(false);
        return true;
    }

    private boolean validateMobile() {
        if (et_mobile.getText().toString().trim().isEmpty()) {
            til_mobile.setError("Enter Your Mobile Number");
            requestFocus(et_mobile);
            return false;
        } else if (et_mobile.getText().toString().length() != 10) {
            til_mobile.setError("Enter A Valid Mobile Number");
            requestFocus(et_mobile);
            return false;
        } else {
            til_mobile.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmail() {
        if (et_email.getText().toString().trim().isEmpty()) {
            til_email.setError("Enter Your Email Address");
            requestFocus(et_email);
            return false;
        } else {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString()).matches()) {
                til_email.setError("Enter A Valid Email Address");
                requestFocus(et_email);
                return false;
            } else
                til_email.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateConfirmPassword() {
        if (et_confirm.getText().toString().trim().isEmpty()) {
            til_confirm.setError("Retype Your Password");
            requestFocus(et_confirm);
            return false;
        } else if (!et_confirm.getText().toString().trim().equals(et_password.getText().toString().trim())) {
            til_confirm.setError("Password Do Not Match");
            til_password.setError("Password Do Not Match");
            requestFocus(et_confirm);
            requestFocus(et_password);
            return false;
        } else {
            til_confirm.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (et_password.getText().toString().trim().isEmpty()) {
            til_password.setError("Enter A Password");
            requestFocus(et_password);
            return false;
        } else if (et_password.getText().toString().trim().length() < 8) {
            til_password.setError("Password Must Contain Minimum Of 8 Characters");
            requestFocus(et_password);
            return false;
        } else if (et_password.getText().toString().trim().length() > 32) {
            til_password.setError("Password Exceeds Limit Of 32 Characters");
            requestFocus(et_password);
            return false;
        } else {
            til_password.setErrorEnabled(false);
        }
        return true;
    }
}

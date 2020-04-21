package com.a3solutions.e_lection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    private TextInputLayout til_voterid, til_password;
    private String voterid, password;
    private Button bt_login;
    private TextInputEditText et_voterid, et_password;
    private Boolean pressedBackOnce = false;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voterid = et_voterid.getText().toString();
                password = et_password.getText().toString();
                if (validateUserId() && validatePassword()) {
                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setTitle("Please Wait");
                    progressDialog.setMessage("Logging In...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                Boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    if (jsonResponse.getBoolean("status")) {
                                        progressDialog.setMessage("Authenticating Administrator");
                                        auth.signInWithEmailAndPassword(jsonResponse.getString("email"), jsonResponse.getString("password"))
                                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        progressDialog.hide();
                                                        progressDialog.dismiss();
                                                        Intent adminLogin = new Intent(LoginActivity.this, AdminPortal.class);
                                                        adminLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(adminLogin);
                                                        finish();
                                                    }
                                                });
                                    } else {
                                        progressDialog.hide();
                                        progressDialog.dismiss();
                                        Intent voterIntent = new Intent(LoginActivity.this,VoterActivity.class);
                                        startActivity(voterIntent);
                                        finish();
                                    }
                                } else {
                                    progressDialog.hide();
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Voter Not Found!!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.hide();
                            progressDialog.dismiss();
                            if (error instanceof TimeoutError) {
                                Toast.makeText(LoginActivity.this, "Network Connection Timed Out", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof NoConnectionError) {
                                Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {
                                Toast.makeText(LoginActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(LoginActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    LoginRequest loginRequest = new LoginRequest(voterid, password, responseListener, errorListener);
                    RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                    requestQueue.add(loginRequest);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (pressedBackOnce) {
            super.onBackPressed();
            return;
        }
        this.pressedBackOnce = true;
        Toast.makeText(this, "Press Back Again To Exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pressedBackOnce = false;
            }
        }, 2000);
    }

    private void initialize() {
        auth = FirebaseAuth.getInstance();
        til_voterid = (TextInputLayout) findViewById(R.id.til_voterid);
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        bt_login = (Button) findViewById(R.id.bt_login);
        et_password = (TextInputEditText) findViewById(R.id.et_password);
        et_voterid = (TextInputEditText) findViewById(R.id.et_voterid);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private Boolean validateUserId() {
        if (et_voterid.getText().toString().trim().isEmpty()) {
            til_voterid.setError("Enter Your Voter ID");
            requestFocus(et_voterid);
            return false;
        } else {
            til_voterid.setErrorEnabled(false);
        }
        return true;
    }

    private Boolean validatePassword() {
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

}
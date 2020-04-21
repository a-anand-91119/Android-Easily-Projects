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

public class AddAdminActivity extends AppCompatActivity {

    EditText et_name_ad, et_username_ad, et_pass_ad, et_conf_ad;
    Button bt_sign_ad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin);
        setTitle("Add New Administrator");

        et_name_ad = (EditText) findViewById(R.id.et_name_ad);
        et_username_ad = (EditText) findViewById(R.id.et_username_ad);
        et_pass_ad = (EditText) findViewById(R.id.et_pass_ad);
        et_conf_ad = (EditText) findViewById(R.id.et_conf_ad);
        bt_sign_ad = (Button) findViewById(R.id.bt_sign_ad);
        bt_sign_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUserName() && validatePassword() && validateName()) {
                    final ProgressDialog progressDialog = new ProgressDialog(AddAdminActivity.this);
                    progressDialog.setMessage("Adding New Administrator");
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
                                    Toast.makeText(AddAdminActivity.this, "Successfully Added " + et_name_ad.getText().toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddAdminActivity.this, "Failed. Try Again!!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    AddAdminRequest addAdminRequest = new AddAdminRequest(et_username_ad.getText().toString(),
                            et_pass_ad.getText().toString(),
                            et_name_ad.getText().toString(),
                            responseListener);
                    RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
                    requestQueue.add(addAdminRequest);
                }
            }
        });
    }

    private boolean validateName() {
        if (et_name_ad.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter A Valid Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        if (et_pass_ad.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter A Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!et_pass_ad.getText().toString().equals(et_conf_ad.getText().toString())) {
            Toast.makeText(this, "Passwords Don't Match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateUserName() {
        if (et_username_ad.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter A Valid Username", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

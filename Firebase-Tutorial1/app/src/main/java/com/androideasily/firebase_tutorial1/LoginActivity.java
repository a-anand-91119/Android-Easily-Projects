package com.androideasily.firebase_tutorial1;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout til_username, til_password;
    Button bt_login;
    TextView tv_register;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login"); //set title of the activity
        initialize(); //Initialize all the widgets present in the layout

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

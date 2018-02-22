package com.androideasily.firebase_loginregistration;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout til_email;
    private TextInputLayout til_password;
    private Button bt_login;
    private TextView tv_register;
    private TextView tv_forgot;
    private String email;
    private String password;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences.Editor storeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Firebase Login & Registration"); //set title of the activity
        initialize(); //Initialize all the widgets present in the layout

        //Initializing Firebase Components
        firebaseAuth = FirebaseAuth.getInstance();

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opening register activity
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                registerIntent.putExtra("contains", false);
                startActivity(registerIntent);
            }
        });

        tv_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Forgot password. Send password Reset Instructions
                final AlertDialog.Builder passReset = new AlertDialog.Builder(LoginActivity.this);
                //Inflating the reset_pass view
                View resetView = getLayoutInflater().inflate(R.layout.reset_pass, null);
                //Setting the view inside the alertDialog
                passReset.setView(resetView);
                passReset.setCancelable(false);
                passReset.setTitle("Password Reset");
                final AlertDialog alertDialog = passReset.create();
                //initializing the widgets in the reset_pass view
                final TextInputLayout til_resetPass = (TextInputLayout) resetView.findViewById(R.id.textInputLayoutReset);
                Button bt_send = (Button) resetView.findViewById(R.id.button5);
                Button bt_cancel = (Button) resetView.findViewById(R.id.button6);
                bt_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //onClickListener for password reset email send button
                        if (validateEmail(til_resetPass.getEditText().getText().toString(), til_resetPass)) {

                            //Sending password reset email using sendPasswordResetEmail() method
                            firebaseAuth.sendPasswordResetEmail(til_resetPass.getEditText().getText().toString()).addOnSuccessListener(LoginActivity.this, new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Snackbar.make(findViewById(android.R.id.content), "Password Reset Mail Send Successfully", Snackbar.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                }
                            }).addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (e instanceof FirebaseAuthInvalidUserException)
                                        Snackbar.make(findViewById(android.R.id.content), "Account not Found", Snackbar.LENGTH_LONG).show();
                                }
                            });
                            //Password Reset Email sending finished
                        }
                    }
                });
                bt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Login User
                email = til_email.getEditText().getText().toString();
                password = til_password.getEditText().getText().toString();
                if (validateEmail(email, til_email) &&
                        validatePassword(password)) {
                    //initializing the progress dialog
                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Logging In");
                    progressDialog.setTitle("Please Wait");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    //logging in user using the signInWithEmailAndPassword() method
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                             /*Initializing the SharedPreference
                             The users password is stored in shared preference to automatically re-authenticate the user. else every time certain operations like,
                             phone verification, user management operations needs to be done, the users password needs to be asked explicitly.*/

                            storeData = getSharedPreferences("USER_D", Context.MODE_PRIVATE).edit();
                            storeData.putString("pd", til_password.getEditText().getText().toString());
                            storeData.apply();
                            progressDialog.dismiss();
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }
                    }).addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Log.i("FIREBASE ERROR:", e.getMessage());
                            if (e instanceof FirebaseAuthInvalidCredentialsException)
                                Snackbar.make(findViewById(android.R.id.content), "Invalid Credentials", Snackbar.LENGTH_SHORT).show();
                            else if (e instanceof FirebaseAuthInvalidUserException)
                                //add option to create an account by adding an action to the snackBar
                                Snackbar.make(findViewById(android.R.id.content), "Account Not Found", Snackbar.LENGTH_LONG).setAction("Create One", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                                        registerIntent.putExtra("contains", true);
                                        registerIntent.putExtra("email", email);
                                        registerIntent.putExtra("password", password);
                                        startActivity(registerIntent);
                                    }
                                }).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Auto-Login using Firebase
        // Note: if the authentication token expires then the user needs to be re-authenticated
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            //User Already Logged In. Proceed to Main Activity
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }

    //INITIALIZING WIDGETS
    private void initialize() {
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        bt_login = (Button) findViewById(R.id.bt_login);
        tv_register = (TextView) findViewById(R.id.tv_register);
        tv_forgot = (TextView) findViewById(R.id.textView4);
    }

    //VALIDATION FUNCTIONS
    private boolean validateEmail(String string, TextInputLayout textInputLayout) {
        if (string.equals("")) {
            textInputLayout.setError("Enter Your Email Address");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(string).matches()) {
            textInputLayout.setError("Enter A Valid Email Address");
            return false;
        }
        textInputLayout.setErrorEnabled(false);
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

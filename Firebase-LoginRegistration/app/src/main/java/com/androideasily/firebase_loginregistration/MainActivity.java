package com.androideasily.firebase_loginregistration;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

//This is a BasicActivity with the FloatingActionButton removed//
public class MainActivity extends AppCompatActivity {

    private Button emailVerify;
    private FirebaseAuth firebaseAuth;
    private TextView tv_email;
    private TextView email_status;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initializing shared preference
        sharedPreferences = getSharedPreferences("USER_D", Context.MODE_PRIVATE);

        //Initializing Widgets
        TextView tv_name = (TextView) findViewById(R.id.textView9);
        tv_email = (TextView) findViewById(R.id.textView13);
        email_status = (TextView) findViewById(R.id.textView5);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        emailVerify = (Button) findViewById(R.id.button2);

        //Initializing FirebaseAuthentication
        firebaseAuth = FirebaseAuth.getInstance();

        //Welcome Message on toolbar
        setTitle("Welcome: " + firebaseAuth.getCurrentUser().getDisplayName());

        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            tv_name.setText(firebaseUser.getDisplayName());
            tv_email.setText(firebaseUser.getEmail());

            //get verification status from Firebase
            if (firebaseUser.isEmailVerified()) {
                email_status.setText(R.string.verified);
                email_status.setTextColor(Color.GREEN);
                emailVerify.setVisibility(View.GONE);
                sharedPreferences.edit().putBoolean("verification_email", false).apply();
            }

            //setting the verification/send email button based on information regarding verification email send previously or not
            if (!sharedPreferences.getBoolean("verification_email", false))
                emailVerify.setText(R.string.verify_email);
            else
                emailVerify.setText(R.string.check_verification_status);

            emailVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseUser.reload();
                    if (firebaseUser.isEmailVerified()) {
                        email_status.setText(R.string.verified);
                        email_status.setTextColor(Color.GREEN);
                        emailVerify.setVisibility(View.GONE);
                    } else {
                        if (!sharedPreferences.getBoolean("verification_email", false)) {
                            //Verification email has not been send previously
                            sendVerification();
                        } else
                            //verification email has been send previously
                            Snackbar.make(findViewById(android.R.id.content), R.string.email_check, Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

            //Downloading and displaying user profile image
            StorageReference profileDownload = FirebaseStorage.getInstance().getReference("profile_images").child(firebaseUser.getUid());
            try {
                final File tempFile = File.createTempFile("profile picture", "png");
                profileDownload.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap profileImage = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                        imageView.setImageBitmap(profileImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendVerification() {
        final Snackbar sendingEmailSnackBar = Snackbar.make(findViewById(android.R.id.content), R.string.sending_email, Snackbar.LENGTH_INDEFINITE);
        sendingEmailSnackBar.show();
        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i("Email Verification", "onCompleteListener");
                sendingEmailSnackBar.dismiss();
                //verification email has been send, saving this information
                sharedPreferences.edit().putBoolean("verification_email", true).apply();
                Snackbar.make(findViewById(android.R.id.content), R.string.email_sent_success, Snackbar.LENGTH_SHORT).show();
                emailVerify.setText(R.string.check_verification_status);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                //perform logout operation
                firebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.del_account:
                //Ask for users confirmation first
                AlertDialog.Builder askConfirm = new AlertDialog.Builder(MainActivity.this);
                askConfirm.setCancelable(false);
                askConfirm.setIcon(android.R.drawable.ic_dialog_alert);
                askConfirm.setTitle("Confirm Account Deletion");
                askConfirm.setMessage("Once Deleted All Data Will Be Lost And Cannot Be Recovered");
                askConfirm.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*
                        *   BEFORE WE CAN DELETE A USER ACCOUNT , WE MUST ENSURE THE USER IS CORRECTLY LOGGED IN.
                        *   HENCE THE NEED TO RE AUTHENTICATE THE USER. ELSE "CREDENTIAL_TOO_OLD_LOGIN_AGAIN" ERROR WILL OCCUR.
                        * */
                        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Deleting Account");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        //getting current user
                        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        //Re-authentication credentials
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(currentUser.getEmail(), //Users Email Address
                                        getSharedPreferences("USER_D", Context.MODE_PRIVATE).getString("pd", ""));// Users Password Stored in SharedPreference During Login
                        //Re-authentication
                        currentUser.reauthenticate(credential).addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //User Successfully Re-Authenticated
                                //First delete users profile image from the database
                                StorageReference toDelete = FirebaseStorage.getInstance().getReference("profile_images/" + currentUser.getUid());
                                toDelete.delete().addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //Profile image deleted successfully, now delete the account itself
                                        currentUser.delete().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Snackbar.make(findViewById(android.R.id.content), "User Account Successfully Deleted", Snackbar.LENGTH_SHORT).show();
                                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
                askConfirm.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                askConfirm.show();
                break;

            case R.id.chg_email:
                //Change The Users Email Address
                AlertDialog.Builder changeEmail = new AlertDialog.Builder(MainActivity.this);
                //Layout with EditText to accept users NEW Email Address
                //EditText Field to be added to the layout
                final EditText emailChange = new EditText(MainActivity.this);
                //Linear Layout to hold the EditText
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                //adding emailToReset to Linear Layout
                emailChange.setLayoutParams(layoutParams);
                //Setting the view inside the alertDialog
                changeEmail.setView(emailChange);
                changeEmail.setCancelable(false);
                changeEmail.setTitle("Enter New Email Address");
                changeEmail.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Changing Email Address");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        //re-authenticating user incase the login token expired
                        //Creating the authentication credentials
                        firebaseAuth.getCurrentUser().reload();
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(firebaseAuth.getCurrentUser().getEmail(), //Users Email Address
                                        getSharedPreferences("USER_D", Context.MODE_PRIVATE).getString("pd", ""));// Users Password Stored in SharedPreference During Login
                        firebaseAuth.getCurrentUser().reauthenticate(credential).addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Once re-authentication has completed we can change the email address
                                firebaseAuth.getCurrentUser().updateEmail(emailChange.getText().toString()).addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        //new email address needs to be verified.
                                        sharedPreferences.edit().putBoolean("verification_email", false).apply();
                                        Snackbar.make(findViewById(android.R.id.content), "Email Changed Successfully", Snackbar.LENGTH_SHORT).show();
                                        //updating UI
                                        tv_email.setText(firebaseAuth.getCurrentUser().getEmail());
                                        emailVerify.setVisibility(View.VISIBLE);
                                        email_status.setText(R.string.email_not_verified);
                                        email_status.setTextColor(Color.RED);
                                        emailVerify.setText(R.string.verify_email);
                                    }
                                }).addOnFailureListener(MainActivity.this, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Snackbar.make(findViewById(android.R.id.content), "Failed. Please Try Again!!", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                });
                changeEmail.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                changeEmail.show();
                break;
        }
        return true;
    }
}
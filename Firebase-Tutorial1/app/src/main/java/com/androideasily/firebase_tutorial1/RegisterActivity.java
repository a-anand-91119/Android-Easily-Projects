package com.androideasily.firebase_tutorial1;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    Button bt_register;
    TextInputLayout til_name, til_password, til_confirmPass, til_mobile, til_email;
    ImageView iv_profile;
    String name, password, email, mobile, confirm;
    boolean IMAGE_STATUS = false;
    Bitmap profilePicture;
    Uri imageUri;
    FirebaseAuth myAuth;
    PhoneAuthProvider phoneAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialize();//Function to initialize widgets

        //Getting Firebase and Phone Authentication Instance
        myAuth = FirebaseAuth.getInstance();
        phoneAuthProvider = PhoneAuthProvider.getInstance(); //Added on Firebase version 11

        //Adding onClickListener to the ImageView to select the profile Picture
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1000);
               /* 1000 is just a unique REQUEST CODE which is used to ensure that the result which we receive in onActivityResult is for picking image.
                In a program which used multiple functions which needs the onActivityResult we can perform the appropriate action based on the request code

                The final result will be available in onActivityResult which is overridden*/
            }
        });

        //FIREBASE REGISTRATION BEGINS
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting the entered text from text fields into String variables
                name = til_name.getEditText().getText().toString();
                password = til_password.getEditText().getText().toString();
                confirm = til_confirmPass.getEditText().getText().toString();
                email = til_email.getEditText().getText().toString();
                mobile = til_mobile.getEditText().getText().toString();

                if (validateEmail(email) &&
                        validateName(name) &&
                        validatePassword(password) &&
                        validateConfirm(confirm) &&
                        validateMobile(mobile) &&
                        validateProfile()) {
                    //Now lets first verify the users mobile number
                    phoneAuthProvider.verifyPhoneNumber("+919656691119", 1, TimeUnit.MINUTES, RegisterActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(s, forceResendingToken);
                            Toast.makeText(RegisterActivity.this, "A verification code has been send to" + mobile, Toast.LENGTH_SHORT).show();
                            final String verificationCode = s;
                            Log.i("SMS Verification Code",s);

                            //Displaying an Alert Dialog to accept uses input (verification Code)
                            AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(RegisterActivity.this);
                            confirmationDialog.setTitle("Enter Verification Code");
                            //Inflate a custom layout containing textView
                            LayoutInflater layoutInflator = getLayoutInflater();
                            final View view = layoutInflator.inflate(R.layout.sms_verification, (ViewGroup) findViewById(R.id.sms_linear));
                            confirmationDialog.setView(view);
                            final EditText smsCode = (EditText) view.findViewById(R.id.sms_code);
                            confirmationDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (smsCode.getText().toString().equals(verificationCode)) {
                                        Toast.makeText(RegisterActivity.this, "Successfully Verified", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            confirmationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                                }
                            });
                            confirmationDialog.setCancelable(false);
                            //Displaying the AlertDialog
                            confirmationDialog.show();
                        }

                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                            //Mobile verification Completed. Now we can create an account for the user.

                            myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //Registration Successful, add User Information

                                        //Get currently created user information
                                        final FirebaseUser currentlyCreatedUser = task.getResult().getUser();

                                        //First Upload the user profile image to Firebase Storage and retrieve the uri for the image
                                        StorageReference myStorageReference = FirebaseStorage.getInstance().getReference("profile_images");
                                        StorageReference uploadReference =
                                                myStorageReference.child(currentlyCreatedUser.getUid() + ".png");// creating a unique filename

                                        //Actual uploading process using the image URI obtained in the onActivityResult
                                        UploadTask uploadProfileImage = uploadReference.putFile(imageUri);
                                        //Success or Error Listeners
                                        uploadProfileImage.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                                Log.i("IMAGE URL", String.valueOf(downloadUrl));
                                                //We need to perform the below opertaion only if the image upload was completed
                                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(name)
                                                        .setPhotoUri(downloadUrl)
                                                        .build();
                                                currentlyCreatedUser.updateProfile(userProfileChangeRequest);
                                            }
                                        });
                                        uploadProfileImage.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RegisterActivity.this, "Error In Uploading Image. Try Again!!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        /*
                                             All the Registration process
                                                 * Creating Account using email and password
                                                 * Uploading profile picture
                                                 * Adding user name
                                             have been completed

                                         */
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Failed To Create Account. Please Try Again!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(RegisterActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
                                    dialog.setTitle("Failed");
                                    dialog.setMessage("Email Address Already In Use");
                                }
                            });
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {
                            if (e instanceof FirebaseTooManyRequestsException) {
                                Toast.makeText(RegisterActivity.this, "SMS Quota Exhausted. Try Again Later :(", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(RegisterActivity.this, "Verification Failed. Kindly Recheck Entered Mobile Number", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
            //Image Successfully Selected
            try {
                //parsing the Intent data and displaying it in the imageView
                imageUri = data.getData();//Getting uri of the data
                InputStream imageStream = getContentResolver().openInputStream(imageUri);//creating an imputstream
                profilePicture = BitmapFactory.decodeStream(imageStream);//decoding the input stream to bitmap
                iv_profile.setImageBitmap(profilePicture);
                IMAGE_STATUS = true;//setting the flag
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void initialize() {
        //Initializing the widgets in the layout
        til_name = (TextInputLayout) findViewById(R.id.til_name_reg);
        til_password = (TextInputLayout) findViewById(R.id.til_password_reg);
        til_confirmPass = (TextInputLayout) findViewById(R.id.til_confirm_reg);
        til_mobile = (TextInputLayout) findViewById(R.id.til_mobile_reg);
        til_email = (TextInputLayout) findViewById(R.id.til_email_reg);
        bt_register = (Button) findViewById(R.id.bt_register);
        iv_profile = (ImageView) findViewById(R.id.im_profile);
    }

    private boolean validateName(String string) {
        if (string.equals("")) {
            til_name.setError("Enter Your Name");
            return false;
        } else if (string.length() > 50) {
            til_name.setError("Maximum 50 Characters");
            return false;
        }
        til_name.setErrorEnabled(false);
        return true;
    }

    private boolean validatePassword(String string) {
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

    private boolean validateConfirm(String string) {
        if (string.equals("")) {
            til_confirmPass.setError("Re-Enter Your Password");
            return false;
        } else if (!string.equals(til_password.getEditText().getText().toString())) {
            til_confirmPass.setError("Passwords Do Not Match");
            til_password.setError("Passwords Do Not Match");
            return false;
        }
        til_confirmPass.setErrorEnabled(false);
        return true;
    }

    private boolean validateMobile(String string) {
        if (string.equals("")) {
            til_mobile.setError("Enter Your Mobile Number");
            return false;
        }
        if (string.length() != 10) {
            til_mobile.setError("Enter A Valid Mobile Number");
            return false;
        }
        til_mobile.setErrorEnabled(false);
        return true;
    }

    private boolean validateEmail(String string) {
        if (string.equals("")) {
            til_email.setError("Enter Your Email Address");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(string).matches()) {
            til_email.setError("Enter A Valid Email Address");
            return false;
        }
        til_email.setErrorEnabled(false);
        return true;
    }

    private boolean validateProfile() {
        if (!IMAGE_STATUS)
            Toast.makeText(this, "Select A Profile Picture", Toast.LENGTH_SHORT).show();
        return IMAGE_STATUS;
    }
}

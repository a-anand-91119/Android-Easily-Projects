package com.androideasily.firebase_loginregistration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity {

    private Button bt_register;
    private TextInputLayout til_name;
    private TextInputLayout til_password;
    private TextInputLayout til_confirmPass;
    private TextInputLayout til_email;
    private ImageView iv_profile;
    private String name;
    private String password;
    private String email;
    private String confirm;
    private boolean IMAGE_STATUS = false;
    private Uri imageUri;
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialize();//Function to initialize widgets

        //Getting Firebase and Phone Authentication Instance
        myAuth = FirebaseAuth.getInstance();

        //checking for any data passed from previous activity
        if (getIntent().getBooleanExtra("contains", false)) {
            til_email.getEditText().setText(getIntent().getStringExtra("email"));
            til_password.getEditText().setText(getIntent().getStringExtra("password"));
            til_confirmPass.getEditText().setText(getIntent().getStringExtra("password"));
        }

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

        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting the entered text from text fields into String variables
                name = til_name.getEditText().getText().toString();
                password = til_password.getEditText().getText().toString();
                confirm = til_confirmPass.getEditText().getText().toString();
                email = til_email.getEditText().getText().toString();

                if (validateEmail(email) &&
                        validateName(name) &&
                        validatePassword(password) &&
                        validateConfirm(confirm) &&
                        validateProfile()) {
                    final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                    progressDialog.setTitle("Please Wait");
                    progressDialog.setMessage("Creating Your Account");
                    progressDialog.show();
                    //Creating a user user in Firebase. This user can access all other firebase functions (this access restriction depends on the rules specified in firebase console)
                    myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.setMessage("Uploading User Details");
                            if (task.isSuccessful()) {
                                //Registration Successful, add User Information such as name, user profile image url

                                //Get the currently created user object
                                final FirebaseUser currentlyCreatedUser = task.getResult().getUser();

                                //First Upload the user profile image to Firebase Storage and retrieve the uri for the image
                                StorageReference myStorageReference = FirebaseStorage.getInstance().getReference("profile_images");
                                StorageReference uploadReference =
                                        myStorageReference.child(currentlyCreatedUser.getUid());// creating a unique filename

                                //Actual uploading process using the image URI obtained in the onActivityResult
                                UploadTask uploadProfileImage = uploadReference.putFile(imageUri);
                                //Success or Error Listeners
                                uploadProfileImage.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Uri downloadUrl = taskSnapshot.getDownloadUrl();// Download URL of the profile image.
                                        //We need to perform the below operation only if the image upload was completed
                                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name)
                                                .setPhotoUri(downloadUrl)
                                                .build();
                                        /*Updating the user profile using updateProfile() method.
                                        Note: the users mobile number can also be added, but it is done using phoneAuth and then merging the
                                                                                                    phoneAuth and emailAndPassword authentication providers.*/
                                        currentlyCreatedUser.updateProfile(userProfileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                /*
                                                    All the Registration process
                                                         * Creating Account using email and password
                                                         * Uploading profile picture
                                                         * Adding user name
                                                    have been completed

                                                */
                                                //Opening Main Activity
                                                Snackbar.make(findViewById(android.R.id.content), "Account Created Successfully", Snackbar.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                                    }
                                });
                                uploadProfileImage.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Log.i("FIREBASE ERROR:", e.getMessage());
                                        Snackbar.make(findViewById(android.R.id.content), "Error In Uploading Image", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    Snackbar.make(findViewById(android.R.id.content), "Email Address Already In Use", Snackbar.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Snackbar.make(findViewById(android.R.id.content), "Failed To Create Account!", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }).addOnFailureListener(RegisterActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Log.i("FIREBASE ERROR failure:", e.getMessage());
                        }
                    });
                }
            }
        });
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
                Bitmap profilePicture = BitmapFactory.decodeStream(imageStream);
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
        til_email = (TextInputLayout) findViewById(R.id.til_email_reg);
        bt_register = (Button) findViewById(R.id.bt_register);
        iv_profile = (ImageView) findViewById(R.id.im_profile);
    }

    //VALIDATION FUNCTIONS
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
            Snackbar.make(findViewById(android.R.id.content), "Select A Profile Picture", Snackbar.LENGTH_SHORT).show();
        return IMAGE_STATUS;
    }
}

package com.androideasily.androideasily_login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity {

    Button bt_register;
    TextInputLayout til_name, til_username, til_password, til_confirmPass, til_mobile, til_email;
    ImageView iv_profile;
    String name, username, password, email, mobile, profile, confirm;
    RequestQueue requestQueue;
    boolean IMAGE_STATUS = false;
    Bitmap profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Create An Account");

        initialize();//Function to initialize widgets

        //creating request queue
        requestQueue = Volley.newRequestQueue(RegisterActivity.this);

        //Adding onClickListener to the ImageView to select the profile Picture
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1000);
                //result will be available in onActivityResult which is overridden
            }
        });

        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = til_name.getEditText().getText().toString();
                username = til_username.getEditText().getText().toString();
                password = til_password.getEditText().getText().toString();
                email = til_email.getEditText().getText().toString();
                mobile = til_mobile.getEditText().getText().toString();
                confirm = til_confirmPass.getEditText().getText().toString();
                if (    //perform validation by calling all the validate functions inside the IF condition
                        validateUsername(username) &&
                                validateName(name) &&
                                validatePassword(password) &&
                                validateConfirm(confirm) &&
                                validateMobile(mobile) &&
                                validateEmail(email) &&
                                validateProfile()
                        ) {
                    final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
                    progress.setTitle("Please Wait");
                    progress.setMessage("Creating Your Account");
                    progress.setCancelable(false);
                    progress.show();
                    //Validation Success
                    convertBitmapToString(profilePicture);
                    RegisterRequest registerRequest = new RegisterRequest(username, name, password, mobile, email, profile, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("Response", response);
                            progress.dismiss();
                            try {
                                if (new JSONObject(response).getBoolean("success")) {
                                    Toast.makeText(RegisterActivity.this, "Account Successfully Created", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else
                                    Toast.makeText(RegisterActivity.this, "Something Has Happened. Please Try Again!", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    requestQueue.add(registerRequest);
                }
            }
        });
    }

    private void convertBitmapToString(Bitmap profilePicture) {
        /*
            Base64 encoding requires a byte array, the bitmap image cannot be converted directly into a byte array.
            so first convert the bitmap image into a ByteArrayOutputStream and then convert this stream into a byte array.
        */
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        profilePicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] array = byteArrayOutputStream.toByteArray();
        profile = Base64.encodeToString(array, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
            //Image Successfully Selected
            try {
                //parsing the Intent data and displaying it in the imageview
                Uri imageUri = data.getData();//Geting uri of the data
                InputStream imageStream = getContentResolver().openInputStream(imageUri);//creating an imputstrea
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
        til_username = (TextInputLayout) findViewById(R.id.til_username_reg);
        til_password = (TextInputLayout) findViewById(R.id.til_password_reg);
        til_confirmPass = (TextInputLayout) findViewById(R.id.til_confirm_reg);
        til_mobile = (TextInputLayout) findViewById(R.id.til_mobile_reg);
        til_email = (TextInputLayout) findViewById(R.id.til_email_reg);
        bt_register = (Button) findViewById(R.id.bt_register);
        iv_profile = (ImageView) findViewById(R.id.im_profile);
    }

    private boolean validateUsername(String string) {
        if (string.equals("")) {
            til_username.setError("Enter A Username");
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
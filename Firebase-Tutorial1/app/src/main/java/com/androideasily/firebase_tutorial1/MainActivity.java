package com.androideasily.firebase_tutorial1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    TextView tv_name, tv_mobile, tv_email;
    ImageView imageView;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_name = (TextView) findViewById(R.id.textView9);
        tv_mobile = (TextView) findViewById(R.id.textView11);
        tv_email = (TextView) findViewById(R.id.textView13);
        imageView = (ImageView) findViewById(R.id.imageView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage =FirebaseStorage.getInstance();
        FirebaseUser firebaseUser= firebaseAuth.getCurrentUser();
        StorageReference storageReference = firebaseStorage.getReference("profile_images");
        if (firebaseUser != null) {
            tv_name.setText(firebaseUser.getDisplayName());
            tv_email.setText(firebaseUser.getEmail());
            Log.i("Provider Id",firebaseUser.getProviderId());
            Log.i("uid",firebaseUser.getUid());
            Log.i("photo url", String.valueOf(firebaseUser.getPhotoUrl()));
            Log.i("provider data", String.valueOf(firebaseUser.getProviderData()));
            Log.i("providers", String.valueOf(firebaseUser.getProviders()));
        }
    }
}

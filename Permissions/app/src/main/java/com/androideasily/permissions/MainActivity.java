package com.androideasily.permissions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androideasily.permissionmanager.PermissionManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private PermissionManager permissionManager;

    private static final String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.CAMERA,
            Manifest.permission.SEND_SMS
    };

    private static final int GROUP_REQUEST_CODE = 200;
    private static final int CALENDER_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int AUDIO_REQUEST_CODE = 2;
    private static final int SMS_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionManager = new PermissionManager(MainActivity.this);

        Button bt_group = (Button) findViewById(R.id.bt_group);
        Button bt_audio = (Button) findViewById(R.id.bt_record);
        Button bt_camera = (Button) findViewById(R.id.bt_camera);
        Button bt_calender = (Button) findViewById(R.id.bt_calender);
        Button bt_sms = (Button) findViewById(R.id.bt_message);

        bt_group.setOnClickListener(this);
        bt_audio.setOnClickListener(this);
        bt_calender.setOnClickListener(this);
        bt_camera.setOnClickListener(this);
        bt_sms.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_group:
                permissionManager.requestGroupPermission(GROUP_REQUEST_CODE, permissions);
                break;
            case R.id.bt_record:
                if (permissionManager.checkPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED)
                    permissionManager.getPermission(AUDIO_REQUEST_CODE, Manifest.permission.RECORD_AUDIO, "This App Need Camera Permission" +
                            "To Do Certain Task");
                else {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    String header = "This is my Header";
                    String message = "This is the content to share";
                    intent.putExtra(Intent.EXTRA_SUBJECT, header);
                    intent.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(intent, "Share Via"));
                }
                break;
            case R.id.bt_camera:
                if (permissionManager.checkPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
                    permissionManager.getPermission(CAMERA_REQUEST_CODE, Manifest.permission.CAMERA);
                else
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_calender:
                if (permissionManager.checkPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED)
                    permissionManager.getPermission(CALENDER_REQUEST_CODE, Manifest.permission.READ_CALENDAR);
                else
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_message:
                if (permissionManager.checkPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED)
                    permissionManager.getPermission(SMS_REQUEST_CODE, Manifest.permission.SEND_SMS);
                else
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissionManager.updatePermissions(requestCode, permissions, grantResults)) {
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
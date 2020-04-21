package com.a3solutions.e_college;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminActivity extends AppCompatActivity {

    Button bt_student,bt_faculty,bt_admin,bt_attendance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setTitle("Welcome " +getIntent().getStringExtra("name"));

        bt_student=(Button)findViewById(R.id.bt_student);
        bt_faculty=(Button)findViewById(R.id.bt_faculty);
        bt_admin=(Button)findViewById(R.id.bt_admin);
        bt_attendance=(Button)findViewById(R.id.bt_attendance);

        bt_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this,AddStudentActivity.class);
                startActivity(intent);
            }
        });
        bt_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this,AddFacultyActivity.class);
                startActivity(intent);
            }
        });
        bt_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this,AddAdminActivity.class);
                startActivity(intent);
            }
        });
        bt_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this,AdminAttendanceActivity.class);
                startActivity(intent);
            }
        });
    }
}
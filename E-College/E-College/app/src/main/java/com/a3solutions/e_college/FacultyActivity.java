package com.a3solutions.e_college;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FacultyActivity extends AppCompatActivity {

    Button bt_notes, bt_attendance,bt_viewatt,bt_questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty);
        setTitle("Welcome " + getIntent().getStringExtra("name"));

        bt_notes = (Button) findViewById(R.id.button6);
        bt_attendance = (Button) findViewById(R.id.button7);
        bt_viewatt = (Button) findViewById(R.id.button);
        bt_questions = (Button) findViewById(R.id.button9);
        bt_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FacultyActivity.this, UploadActivity.class));
            }
        });
        bt_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyActivity.this, AttendanceActivity.class);
                intent.putExtra("class", getIntent().getStringExtra("class"));
                intent.putExtra("subject", getIntent().getStringExtra("subject"));
                startActivity(intent);
            }
        });
        bt_viewatt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyActivity.this, ViewFacultyAttendance.class);
                intent.putExtra("class", getIntent().getStringExtra("class"));
                intent.putExtra("subject", getIntent().getStringExtra("subject"));
                startActivity(intent);
            }
        });
        bt_questions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyActivity.this, AnswerActivity.class);
                intent.putExtra("class", getIntent().getStringExtra("class"));
                intent.putExtra("faculty","true");
                startActivity(intent);
            }
        });
    }
}

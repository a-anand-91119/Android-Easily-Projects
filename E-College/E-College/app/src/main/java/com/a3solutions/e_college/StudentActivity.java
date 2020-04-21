package com.a3solutions.e_college;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StudentActivity extends AppCompatActivity {

    Button bt_notes, bt_attendance, bt_ask, answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        setTitle("Welcome " + getIntent().getStringExtra("name"));
        bt_notes = (Button) findViewById(R.id.button10);
        bt_attendance = (Button) findViewById(R.id.button11);
        bt_ask = (Button) findViewById(R.id.button2);
        answer = (Button) findViewById(R.id.button4);
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, AnswerActivity.class);
                intent.putExtra("class", getIntent().getStringExtra("class"));
                intent.putExtra("faculty","false");
                startActivity(intent);
            }
        });
        bt_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, ViewNotesActivity.class);
                intent.putExtra("class", getIntent().getStringExtra("class"));
                startActivity(intent);
            }
        });
        bt_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, ViewAttendanceActivity.class);
                intent.putExtra("class", getIntent().getStringExtra("class"));
                intent.putExtra("student_id", getIntent().getStringExtra("student_id"));
                startActivity(intent);
            }
        });
        bt_ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, AskQuestionActivity.class);
                intent.putExtra("class", getIntent().getStringExtra("class"));
                startActivity(intent);
            }
        });
    }
}

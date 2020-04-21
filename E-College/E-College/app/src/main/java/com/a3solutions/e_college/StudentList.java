package com.a3solutions.e_college;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Anand on 24-04-2017.
 */

public class StudentList extends ArrayAdapter<String> {
    private ArrayList<String> studentId;
    private ArrayList<String> studentName;
    private Activity context;
    boolean[] present;

    public StudentList(Activity context, ArrayList<String> studentId, ArrayList<String> studentName) {
        super(context, R.layout.student_list_layout, studentId);
        this.context = context;
        this.studentId = studentId;
        this.studentName = studentName;
        present=new boolean[studentId.size()];
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.student_list_layout, null, true);
        TextView tv_stu_name = (TextView) listViewItem.findViewById(R.id.tv_stu_name);
        CheckBox cb_stu_present = (CheckBox) listViewItem.findViewById(R.id.cb_stu_present);
        tv_stu_name.setText(studentName.get(position));
        cb_stu_present.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    present[position] = true;
                else
                    present[position] = false;
            }
        });
        return listViewItem;
    }

    public ArrayList<String> getStudentId() {
        return studentId;
    }

    public boolean[] getStatus() {
        return present;
    }
}
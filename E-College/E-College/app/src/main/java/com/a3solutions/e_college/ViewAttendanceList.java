package com.a3solutions.e_college;

import android.app.Activity;
import android.graphics.Color;
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

public class ViewAttendanceList extends ArrayAdapter<String> {
    private String subjectName;
    private String[] date;
    private String[] status;
    private Activity context;

    public ViewAttendanceList(Activity context, String subjectName, String[] date, String[] status) {
        super(context, R.layout.view_attendance_layout, date);
        this.context = context;
        this.date = date;
        this.status = status;
        this.subjectName = subjectName;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.view_attendance_layout, null, true);
        TextView tv_name = (TextView) listViewItem.findViewById(R.id.textView4);
        TextView tv_date = (TextView) listViewItem.findViewById(R.id.textView6);
        tv_name.setText(subjectName);
        tv_date.setText(date[position]);
        if (status[position].equals("true"))
            listViewItem.setBackgroundResource(R.color.present);
        else
            listViewItem.setBackgroundResource(R.color.absent);
        return listViewItem;
    }
}
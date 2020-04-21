package com.a3solutions.e_college;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Anand on 28-04-2017.
 */

public class QuestionList extends ArrayAdapter<String> {
    private ArrayList<String> questionId;
    private ArrayList<String> question;
    private Activity context;

    public QuestionList(Activity context, ArrayList<String> questionId, ArrayList<String> question) {
        super(context, R.layout.student_list_layout, questionId);
        this.context = context;
        this.questionId = questionId;
        this.question = question;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.question_list, null, true);
        TextView tv_question = (TextView) listViewItem.findViewById(R.id.textView8);
        tv_question.setText(question.get(position));
        return listViewItem;
    }
}
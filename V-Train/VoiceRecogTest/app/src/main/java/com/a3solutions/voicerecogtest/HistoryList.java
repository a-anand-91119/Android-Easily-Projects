package com.a3solutions.voicerecogtest;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Anand on 16-04-2017.
 */

public class HistoryList extends ArrayAdapter<String> {
    private ArrayList<String> trainClass, pnr, fromStation, toStation, date, trainName;
    private Activity context;

    public HistoryList(Activity context, ArrayList<String> trainClass, ArrayList<String> pnr, ArrayList<String> fromStation, ArrayList<String> toStation, ArrayList<String> date, ArrayList<String> trainName) {
        super(context, R.layout.history_list, pnr);
        this.context = context;
        this.trainClass = trainClass;
        this.pnr = pnr;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.date = date;
        this.trainName = trainName;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View historyItem = layoutInflater.inflate(R.layout.history_list, null, true);
        TextView tv_train_class = (TextView) historyItem.findViewById(R.id.tv_train_class);
        TextView tv_train_pnr = (TextView) historyItem.findViewById(R.id.tv_train_pnr);
        tv_train_class.setText(date.get(position));
        tv_train_pnr.setText(trainName.get(position));
        return historyItem;
    }
}

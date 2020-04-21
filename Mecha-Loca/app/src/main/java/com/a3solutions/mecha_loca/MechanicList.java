package com.a3solutions.mecha_loca;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Anand on 14-04-2017.
 */

public class MechanicList extends ArrayAdapter<String> {
    private ArrayList<String> userId;
    private ArrayList<String> userName;
    private ArrayList<String> latitude;
    private ArrayList<String> longitude;
    private ArrayList<String> mobile;
    private ArrayList<String> location;
    private Activity context;

    public MechanicList(Activity context, ArrayList<String> userId, ArrayList<String> latitude, ArrayList<String> longitude, ArrayList<String> userName, ArrayList<String> mobile, ArrayList<String> location) {
        super(context, R.layout.mechanic_list_layout, userId);
        this.context = context;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mobile = mobile;
        this.location = location;
        this.userName = userName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.mechanic_list_layout, null, true);
        TextView mtv_mobile = (TextView) listViewItem.findViewById(R.id.textView5);
        TextView mtv_location = (TextView) listViewItem.findViewById(R.id.textView7);
        TextView mtv_userName = (TextView) listViewItem.findViewById(R.id.mtv_userName);
        mtv_mobile.setText(context.getString(R.string.mob) + mobile.get(position));
        mtv_location.setText(location.get(position));
        mtv_userName.setText(userName.get(position));
        return listViewItem;
    }

    public void remove(int position) {
        userId.remove(position);
        userName.remove(position);
        latitude.remove(position);
        longitude.remove(position);
        mobile.remove(position);
        location.remove(position);
    }
}
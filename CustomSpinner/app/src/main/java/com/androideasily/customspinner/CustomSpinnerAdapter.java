package com.androideasily.customspinner;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anand on 27-06-2017.
 */

class CustomSpinnerAdapter extends ArrayAdapter<SpinnerData> {
    //the class extends array adapter which holds the spinner data class

    private Context context;
    private List<SpinnerData> spinnerDatas;

    CustomSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<SpinnerData> spinnerDatas) {
        super(context, resource, spinnerDatas);
        this.context = context;
        this.spinnerDatas = spinnerDatas;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return mySpinnerCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return mySpinnerCustomView(position, convertView, parent);
    }

    private View mySpinnerCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Getting the Layout Inflater Service from the system
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflating out custom spinner view
        View customView = layoutInflater.inflate(R.layout.spinner_layout, parent, false);
        //Declaring and initializing the widgets in custom layout
        ImageView imageView = (ImageView) customView.findViewById(R.id.icon);
        TextView textView = (TextView) customView.findViewById(R.id.iconName);
        //displaying the data
        imageView.setImageResource(spinnerDatas.get(position).getIcon());
        textView.setText(spinnerDatas.get(position).getIconName());
        return customView;
    }
}

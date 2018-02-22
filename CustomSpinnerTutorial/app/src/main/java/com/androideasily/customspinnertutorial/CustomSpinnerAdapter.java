package com.androideasily.customspinnertutorial;

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

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Anand on 28-06-2017.
 */

public class CustomSpinnerAdapter extends ArrayAdapter<SpinnerData> {
    private Context context;
    private List<SpinnerData> spinnerDatas;

    public CustomSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, List<SpinnerData> spinnerDatas) {
        super(context, resource, spinnerDatas);
        this.context = context;
        this.spinnerDatas = spinnerDatas;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return myCustomSpinnerView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return myCustomSpinnerView(position, convertView, parent);
    }

    private View myCustomSpinnerView(int position, @Nullable View myView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.spinner_layout, parent, false);

        TextView textView = (TextView) customView.findViewById(R.id.textView3);
        ImageView imageView = (ImageView) customView.findViewById(R.id.imageView);

        textView.setText(spinnerDatas.get(position).getIconName());
        imageView.setImageResource(spinnerDatas.get(position).getIcon());

        return customView;
    }
}

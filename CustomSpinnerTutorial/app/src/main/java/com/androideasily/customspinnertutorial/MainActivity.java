package com.androideasily.customspinnertutorial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Spinner simpleSpinner, customSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleSpinner = (Spinner) findViewById(R.id.simple_spinner);
        customSpinner = (Spinner) findViewById(R.id.custom_spinner);

        final List<String> simpleList = new ArrayList<String>();
        simpleList.add("Nougat");
        simpleList.add("Marshmallow");
        simpleList.add("Lollipop");
        simpleList.add("Kitkat");
        simpleList.add("Jellybean");
        simpleList.add("Icecream Sandwich");
        simpleList.add("Gingerbread");

        ArrayAdapter<String> simpleAdapter = new ArrayAdapter<>(MainActivity.this,R.layout.support_simple_spinner_dropdown_item,simpleList);
        simpleSpinner.setAdapter(simpleAdapter);

        simpleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, simpleList.get(i), Toast.LENGTH_SHORT).show();
                // i is the position
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Custom Spinners
        final List<SpinnerData> customList1 = new ArrayList<>();
        customList1.add(new SpinnerData(R.drawable.ic_account_circle_black_48dp,"Account Circle"));
        customList1.add(new SpinnerData(R.drawable.ic_add_circle_black_48dp,"Add Circle"));
        customList1.add(new SpinnerData(R.drawable.ic_assessment_black_48dp,"Assessment"));

        //Initialize custom adapter
        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(MainActivity.this,R.layout.spinner_layout,customList1);
        customSpinner.setAdapter(customSpinnerAdapter);

        customSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, customList1.get(i).getIconName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
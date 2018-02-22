package com.androideasily.customspinner;

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

        //Initialize the two spinners
        simpleSpinner = (Spinner) findViewById(R.id.simple_spinner);
        customSpinner = (Spinner) findViewById(R.id.custom_spinner);

        /* Setting up the simple spinner
            First Create a List
            place the list inside and adapter
            place this adapter inside the spinner
         */
        final List<String> simpleList = new ArrayList<>();
        simpleList.add("Nougat");
        simpleList.add("Marshmallow");
        simpleList.add("Lollipop");
        simpleList.add("Kitkat");
        simpleList.add("JellyBean");
        simpleList.add("Ice Cream Sandwich");
        simpleList.add("Honeycomb");
        simpleList.add("Gingerbread");
        simpleList.add("Froyo");
        simpleList.add("Eclair");
        simpleList.add("Donut");
        //Adapter to hold the "simpleList" and it uses the "support_simple_spinner_dropdown_item" Layout
        ArrayAdapter<String> simpleAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, simpleList);

        //An alternate method to populate a spinner is from the string resources. here i have created a string-array "android_versions" in strings.xml
        //ArrayAdapter<String> simpleAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.android_versions));

        //Adding the adapter to the Simple Spinner
        simpleSpinner.setAdapter(simpleAdapter);

        //Adding a listener to the simple spinner when an item is selected from the spinner
        simpleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, simpleList.get(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /* Setting up the CUSTOM SPINNER 1 (with ImageView)
            Here we need to make you of a Custom Class to hold the data - SpinnerData
            Then instead of using the simple adapter as used above, we create another class - a Custom Adapter Class (CustomSpinnerAdapter)
            Create an object of the adapter class and add it to the custom spinner
         */
        //Prepare the custom list
        final List<SpinnerData> customList1 = new ArrayList<>();//the customList1 holds a list of objects of the class SpinnerData.
        customList1.add(new SpinnerData("Account Circle", R.drawable.ic_account_circle_black_48dp));
        customList1.add(new SpinnerData("Add Circle", R.drawable.ic_add_circle_black_48dp));
        customList1.add(new SpinnerData("Arrow Forward", R.drawable.ic_arrow_forward_black_24dp));
        customList1.add(new SpinnerData("Assessment", R.drawable.ic_assessment_black_48dp));
        customList1.add(new SpinnerData("Error", R.drawable.ic_error_black_48dp));
        customList1.add(new SpinnerData("Error Outline", R.drawable.ic_error_outline_black_48dp));
        customList1.add(new SpinnerData("Image", R.drawable.ic_image_black_48dp));
        customList1.add(new SpinnerData("Lock", R.drawable.ic_lock_black_48dp));

        //Instantiate out custom spinner class
        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(MainActivity.this, R.layout.spinner_layout, customList1);
        customSpinner.setAdapter(customSpinnerAdapter);

        //Adding a listener to the custom spinner when an item is selected from the spinner
        customSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Displaying the selected icon name in a Toast
                Toast.makeText(MainActivity.this, customList1.get(i).getIconName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}

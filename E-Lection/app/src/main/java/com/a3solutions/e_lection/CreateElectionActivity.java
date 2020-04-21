package com.a3solutions.e_lection;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class CreateElectionActivity extends AppCompatActivity {

    private int mYear;
    private int mMonth;
    private int mDay;
    private static int DATE_DIALOG_ID;
    private Button submit;
    private TextInputLayout til_election_name, til_start, til_end;
    private TextInputEditText et_election_name, et_start, et_end;
    private String election_name, start, end;
    private UUID election_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_election);

        //TODO error with date needs to be fixed
        initialize();
        election_id= UUID.randomUUID();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        et_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DATE_DIALOG_ID = 1;
                showDialog(DATE_DIALOG_ID);
            }
        });
        et_end.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DATE_DIALOG_ID = 0;
                showDialog(DATE_DIALOG_ID);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                election_name = et_election_name.getText().toString();
                start = et_start.getText().toString();
                end = et_end.getText().toString();
                if (validateElectionName() && validateStartDate() && validateEndDate()) {
                    final AlertDialog.Builder confirmInput = new AlertDialog.Builder(CreateElectionActivity.this);
                    confirmInput.setTitle("Confirm Details");
                    confirmInput.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            uploadNewElection(election_name, start, end);
                            dialogInterface.dismiss();
                        }
                    });
                    confirmInput.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    LayoutInflater inflater = (CreateElectionActivity.this).getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.confirm, null);
                    TextView election_name2 = (TextView) dialogView.findViewById(R.id.textView2);
                    TextView start_date = (TextView) dialogView.findViewById(R.id.textView6);
                    TextView end_date = (TextView) dialogView.findViewById(R.id.textView8);
                    election_name2.setText(election_name);
                    start_date.setText(start);
                    end_date.setText(end);
                    confirmInput.setView(dialogView);
                    confirmInput.create().show();
                }
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
    }

    private void updateDisplay(int id) {
        if (id == 1) {
            et_start.setText(
                    new StringBuilder()
                            .append(mYear).append("/")
                            .append(mMonth + 1).append("/")
                            .append(mDay).append(""));
        } else {
            et_end.setText(
                    new StringBuilder()
                            .append(mYear).append("/")
                            .append(mMonth + 1).append("/")
                            .append(mDay).append(""));
        }
    }

    private final DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay(DATE_DIALOG_ID);
                }
            };

    private void uploadNewElection(String election_name, String start, String end) {
        final ProgressDialog progressDialog = new ProgressDialog(CreateElectionActivity.this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Creating Election");
        progressDialog.show();
        Response.Listener<String> uploadElection = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    final String election_id = jsonResponse.getString("election_id");
                    progressDialog.hide();
                    progressDialog.dismiss();
                    if (jsonResponse.getBoolean("success")) {
                        finish();
                    } else {
                        //TODO Election Exist. Deal With It Properly
                        Toast.makeText(CreateElectionActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ElectionRequest electionRequest = new ElectionRequest(election_id,election_name, start, end, uploadElection);
        RequestQueue requestQueue = Volley.newRequestQueue(CreateElectionActivity.this);
        requestQueue.add(electionRequest);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private Boolean validateElectionName() {
        if (et_election_name.getText().toString().trim().isEmpty()) {
            til_election_name.setError("Enter A Valid Election Name");
            requestFocus(et_election_name);
            return false;
        } else {
            til_election_name.setErrorEnabled(false);
        }
        return true;
    }

    private Boolean validateStartDate() {
        if (et_start.getText().toString().trim().isEmpty()) {
            til_start.setError("Enter The Date When Voting Can Begin");
            requestFocus(et_start);
            return false;
        } else {
            til_start.setErrorEnabled(false);
        }
        return true;
    }

    private Boolean validateEndDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/mm/dd",Locale.getDefault());
        Log.i("TIME",simpleDateFormat.toString());
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = simpleDateFormat.parse(et_start.getText().toString());
            endDate = simpleDateFormat.parse(et_end.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (et_end.getText().toString().trim().isEmpty()) {
            til_end.setError("Enter The Date When Voting Ends");
            requestFocus(et_end);
            return false;
        } else if (startDate.after(endDate)) {
            til_end.setError("Invalid Ending Date");
            requestFocus(et_end);
            return false;
        } else {
            til_end.setErrorEnabled(false);
        }
        return true;
    }

    private void initialize() {
        til_election_name = (TextInputLayout) findViewById(R.id.til_election_name);
        til_start = (TextInputLayout) findViewById(R.id.til_start);
        til_end = (TextInputLayout) findViewById(R.id.til_end);
        et_election_name = (TextInputEditText) findViewById(R.id.et_election_name);
        et_start = (TextInputEditText) findViewById(R.id.et_start);
        et_end = (TextInputEditText) findViewById(R.id.et_end);
        submit = (Button) findViewById(R.id.submit);
    }

}
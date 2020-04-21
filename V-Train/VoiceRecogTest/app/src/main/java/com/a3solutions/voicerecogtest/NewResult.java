package com.a3solutions.voicerecogtest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.satsuware.usefulviews.LabelledSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class NewResult extends Activity {

    private TextView tv_num_ticket, tv_date;
    private LabelledSpinner sp_fromStation, sp_toStation;
    private int fromPosition;
    private int toPosition;
    @SuppressLint("UseSparseArrays")
    private final HashMap<Integer, String> spinnerMapTo = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private final HashMap<Integer, String> spinnerMapFrom = new HashMap<>();
    ArrayList<String> from_station_list, to_station_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_result);

        tv_num_ticket = (TextView) findViewById(R.id.tv_num_ticket);
        tv_date = (TextView) findViewById(R.id.tv_date);
        sp_fromStation = (LabelledSpinner) findViewById(R.id.sp_from_station);
        sp_toStation = (LabelledSpinner) findViewById(R.id.sp_to_station);
        sp_fromStation.setColor(R.color.white);
        sp_toStation.setColor(R.color.white);
        Button confirmBooking = (Button) findViewById(R.id.bt_confirm);
        confirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progress = new ProgressDialog(NewResult.this);
                progress.setMessage("Fetching Trains Between Selected Stations");
                progress.setCancelable(false);
                progress.show();
                final String date = tv_date.getText().toString().trim();
                Log.e("date", date);
                GetTrainsRequest getTrainsRequest = new GetTrainsRequest(spinnerMapFrom.get(fromPosition), spinnerMapTo.get(toPosition), date.substring(0, (date.lastIndexOf("-"))), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("TRAINS RESPONSE", response);
                        progress.dismiss();
                        progress.hide();
                        Intent intent = new Intent(NewResult.this, TrainDetails.class);
                        intent.putExtra("response", response);
                        intent.putExtra("title", spinnerMapFrom.get(fromPosition) + " - " + spinnerMapTo.get(toPosition));
                        intent.putExtra("date", date);
                        intent.putExtra("from", from_station_list.get(fromPosition));
                        intent.putExtra("to", to_station_list.get(toPosition));
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        progress.hide();
                        if (error instanceof NetworkError) {
                            Toast.makeText(NewResult.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(NewResult.this, "No Connection Available", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(NewResult.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(NewResult.this, "Server Error", Toast.LENGTH_SHORT).show();
                        }
                        error.printStackTrace();
                    }
                });
                RequestQueue requestQueue = RequestQueueCreate.getInstance(getApplicationContext()).getRequestQueue();
                requestQueue.add(getTrainsRequest);
            }
        });

        sp_toStation.setOnItemChosenListener(new LabelledSpinner.OnItemChosenListener() {
            @Override
            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
                toPosition = adapterView.getSelectedItemPosition();
            }

            @Override
            public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {

            }
        });

        sp_fromStation.setOnItemChosenListener(new LabelledSpinner.OnItemChosenListener() {
            @Override
            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
                fromPosition = adapterView.getSelectedItemPosition();
            }

            @Override
            public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {

            }
        });
        String response = getIntent().getStringExtra("response");
        parseJSON(response);
    }

    private void parseJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String numTicket = jsonObject.getString("numTicket");
            if (numTicket.equals("-1")) {
                Toast.makeText(this, "Failed To Recognize Number Of Tickets. Please Try Again!!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(NewResult.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            tv_num_ticket.setText(numTicket);
            Log.i("NUM TICKET", String.valueOf(numTicket));
            int x = jsonObject.length();
            Log.i("LENGTH OF OBJECT", String.valueOf(x));
            int key = 0;
            for (int i = 2; i <= x; i++) {
                JSONObject value = new JSONObject(jsonObject.getString(String.valueOf(key)));
                if (value.getString("type").equals("fromStation")) {
                    parseFromStation(value);
                }
                if (value.getString("type").equals("toStation")) {
                    parseToStation(value);
                }
                if (value.getString("type").equals("date")) {
                    parseDate(value);
                }
                key += 1;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseFromStation(JSONObject fromStation) {
        Log.i("FROM STATION", fromStation.toString());
        try {
            if (fromStation.getInt("response_code") == 200) {
                JSONArray stationList = fromStation.getJSONArray("station");
                from_station_list = new ArrayList<>();
                for (int i = 0; i < stationList.length(); i++) {
                    JSONObject station = new JSONObject(stationList.getJSONObject(i).toString());
                    from_station_list.add(station.getString("fullname"));
                    spinnerMapFrom.put(i, station.getString("code"));
                }
                sp_fromStation.setItemsArray(from_station_list);
            } else if (fromStation.getInt("response_code") == 204) {
                Toast.makeText(this, R.string.error_station_from, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(NewResult.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseToStation(JSONObject toStation) {
        Log.i("TO STATION", toStation.toString());
        try {
            if (toStation.getInt("response_code") == 200) {
                JSONArray stationList = toStation.getJSONArray("station");
                to_station_list = new ArrayList<>();
                for (int i = 0; i < stationList.length(); i++) {
                    JSONObject station = new JSONObject(stationList.getJSONObject(i).toString());
                    to_station_list.add(station.getString("fullname"));
                    spinnerMapTo.put(i, station.getString("code"));
                }
                sp_toStation.setItemsArray(to_station_list);
            } else if (toStation.getInt("response_code") == 204) {
                Toast.makeText(this, R.string.error_station_to, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(NewResult.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseDate(JSONObject dateObject) {
        Log.i("DATE", dateObject.toString());
        try {
            if (dateObject.getString("status").equals("valid")) {
                tv_date.setText(dateObject.getString("dateJourney"));
            } else {
                Toast.makeText(this, R.string.error_date, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(NewResult.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

package com.a3solutions.voicerecogtest;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

public class HistoryActivity extends AppCompatActivity {

    ListView history;
    TextView header;
    ArrayList<String> trainClass = new ArrayList<>(),
            trainPnr = new ArrayList<>(),
            fromStation = new ArrayList<>(),
            toStation = new ArrayList<>(),
            trainName = new ArrayList<>(),
            date = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        header = (TextView) findViewById(R.id.textView5);
        history = (ListView) findViewById(R.id.lv_history);
        history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder detailsDialog = new AlertDialog.Builder(HistoryActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View expandedView = inflater.inflate(R.layout.expanded_history, null);
                detailsDialog.setView(expandedView);
                AutofitTextView hl_name, hl_pnr, hl_from, hl_to;
                TextView hl_class, hl_date;

                hl_name = (AutofitTextView) expandedView.findViewById(R.id.tv_hl_name);
                hl_class = (TextView) expandedView.findViewById(R.id.tv_hl_class);
                hl_pnr = (AutofitTextView) expandedView.findViewById(R.id.tv_hl_pnr);
                hl_from = (AutofitTextView) expandedView.findViewById(R.id.tv_hl_from);
                hl_to = (AutofitTextView) expandedView.findViewById(R.id.tv_hl_to);
                hl_date = (TextView) expandedView.findViewById(R.id.tv_hl_date);

                hl_name.setText(trainName.get(position));
                hl_class.setText(trainClass.get(position));
                hl_pnr.setText(String.format("%s%s", getString(R.string.pnr), trainPnr.get(position)));
                hl_from.setText(String.format("%s%s", getString(R.string.to), fromStation.get(position)));
                hl_to.setText(String.format("%s%s", getString(R.string.frm), toStation.get(position)));
                hl_date.setText(String.format("%s%s", getString(R.string.doj), date.get(position)));
                detailsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                detailsDialog.create().show();
            }
        });
        final ProgressDialog progressDialog = new ProgressDialog(HistoryActivity.this);
        progressDialog.setMessage("Fetching Booking History");
        progressDialog.show();

        RequestQueue requestQueue = RequestQueueCreate.getInstance(getApplicationContext()).getRequestQueue();
        HistoryRequest historyRequest = new HistoryRequest(getIntent().getStringExtra("user_id"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("success"))
                        header.setText(R.string.no_booking_found);
                    else {
                        header.setText(R.string.past_booking);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject subObject = jsonArray.getJSONObject(i);
                            trainClass.add(i, subObject.getString("class"));
                            trainPnr.add(i, subObject.getString("pnr"));
                            fromStation.add(i, subObject.getString("fromStation"));
                            toStation.add(i, subObject.getString("toStation"));
                            trainName.add(i, subObject.getString("trainName"));
                            date.add(i, subObject.getString("date"));
                        }
                        HistoryList historyList = new HistoryList(HistoryActivity.this, trainClass, trainPnr, fromStation, toStation, date, trainName);
                        history.setAdapter(historyList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                progressDialog.hide();
                if (error instanceof NetworkError) {
                    Toast.makeText(HistoryActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(HistoryActivity.this, "No Connection Available", Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(HistoryActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(HistoryActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestQueue.add(historyRequest);
    }

}

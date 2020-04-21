package com.a3solutions.voicerecogtest;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Anand on 29-03-2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private final List<ListItem> items;
    private final Context context;
    private String selectedClass;
    String user_id = MainActivity.user_id;
    String fromStation, toStation, dateJourney;

    public CardAdapter(String fromStation, String toStation, String dateJourney, String trainId[], String[] trainName, String[] arriveTime, String[] departTime, JSONArray[] classes, Context context) {
        super();
        this.context = context;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.dateJourney = dateJourney;
        items = new ArrayList<>();
        for (int i = 0; i < trainId.length; i++) {
            ListItem item = new ListItem();
            item.setTrainId(trainId[i]);
            item.setTrainName(trainName[i]);
            item.setArrivetime(arriveTime[i]);
            item.setDepartTime(departTime[i]);
            item.setClasses(classes[i]);
            items.add(item);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_train_details, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ListItem list = items.get(position);
        holder.trainId.setText(list.getTrainId());
        holder.trainName.setText(list.getTrainName());
        holder.departTime.setText(list.getDepartTime());
        holder.arriveTime.setText(list.getArrivetime());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());
                alertDialog.setTitle("Select Your Class");
                alertDialog.setPositiveButton("Book", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final ProgressDialog progress = new ProgressDialog(context);
                        progress.setTitle("Please Wait");
                        progress.setMessage("Booking Your Ticket");
                        progress.setCancelable(false);
                        progress.show();
                        UUID pnr = UUID.randomUUID();
                        BookRequest bookRequest = new BookRequest(list.getTrainName(), fromStation, toStation, dateJourney, selectedClass, pnr, user_id, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progress.dismiss();
                                progress.hide();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getBoolean("success")) {
                                        Toast.makeText(context, "Successfully Booked Ticket", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(context, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        context.startActivity(intent);
                                    } else {
                                        Toast.makeText(context, "Error!! Please Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progress.dismiss();
                                progress.hide();
                                error.printStackTrace();
                            }
                        });
                        RequestQueue requestQueue = RequestQueueCreate.getInstance(context.getApplicationContext()).getRequestQueue();
                        requestQueue.add(bookRequest);
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                LayoutInflater layoutInflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") View confirmView = layoutInflater.inflate(R.layout.class_select, null);
                final Spinner classSelect = (Spinner) confirmView.findViewById(R.id.sp_class_select);
                ArrayList<String> array = getClassesArray(list.getClasses());
                classSelect.setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, array));
                classSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedClass = classSelect.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                alertDialog.setView(confirmView);
                alertDialog.create().show();
            }
        });
    }

    private ArrayList<String> getClassesArray(JSONArray classes) {
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < classes.length(); i++) {
            try {
                JSONObject jsonObj = classes.getJSONObject(i);
                if (jsonObj.getString("available").equals("Y")) {
                    temp.add(jsonObj.getString("class-code"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return temp;
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView trainId;
        public final TextView trainName;
        public final TextView departTime;
        public final TextView arriveTime;
        public final CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cv);
            trainId = (TextView) itemView.findViewById(R.id.tv_trainid);
            trainName = (TextView) itemView.findViewById(R.id.tv_trainname);
            departTime = (TextView) itemView.findViewById(R.id.tv_departtime);
            arriveTime = (TextView) itemView.findViewById(R.id.tv_arrivaltime);
        }
    }


}
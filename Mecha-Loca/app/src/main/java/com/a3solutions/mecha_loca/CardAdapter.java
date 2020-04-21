package com.a3solutions.mecha_loca;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anand on 13-04-2017.
 */

class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    List<ListItem> items;
    String userName;
    String location;
    private Context context;

    public CardAdapter(String[] nrating,  String[] rating, Double userLatitude, Double userLongitude, String userid, String mechId[], String[] id, String[] distance, String[] mechanicName, String[] longitude, String[] latitude, String userName, Context context) {
        super();
        this.context = context;
        items = new ArrayList<ListItem>();
        for (int i = 0; i < id.length; i++) {
            ListItem item = new ListItem();
            item.setId(id[i]);
            item.setDistance(distance[i]);
            item.setLatitude(latitude[i]);
            item.setLongitude(longitude[i]);
            item.setMechanicName(mechanicName[i]);
            item.setMechId(mechId[i]);
            item.setUserId(userid);
            item.setUserLatitude(userLatitude.toString());
            item.setUserLongitude(userLongitude.toString());
            item.setRating(rating[i]);
            item.setNrating(nrating[i]);
            items.add(item);
        }
        this.userName = userName;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mechanic_details, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ListItem list = items.get(position);
        holder.id.setText(list.getId());
        holder.mechanicName.setText(list.getMechanicName());
        holder.latitude.setText("Latitude: " + list.getLatitude().substring(0, list.getLatitude().indexOf(".") + 2));
        holder.longitude.setText("Longitude: " + list.getLongitude().substring(0, list.getDistance().indexOf(".") + 2));
        holder.distance.setText(list.getDistance().substring(0, list.getDistance().indexOf(".") + 2) + " Km");
        holder.ratingBar.setRating(Float.parseFloat(list.getRating()));
        holder.nrating.setText("("+list.getNrating()+")");
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Registering Help Request");
                progressDialog.show();
                String URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + list.getUserLatitude() + "," + list.getUserLongitude() + "&key=AIzaSyA_pXOjKWu25m5EWscKgfF9zabT1nSMyzQ";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("LOCAITON NAME", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("OK")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("results");
                                JSONObject newJsonObject = jsonArray.getJSONObject(0);
                                location = newJsonObject.getString("formatted_address");
                            } else {
                                Toast.makeText(context, "Error In Reverse Geocoding", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        GetHelpRequest getHelpRequest = new GetHelpRequest(list.getMechId(), list.getUserId(), list.getUserLatitude(), list.getUserLongitude(), location, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                progressDialog.hide();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getBoolean("success")) {
                                        Toast.makeText(context, "Help Requested Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(context, UserActivity.class);
                                        intent.putExtra("user_id", list.getUserId());
                                        intent.putExtra("name", userName);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    } else {
                                        Toast.makeText(context, "Error!! Please Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        RequestQueue requestQueue = GetNet.getInstance(context.getApplicationContext()).getRequestQueue();
                        requestQueue.add(getHelpRequest);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            Toast.makeText(context, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(context, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(context, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(context, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                RequestQueue requestQueue = GetNet.getInstance(context.getApplicationContext()).getRequestQueue();
                requestQueue.add(stringRequest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView id, mechanicName, latitude, longitude, distance, nrating;
        CardView cardView;
        RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.tv_mechanic_id);
            mechanicName = (TextView) itemView.findViewById(R.id.tv_mechanic_name);
            latitude = (TextView) itemView.findViewById(R.id.tv_latitude);
            longitude = (TextView) itemView.findViewById(R.id.tv_longitude);
            nrating=(TextView)itemView.findViewById(R.id.textView8);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar2);
            distance = (TextView) itemView.findViewById(R.id.tv_distance_km);
            cardView = (CardView) itemView.findViewById(R.id.cv);
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        }
    }

}
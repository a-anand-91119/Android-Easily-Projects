package com.androideasily.networkinglibraries;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "URL", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        ClassRequest classRequest = new ClassRequest(Request.Method.POST, "URL", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        classRequest.setPriority(Request.Priority.HIGH);

        classRequest.setTag("toCancel");
        requestQueue.cancelAll("toCancel");

        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;//Cancels all the requests in the queue
            }
        });

        requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);

        Cache.Entry entry = requestQueue.getCache().get("URL");
        if(entry!=null){
            //Response available in cache
        }
        else{
            //Perform Network Request
        }
        stringRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.getCache().invalidate("URL",true);
    }
}

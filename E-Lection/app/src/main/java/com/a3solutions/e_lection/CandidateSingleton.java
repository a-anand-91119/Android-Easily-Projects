package com.a3solutions.e_lection;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Anand on 25-02-2017.
 */

public class CandidateSingleton {
    private static CandidateSingleton instance;
    private RequestQueue requestQueue;
    private Context context;
    private ImageLoader imageLoader;

    private CandidateSingleton(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized CandidateSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new CandidateSingleton(context);
        }
        return instance;
    }
    public ImageLoader getImageLoader(){
        getRequestQueue();
        if(imageLoader ==  null){
            imageLoader = new ImageLoader(this.requestQueue, new LruBitmapCache());
        }

        return this.imageLoader;
    }
    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }
    public <T> void addToRequestQueue(Request<T> request){
        requestQueue.add(request);
    }
}

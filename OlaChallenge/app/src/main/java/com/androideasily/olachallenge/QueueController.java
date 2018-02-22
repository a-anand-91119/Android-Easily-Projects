package com.androideasily.olachallenge;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Anand on 16-12-2017.
 */

//Singelton class for one instance for the requestqueue and favourite preference
public class QueueController {
    private static QueueController queueControllerInstance = null;
    private final RequestQueue requestQueue;
    private final FavouritePreference favouritePreference;

    //emthod to provide the quecontroller instance
    public static synchronized QueueController getInstance(Context context) {
        if (queueControllerInstance == null) {
            queueControllerInstance = new QueueController(context);
        }
        return queueControllerInstance;
    }

    //private contrutor
    private QueueController(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        favouritePreference = new FavouritePreference(context);
    }

    //provinding request queue instance
    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    //providing FavouritePreference instance
    public FavouritePreference getFavouritePreference() {
        return favouritePreference;
    }
}

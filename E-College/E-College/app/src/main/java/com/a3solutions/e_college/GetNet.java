package com.a3solutions.e_college;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Anand on 24-04-2017.
 */

public class GetNet {
    private static GetNet myInstance;
    private final RequestQueue requestQueue;

    public static synchronized GetNet getInstance(Context context) {
        if (myInstance == null) {
            myInstance = new GetNet(context);
        }
        return myInstance;
    }

    private GetNet(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}

package com.a3solutions.mecha_loca;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Anand on 12-04-2017.
 */

public class GetNet {

    private static GetNet ourInstance;
    private final RequestQueue requestQueue;

    public static synchronized GetNet getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new GetNet(context);
        }
        return ourInstance;
    }

    private GetNet(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}

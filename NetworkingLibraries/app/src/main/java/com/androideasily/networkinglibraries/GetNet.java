package com.androideasily.networkinglibraries;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Anand on 11-06-2017.
 */

public class GetNet {

    private static GetNet mInstance;
    private RequestQueue requestQueue;

    private GetNet(Context context) {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context);
    }

    public static synchronized GetNet getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GetNet(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}

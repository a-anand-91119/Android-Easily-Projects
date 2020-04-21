package com.a3solutions.voicerecogtest;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Anand on 19-03-2017.
 */

public class RequestQueueCreate {
    private static RequestQueueCreate queueInstance = null;
    private final RequestQueue requestQueue;

    public static synchronized RequestQueueCreate getInstance(Context context) {
        if (queueInstance == null) {
            queueInstance = new RequestQueueCreate(context);
        }
        return queueInstance;
    }

    private RequestQueueCreate(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}

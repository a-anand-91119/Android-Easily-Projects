package com.androideasily.networkinglibraries;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 10-06-2017.
 */

public class ClassRequest extends StringRequest {
    private Map<String, String> params;
    private Priority priority;

    public ClassRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        params = new HashMap<>();
        params.put("key", "value");
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public Priority getPriority() {
        return priority;
    }
}

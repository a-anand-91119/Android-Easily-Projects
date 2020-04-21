package com.a3solutions.voicerecogtest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 28-03-2017.
 */

class GetTrainsRequest extends StringRequest {
    private static final String URL="http://androideasily.in/v_ticket/trainstation.php";
    private final Map<String,String> params;
    public GetTrainsRequest(String fromCode,String toCode,String date,Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, URL, listener, errorListener);
        params=new HashMap<>();
        params.put("from_code",fromCode);
        params.put("to_code",toCode);
        params.put("date",date);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}

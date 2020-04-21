package com.a3solutions.voicerecogtest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 26-03-2017.
 */

class SpeechRequest extends StringRequest {
    private static final String URL="http://androideasily.in/v_ticket/stationcheck.php";
    private final Map<String,String> params;
    public SpeechRequest(String speech,Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST,URL, listener, errorListener);
        params=new HashMap<>();
        params.put("speech",speech);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}

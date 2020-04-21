package com.a3solutions.mecha_loca;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Anand on 13-04-2017.
 */

public class CheckRequest extends StringRequest {
    private Map<String, String> params;

    public CheckRequest(String user_id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, "http://project6007.netai.net/mecha_loca/checkRequest.php", listener, errorListener);
        params = new HashMap<>();
        params.put("user_id", user_id);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

package com.a3solutions.mecha_loca;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 14-04-2017.
 */

public class DeleteRequest extends StringRequest {
    private static final String CHECK_URL = "http://project6007.netai.net/mecha_loca/deleteRequest.php";
    private Map<String, String> params;

    public DeleteRequest(String user_id, String mech_id, Response.Listener<String> listener) {
        super(Request.Method.POST, CHECK_URL, listener, null);
        params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("mech_id", mech_id);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

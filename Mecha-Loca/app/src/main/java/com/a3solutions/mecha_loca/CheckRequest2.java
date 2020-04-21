package com.a3solutions.mecha_loca;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 14-04-2017.
 */

public class CheckRequest2 extends StringRequest {
    private static final String CHECK_URL = "http://project6007.netai.net/mecha_loca/checkRequest2.php";
    private Map<String, String> params;

    public CheckRequest2(String mech_id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, CHECK_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("mech_id", mech_id);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

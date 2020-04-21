package com.a3solutions.mecha_loca;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 05-06-2017.
 */

class CancelRequest extends StringRequest {
    private static final String URL = "http://project6007.netai.net/mecha_loca/cancelrequest.php";
    private Map<String, String> params;

    public CancelRequest(String user_id, String mech_id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);
        params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("mech_id", mech_id);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
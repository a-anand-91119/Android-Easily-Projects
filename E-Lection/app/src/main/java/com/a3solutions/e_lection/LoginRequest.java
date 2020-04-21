package com.a3solutions.e_lection;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 13-02-2017.
 */

class LoginRequest extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "http://androiddevz.netai.net/e_lection/voter_login.php";
    private final Map<String, String> params;

    public LoginRequest(String voter_id, String password, Response.Listener<String> listener,Response.ErrorListener errorListener) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("voter_id", voter_id);
        params.put("password", password);
    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

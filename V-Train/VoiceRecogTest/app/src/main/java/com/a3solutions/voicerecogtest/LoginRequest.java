package com.a3solutions.voicerecogtest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 10-04-2017.
 */

public class LoginRequest extends StringRequest {
    private static final String LOGIN_URL="http://androideasily.in/v_ticket/login.php";
    private Map<String,String> params;

    public LoginRequest(String username, String password, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, LOGIN_URL, listener, errorListener);
        params=new HashMap<>();
        params.put("username",username);
        params.put("password",password);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}

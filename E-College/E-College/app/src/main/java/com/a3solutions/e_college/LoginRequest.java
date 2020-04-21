package com.a3solutions.e_college;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 24-04-2017.
 */

public class LoginRequest extends StringRequest {
    private static final String URL = "http://project700007.netai.net/college/login.php";
    private Map<String, String> params;

    public LoginRequest(String username, String password, Response.Listener<String> listener,String type, Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);
        params=new HashMap<>();
        params.put("username",username);
        params.put("password",password);
        params.put("type",type);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}

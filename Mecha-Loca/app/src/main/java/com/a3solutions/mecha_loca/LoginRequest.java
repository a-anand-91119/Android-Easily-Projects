package com.a3solutions.mecha_loca;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 12-04-2017.
 */

public class LoginRequest extends StringRequest {
    private static final String URL = "http://project6007.netai.net/mecha_loca/login.php";
    private Map<String, String> params;

    public LoginRequest(String username, String password, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);
        params=new HashMap<>();
        params.put("username",username);
        params.put("password",password);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}

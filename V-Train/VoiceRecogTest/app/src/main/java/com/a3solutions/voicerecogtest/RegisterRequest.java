package com.a3solutions.voicerecogtest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Anand on 16-04-2017.
 */

public class RegisterRequest extends StringRequest {
    private static final String LOGIN_URL="http://androideasily.in/v_ticket/register.php";
    private Map<String,String> params;

    public RegisterRequest(String username, String password, String name, String email, String mobile, UUID userId, Response.Listener<String> listener) {
        super(Request.Method.POST, LOGIN_URL, listener, null);
        params=new HashMap<>();
        params.put("username",username);
        params.put("password",password);
        params.put("name",name);
        params.put("email",email);
        params.put("mobile",mobile);
        params.put("user_id",userId.toString());
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}

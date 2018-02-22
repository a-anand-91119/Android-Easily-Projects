package com.androideasily.androideasily_login;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 16-06-2017.
 * Custom Request to register a new user
 */

public class RegisterRequest extends StringRequest {

    private static final String REGISTER_URL = "http://myresolvcoke.000webhostapp.com/register.php";
    private Map<String, String> parameters;

    public RegisterRequest(String username, String name, String password, String mobile, String email, String image, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("name", name);
        parameters.put("password", password);
        parameters.put("mobile", mobile);
        parameters.put("email", email);
        parameters.put("image", image);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}

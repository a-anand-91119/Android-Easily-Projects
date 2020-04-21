package com.a3solutions.mecha_loca;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Anand on 12-04-2017.
 */


public class RegisterRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "http://project6007.netai.net/mecha_loca/register.php";
    private Map<String, String> params;

    public RegisterRequest(String name, String username, String password, String email, UUID userId, String mobile, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("username", username);
        params.put("password", password);
        params.put("email", email);
        params.put("mobile", mobile);
        params.put("user_id", String.valueOf(userId));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

package com.a3solutions.e_college;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Anand on 28-04-2017.
 */

public class AddFacultyRequest extends StringRequest {
    private static final String URL = "http://project700007.netai.net/college/addfaculty.php";
    private Map<String, String> params;

    public AddFacultyRequest(String username, String password, String name, String Class, String subject, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("name", name);
        params.put("Class", Class);
        params.put("subject", subject);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}

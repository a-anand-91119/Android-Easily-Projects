package com.a3solutions.e_college;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 25-04-2017.
 */

public class StudentRequest extends StringRequest {
    private static final String UPLOAD_REQUEST_URL = "http://project700007.netai.net/college/getStudent.php";
    private Map<String, String> params;

    public StudentRequest(String className, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, UPLOAD_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("className", className);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

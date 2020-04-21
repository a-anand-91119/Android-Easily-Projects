package com.a3solutions.e_college;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 28-04-2017.
 */

public class AttendanceFacultyFetch extends StringRequest {
    private static final String UPLOAD_REQUEST_URL = "http://project700007.netai.net/college/attendancefacultyfetch.php";
    private Map<String, String> params;

    public AttendanceFacultyFetch(String className,String subject, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, UPLOAD_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("className", className);
        params.put("subject", subject);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

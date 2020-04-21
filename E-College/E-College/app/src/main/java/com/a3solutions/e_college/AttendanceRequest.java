package com.a3solutions.e_college;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 24-04-2017.
 */

public class AttendanceRequest extends StringRequest {
    private static final String UPLOAD_REQUEST_URL = "http://project700007.netai.net/college/attendance.php";
    private Map<String, String> params;

    public AttendanceRequest(String list, Response.Listener<String> listener) {
        super(Request.Method.POST, UPLOAD_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("list", list);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

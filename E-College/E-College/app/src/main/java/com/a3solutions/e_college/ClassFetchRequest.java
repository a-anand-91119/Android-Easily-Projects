package com.a3solutions.e_college;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 28-04-2017.
 */

public class ClassFetchRequest extends StringRequest {
    private static final String UPLOAD_REQUEST_URL = "http://project700007.netai.net/college/getclass.php";
    private Map<String, String> params;

    public ClassFetchRequest(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, UPLOAD_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

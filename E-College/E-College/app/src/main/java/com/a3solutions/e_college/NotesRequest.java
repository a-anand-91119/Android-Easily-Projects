package com.a3solutions.e_college;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 25-04-2017.
 */

public class NotesRequest extends StringRequest {
    private static final String URL = "http://project700007.netai.net/college/notes.php";
    private Map<String, String> params;

    public NotesRequest(String className, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);
        params = new HashMap<>();
        params.put("className", className);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}

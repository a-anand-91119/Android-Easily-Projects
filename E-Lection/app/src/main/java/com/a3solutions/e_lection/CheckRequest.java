package com.a3solutions.e_lection;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by Anand on 18-02-2017.
 */

class CheckRequest extends StringRequest {
    private static final String CHECK_REQUEST_URL = "http://androiddevz.netai.net/e_lection/check.php";

    public CheckRequest(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, CHECK_REQUEST_URL, listener, errorListener);
    }
}

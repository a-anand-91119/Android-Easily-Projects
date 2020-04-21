package com.a3solutions.voicerecogtest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 16-04-2017.
 */

public class HistoryRequest extends StringRequest {
    private final Map<String,String> params;
    private  static final String BOOK_URL="http://androideasily.in/v_ticket/history.php";

    public HistoryRequest(String user_id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, BOOK_URL, listener, errorListener);
        params=new HashMap<>();
        params.put("user_id",user_id);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
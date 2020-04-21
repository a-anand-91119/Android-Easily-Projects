package com.a3solutions.voicerecogtest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Anand on 30-03-2017.
 */

class BookRequest extends StringRequest {
    private final Map<String,String> params;
    private  static final String BOOK_URL="http://androideasily.in/v_ticket/book_ticket.php";

    public BookRequest(String trainName,String fromStation, String toStation, String date, String selectedClass, UUID pnr, String user_id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, BOOK_URL, listener, errorListener);
        params=new HashMap<>();
        params.put("class",selectedClass);
        params.put("user_id",user_id);
        params.put("trainName",trainName);
        params.put("fromStation",fromStation);
        params.put("toStation",toStation);
        params.put("date",date);
        params.put("pnr",pnr.toString());
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
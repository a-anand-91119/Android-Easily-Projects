package com.a3solutions.mecha_loca;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 01-06-2017.
 */

public class RatingRequest extends StringRequest {
    private static final String URL = "http://project6007.netai.net/mecha_loca/rating.php";
    public Map<String, String> params;

    public RatingRequest(String mech_id,String rating, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);
        params = new HashMap<>();
        params.put("rating", rating);
        params.put("mech_id", mech_id);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}

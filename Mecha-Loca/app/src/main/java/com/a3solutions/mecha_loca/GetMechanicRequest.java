package com.a3solutions.mecha_loca;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 13-04-2017.
 */

public class GetMechanicRequest extends StringRequest {
    private static final String FETCH_URL = "http://project6007.netai.net/mecha_loca/mechget.php";
    private Map<String, String> params;

    public GetMechanicRequest(String mech_id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, FETCH_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("mech_id", mech_id);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}

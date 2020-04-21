package com.a3solutions.mecha_loca;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 13-04-2017.
 */

public class GetHelpRequest extends StringRequest {
    private static final String URL = "http://project6007.netai.net/mecha_loca/saveHelp.php";
    private Map<String, String> params;

    public GetHelpRequest(String mech_id, String user_id, String latitude, String longitude, String location, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        params = new HashMap<>();
        params.put("mech_id", mech_id);
        params.put("user_id", user_id);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("location", location);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}

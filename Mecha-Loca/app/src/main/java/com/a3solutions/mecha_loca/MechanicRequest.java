package com.a3solutions.mecha_loca;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Anand on 12-04-2017.
 */

public class MechanicRequest extends StringRequest {
    private static final String MECHANIC_REGISTER_REQUEST_URL = "http://project6007.netai.net/mecha_loca/register_mech.php";
    private Map<String, String> params;

    public MechanicRequest(String name, String username, String password, String email, UUID mechId, Double latitude, Double longitude, String mobile, Response.Listener<String> listener) {
        super(Method.POST, MECHANIC_REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("username", username);
        params.put("password", password);
        params.put("email", email);
        params.put("mobile", mobile);
        params.put("mech_id", String.valueOf(mechId));
        params.put("latitude", String.valueOf(latitude));
        params.put("longitude", String.valueOf(longitude));
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

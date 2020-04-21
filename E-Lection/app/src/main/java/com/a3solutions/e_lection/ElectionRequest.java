package com.a3solutions.e_lection;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Anand on 18-02-2017.
 */

class ElectionRequest extends StringRequest {
    private static final String ELECTION_UPLOAD_URL = "http://androiddevz.netai.net/e_lection/election_upload.php";
    private final Map<String, String> params;

    public ElectionRequest(UUID election_id, String election_name, String start, String end, Response.Listener<String> listener) {
        super(Method.POST, ELECTION_UPLOAD_URL, listener, null);
        params = new HashMap<>();
        params.put("election_id",election_id.toString());
        params.put("election_name", election_name);
        params.put("start", start);
        params.put("end", end);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

package com.a3solutions.e_college;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 24-04-2017.
 */

public class PDFUploadRequest extends StringRequest {
    private static final String UPLOAD_REQUEST_URL = "http://project700007.netai.net/college/file_details_upload.php";
    private Map<String, String> params;

    public PDFUploadRequest(String file_name, String classSelected, Response.Listener<String> listener) {
        super(Method.POST, UPLOAD_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("file_name", file_name);
        params.put("classSelected", classSelected);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

package com.a3solutions.e_lection;

import android.graphics.Bitmap;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Anand on 19-02-2017.
 */

class CandidateUploadRequest extends StringRequest {
    private static final String UPLOAD_URL = "http://androiddevz.netai.net/e_lection/candidateUpload.php";
    private final Map<String, String> params;

    public CandidateUploadRequest(UUID candidate_id, Bitmap bitmap, String candid_name, String party, String election_id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST,UPLOAD_URL, listener, errorListener);
        params = new Hashtable<>();
        String imageString = getStringImage(bitmap);
        params.put("candidate_id",candidate_id.toString());
        params.put("image", imageString);
        params.put("candid_name",candid_name);
        params.put("party",party);
        params.put("election_id",election_id);
    }

    private String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}

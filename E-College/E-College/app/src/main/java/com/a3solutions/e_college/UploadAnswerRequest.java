package com.a3solutions.e_college;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anand on 28-04-2017.
 */

public class UploadAnswerRequest extends StringRequest {
    private static final String URL = "http://project700007.netai.net/college/uploadanswer.php";
    private Map<String, String> params;

    public UploadAnswerRequest(String question_id, String answer, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        params = new HashMap<>();
        params.put("question_id", question_id);
        answer=answer.replace("\"","");
        answer=answer.replace("\'","");
        params.put("answer", answer);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}

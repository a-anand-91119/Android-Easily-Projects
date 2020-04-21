package com.a3solutions.e_college;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Anand on 28-04-2017.
 */

public class QuestionRequest extends StringRequest {
    private static final String UPLOAD_REQUEST_URL = "http://project700007.netai.net/college/question.php";
    private Map<String, String> params;

    public QuestionRequest(String question, String className, Response.Listener<String> listener) {
        super(Method.POST, UPLOAD_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("class",className);
        question=question.replace("\"","");
        question=question.replace("\'","");
        params.put("question",question);
        params.put("question_id", UUID.randomUUID().toString());
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

package com.a3solutions.voicerecogtest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 1234;
    private RequestQueue requestQueue;
    TextView name;
    static String user_id;
    private Boolean pressedBackOnce = false;

    ImageView imageView2, imageView3, imageView4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        String namePrevious = preferences.getString("name", "");
        user_id = preferences.getString("user_id", "");
        name = (TextView) findViewById(R.id.tv_name);

        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor logout = getSharedPreferences("LOGIN", Context.MODE_PRIVATE).edit();
                logout.clear().apply();
                Toast.makeText(MainActivity.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Sample: Book 3 Tickets From Trivandrum To Kozhikkode On 29th Of April", Toast.LENGTH_LONG).show();
            }
        });
        name.setText(namePrevious);
        ImageButton speakButton = (ImageButton) findViewById(R.id.btnSpeak);
        requestQueue = RequestQueueCreate.getInstance(getApplicationContext()).getRequestQueue();
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            speakButton.setEnabled(false);
        }
    }

    public void speakButtonClicked(View v) {
        startVoiceRecognitionActivity();
    }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Recognizing");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Parsing Speech");
        progressDialog.show();
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            float[] confidence = data.getFloatArrayExtra(
                    RecognizerIntent.EXTRA_CONFIDENCE_SCORES);

            JSONObject devices = new JSONObject();
            try {
                devices.put("length", confidence.length);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray speech = new JSONArray();
            for (int i = 0; i < confidence.length; i++) {
                JSONObject speech2 = new JSONObject();
                try {
                    speech2.put("data", matches.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                speech.put(speech2);
            }
            try {
                devices.put("speech", speech);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String json = devices.toString();
            Log.i("JSON", json);
            SpeechRequest speechRequest = new SpeechRequest(json, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    progressDialog.hide();
                    Log.i("RESPONSE", response);
                    Intent intent = new Intent(MainActivity.this, NewResult.class);
                    intent.putExtra("response", response);
                    startActivity(intent);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    progressDialog.hide();
                    if (error instanceof NetworkError) {
                        Toast.makeText(MainActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(MainActivity.this, "No Connection Available", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(MainActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    }
                    error.printStackTrace();
                }
            });
            speechRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(speechRequest);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (pressedBackOnce) {
            super.onBackPressed();
        }
        this.pressedBackOnce = true;
        Toast.makeText(this, "Press Again To Exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pressedBackOnce = false;
            }
        }, 2000);
    }
}
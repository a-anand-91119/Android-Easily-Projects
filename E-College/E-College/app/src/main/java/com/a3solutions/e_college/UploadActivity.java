package com.a3solutions.e_college;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UploadActivity extends AppCompatActivity {

    Button bt_upload, bt_browse;
    Uri path;
    String uploadFileName;
    EditText filename;
    boolean file_selected = false;
    ArrayAdapter<String> classAdapter;
    Spinner sp_class;
    String classSelected="";
    ArrayList<String> classList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        bt_browse = (Button) findViewById(R.id.browse);
        filename = (EditText) findViewById(R.id.et_file_name);
        sp_class = (Spinner) findViewById(R.id.spinner);
        bt_upload = (Button) findViewById(R.id.upload);
        bt_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select PDF"), 1);
            }
        });
        bt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFileName = filename.getText().toString();
                if (uploadFileName.equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter The Name To Be Given To The File", Toast.LENGTH_SHORT).show();
                } else if (classSelected.equals("")) {
                    Toast.makeText(UploadActivity.this, "Select A Class", Toast.LENGTH_SHORT).show();
                } else if (!file_selected) {
                    Toast.makeText(getApplicationContext(), "Select The File To Be Uploaded", Toast.LENGTH_SHORT).show();
                } else {
                    ProgressDialog pd = new ProgressDialog(UploadActivity.this);
                    pd.setMessage("Saving File Details...");
                    pd.show();
                    Upload_File();
                    pd.dismiss();
                    (new Upload(UploadActivity.this, path)).execute();
                }
            }
        });
        sp_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classSelected = sp_class.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final ProgressDialog progressDialog = new ProgressDialog(UploadActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        StringRequest classRequest = new StringRequest(Request.Method.POST, "http://project700007.netai.net/college/getclass.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("RESPONSE", response);
                progressDialog.dismiss();
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("class");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            classList.add(i, jsonObject1.getString("class"));
                        }
                    }
                    else{
                        Toast.makeText(UploadActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
             //   classAdapter.add(classList);
                classAdapter = new ArrayAdapter<String>(UploadActivity.this,R.layout.support_simple_spinner_dropdown_item,classList);
                sp_class.setAdapter(classAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                progressDialog.hide();
                if (error instanceof TimeoutError) {
                    Toast.makeText(UploadActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(UploadActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(UploadActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
        requestQueue.add(classRequest);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                path = result.getData();
                file_selected = true;
                Toast.makeText(getApplicationContext(), "File Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class Upload extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd1;
        private Context c;
        private Uri path;

        public Upload(Context c, Uri path) {
            this.c = c;
            this.path = path;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd1 = ProgressDialog.show(c, "Uploading File", "Please Wait");
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "File Upload Successful", Toast.LENGTH_SHORT).show();
            pd1.dismiss();
            finish();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url_path = "http://project700007.netai.net/college/upload.php";
            HttpURLConnection conn = null;

            int maxBufferSize = 1024;
            try {
                URL url = new URL(url_path);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setChunkedStreamingMode(1024);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data");

                OutputStream outputStream = conn.getOutputStream();
                InputStream inputStream = c.getContentResolver().openInputStream(path);

                int bytesAvailable = inputStream.available();
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                int bytesRead;
                while ((bytesRead = inputStream.read(buffer, 0, bufferSize)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                inputStream.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.e("RESPONSE", line);
                }
                reader.close();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return null;
        }
    }

    public void Upload_File() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RESPONSE", response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Toast.makeText(getApplicationContext(), "File Details Saved Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UploadActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        PDFUploadRequest pdfUploadRequest = new PDFUploadRequest(uploadFileName,classSelected, responseListener);
        RequestQueue queue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
        queue.add(pdfUploadRequest);
    }
}

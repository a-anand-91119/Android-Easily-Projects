package com.a3solutions.e_college;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewNotesActivity extends AppCompatActivity {

    private long downloadReference;
    String filename;
    ListView listView;
    ArrayAdapter<String> itemsAdapter;
    ArrayList<String> notes;

    @Override
    protected void onDestroy() {
        unregisterReceiver(downloadReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);
        listView = (ListView) findViewById(R.id.list_notes);

        final ProgressDialog progressDialog = new ProgressDialog(ViewNotesActivity.this);
        progressDialog.setMessage("Fetching Notes List");
        progressDialog.setCancelable(false);
        progressDialog.show();
        NotesRequest notesRequest = new NotesRequest(getIntent().getStringExtra("class"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response);
                progressDialog.dismiss();
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("notes");
                        notes = new ArrayList<String>(jsonArray.length());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            notes.add(i, jsonObject1.getString("notes"));
                        }
                        itemsAdapter = new ArrayAdapter<String>(ViewNotesActivity.this, android.R.layout.simple_list_item_1, notes);
                        listView.setAdapter(itemsAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                progressDialog.hide();
                if (error instanceof TimeoutError) {
                    Toast.makeText(ViewNotesActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(ViewNotesActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(ViewNotesActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        RequestQueue requestQueue = GetNet.getInstance(getApplicationContext()).getRequestQueue();
        requestQueue.add(notesRequest);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int i = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewNotesActivity.this);
                builder.setTitle("Do You Want To Download This File");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        filename = listView.getItemAtPosition(i).toString();
                        DownloadData();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    private void DownloadData() {
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Uri Download_Uri = Uri.parse("https://project700007.netai.net/college/documents/" + filename + ".pdf");
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setTitle("Downloading " + filename + ".pdf");
       // request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOCUMENTS, filename + ".pdf");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, filename + ".pdf");
        downloadReference = downloadManager.enqueue(request);
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadReference == referenceId) {
                Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
            }
        }
    };
}

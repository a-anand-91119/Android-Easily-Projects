package com.a3solutions.e_lection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminPortal extends AppCompatActivity {

    private Boolean pressedBackOnce = false;
    private  Boolean fabCheck = false;
    private  TextView tv_id, tv_name, tv_starting, tv_ending, tv_number;
    private String election_id = "";
    private FloatingActionButton fab_get_result, fab_add_candidate, fab_create_election;
    private FrameLayout frameLayout;
    private FloatingActionsMenu fabMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_portal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialize();

        frameLayout.getBackground().setAlpha(0);
        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frameLayout.getBackground().setAlpha(240);
                frameLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        fabMenu.collapse();
                        return true;
                    }
                });
            }

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });
        fab_get_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.collapse();
            }
        });
        fab_add_candidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.collapse();
                Intent addCandidate = new Intent(AdminPortal.this, AddCandidate.class);
                addCandidate.putExtra("election_id", election_id);
                startActivity(addCandidate);
            }
        });
        fab_create_election.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.collapse();
                fabCheck = true;
                checkElection();
            }
        });
        fab_get_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.collapse();
                fabCheck = true;
                Intent showResult = new Intent(AdminPortal.this,ViewResults.class);
                startActivity(showResult);
            }
        });
    }

    private void initialize() {
        fab_add_candidate = (FloatingActionButton) findViewById(R.id.fab_add_candidate);
        fab_create_election = (FloatingActionButton) findViewById(R.id.fab_create_election);
        fab_get_result = (FloatingActionButton) findViewById(R.id.fab_get_result);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        tv_id = (TextView) findViewById(R.id.tv_id);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_starting = (TextView) findViewById(R.id.tv_starting);
        tv_ending = (TextView) findViewById(R.id.tv_ending);
        tv_number = (TextView) findViewById(R.id.tv_number);
    }

    @Override
    public void onBackPressed() {
        if (fabMenu.isExpanded()) {
            fabMenu.collapse();
        } else {
            if (pressedBackOnce) {
                super.onBackPressed();
                return;
            }
            this.pressedBackOnce = true;
            Toast.makeText(this, "Press Back Again To Exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pressedBackOnce = false;
                }
            }, 2000);
        }
    }

    private void checkElection() {
        final ProgressDialog progressDialog = new ProgressDialog(AdminPortal.this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Checking For Existing Election");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Response.Listener<String> checkListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    progressDialog.hide();
                    progressDialog.dismiss();
                    Boolean result = jsonObject.getBoolean("success");
                    if (result) {
                        if (fabCheck) {
                            Toast.makeText(AdminPortal.this, "An Election Is Currently Running", Toast.LENGTH_SHORT).show();
                            fabCheck = false;
                        }
                        election_id = jsonObject.getString("election_id");
                        String election_name = jsonObject.getString("election_name");
                        String start = jsonObject.getString("start");
                        String expiry = jsonObject.getString("expiry");
                        String no_of_candidates = jsonObject.getString("no_of_candidates");
                        Log.i("CANDIDATES", no_of_candidates);
                        tv_number.setText(no_of_candidates);
                        tv_ending.setText(expiry);
                        tv_starting.setText(start);
                        tv_name.setText(election_name);
                        tv_id.setText(election_id);
                        tv_number.setText(no_of_candidates);
                        fab_add_candidate.setEnabled(true);
                    } else {
                        if (fabCheck) {
                            Intent createElection = new Intent(AdminPortal.this, CreateElectionActivity.class);
                            startActivity(createElection);
                            fabCheck = false;
                        } else {
                            fab_add_candidate.setEnabled(false);
                            Toast.makeText(AdminPortal.this, "No Election Currently Running", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                progressDialog.dismiss();
                if (error instanceof TimeoutError) {
                    Toast.makeText(AdminPortal.this, "Connection Timed Out. Try Again!!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(AdminPortal.this, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(AdminPortal.this, "No Response From Server", Toast.LENGTH_SHORT).show();
                }
            }
        };
        CheckRequest checkRequest = new CheckRequest(checkListener, errorListener);
        RequestQueue requestQueue = Volley.newRequestQueue(AdminPortal.this);
        requestQueue.add(checkRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkElection();
    }
}

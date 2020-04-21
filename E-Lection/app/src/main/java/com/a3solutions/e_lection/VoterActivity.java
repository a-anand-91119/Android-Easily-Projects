package com.a3solutions.e_lection;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VoterActivity extends AppCompatActivity {

    private CandidateConfig candidateConfig;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ProgressDialog progressDialog;
    static String candidNameConfirm,partyConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter);

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        progressDialog = new ProgressDialog(VoterActivity.this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading Candidate Details");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest fetchRequest = new StringRequest(Request.Method.POST, CandidateConfig.FETCH_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("SERVER RESPONSE", response);
                progressDialog.hide();
                progressDialog.dismiss();
                parseJSON(response);
                showData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                progressDialog.dismiss();
                if (error instanceof NetworkError) {
                    Toast.makeText(VoterActivity.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(VoterActivity.this, "Bad Server Response", Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(VoterActivity.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                }
            }
        });
        CandidateSingleton.getInstance(getApplicationContext()).addToRequestQueue(fetchRequest);
    }

    private void parseJSON(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray(CandidateConfig.TAG_JSON_ARRAY);
            candidateConfig = new CandidateConfig(array.length());

            for (int i = 0; i < array.length(); i++) {
                JSONObject j = array.getJSONObject(i);
                CandidateConfig.id[i] = getId(j);
                CandidateConfig.candid_id[i] = getCandidId(j);
                CandidateConfig.candid_name[i] = getCandidName(j);
                CandidateConfig.party[i] = getParty(j);
                CandidateConfig.election_id[i] = getElectionId(j);
                CandidateConfig.symbol[i] = getSymbol(j);
                CandidateConfig.election_name[i]=getElectionName(j);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getId(JSONObject jsonObject) {
        String id = null;
        try {
            id = jsonObject.getString(CandidateConfig.TAG_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }

    private String getCandidId(JSONObject jsonObject) {
        String candidId = null;
        try {
            candidId = jsonObject.getString(CandidateConfig.TAG_CANDID_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return candidId;
    }

    private String getCandidName(JSONObject jsonObject) {
        String candidName = null;
        try {
            candidName = jsonObject.getString(CandidateConfig.TAG_CANDID_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return candidName;
    }

    private String getParty(JSONObject jsonObject) {
        String party = null;
        try {
            party = jsonObject.getString(CandidateConfig.TAG_PARTY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return party;
    }

    private String getElectionId(JSONObject jsonObject) {
        String electionId = null;
        try {
            electionId = jsonObject.getString(CandidateConfig.TAG_ELECTION_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return electionId;
    }

    private String getSymbol(JSONObject jsonObject) {
        String symbol = null;
        try {
            symbol = jsonObject.getString(CandidateConfig.TAG_SYMBOL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return symbol;
    }

    private String getElectionName(JSONObject jsonObject) {
        String symbol = null;
        try {
            symbol = jsonObject.getString(CandidateConfig.TAG_ELECTION_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return symbol;
    }

    public void showData(){
        adapter = new CardAdapter(CandidateConfig.election_name,CandidateConfig.id, CandidateConfig.candid_id, CandidateConfig.candid_name, CandidateConfig.election_id, CandidateConfig.party, CandidateConfig.symbol,getApplicationContext());
        recyclerView.setAdapter(adapter);
        progressDialog.hide();
        progressDialog.dismiss();
    }
    public static void confirmVote(String candidName, String party){
        candidNameConfirm=candidName;
        partyConfirm=party;

    }
    public final void vote(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(VoterActivity.this);
        alertDialog.setTitle("Confirm Your Vote");
        alertDialog.setPositiveButton("Vote", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO upload vote
                Toast.makeText(, "Will Be Uploaded", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        LayoutInflater layoutInflator = (VoterActivity.this).getLayoutInflater();
        View confirmView = layoutInflator.inflate(R.layout.vote_confirm,null);
        TextView candidateName=(TextView)confirmView.findViewById(R.id.tv_vote_candidname_confirm);
        candidateName.setText("candidate name");
        TextView partyName = (TextView)confirmView.findViewById(R.id.tv_vote_party_confirm);
        partyName.setText("party name");
        alertDialog.setView(confirmView);
        alertDialog.create().show();
    }
}

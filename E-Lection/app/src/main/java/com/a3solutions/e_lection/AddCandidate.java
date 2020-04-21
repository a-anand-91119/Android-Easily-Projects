package com.a3solutions.e_lection;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;


public class AddCandidate extends AppCompatActivity {
    private ImageView symbol;
    private Bitmap bitmap;
    private Button candidate_submit, image_choose;
    private TextInputLayout til_candidate_name, til_party_name;
    private TextInputEditText et_candidate_name, et_party_name;
    private String candidate_name, party_name, election_id = "";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference rootNode = firebaseDatabase.getReference();
    private DatabaseReference candidAdd;
    private UUID candidate_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_candidate);

        initialize_candidate();
        candidate_id=UUID.randomUUID();
        election_id = getIntent().getExtras().getString("election_id");
        image_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent symbolPicker = new Intent(Intent.ACTION_PICK);
                symbolPicker.setType("image/*");
                startActivityForResult(symbolPicker, 0);
            }
        });
        candidate_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                candidate_name = et_candidate_name.getText().toString();
                party_name = et_party_name.getText().toString();

                if (validateCandidName() && validatePartyName()) {
                    final AlertDialog.Builder confirmInput = new AlertDialog.Builder(AddCandidate.this);
                    confirmInput.setTitle("Confirm Details");
                    confirmInput.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final ProgressDialog progressDialog2 = new ProgressDialog(AddCandidate.this);
                            progressDialog2.setTitle("Please Wait");
                            progressDialog2.setMessage("Uploading Candidate Details");
                            progressDialog2.show();
                            Response.Listener<String> candidRequest = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        candidAdd=rootNode.child(jsonObject.getString("candidate_row_id"));
                                        candidAdd.setValue(0);
                                        progressDialog2.hide();
                                        progressDialog2.dismiss();
                                        Toast.makeText(AddCandidate.this, "Successfully Added Candidate", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            Response.ErrorListener errorListener = new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog2.hide();
                                    progressDialog2.dismiss();
                                    if (error instanceof NoConnectionError) {
                                        Toast.makeText(AddCandidate.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                    } else if (error instanceof ServerError) {
                                        Toast.makeText(AddCandidate.this, "Bad Server Response", Toast.LENGTH_SHORT).show();
                                    } else if (error instanceof NetworkError) {
                                        Toast.makeText(AddCandidate.this, "bad Internet Connection", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            };
                            CandidateUploadRequest candidaUploadRequest = new CandidateUploadRequest(candidate_id, bitmap, candidate_name, party_name, election_id, candidRequest, errorListener);
                            RequestQueue requestQueue2 = Volley.newRequestQueue(AddCandidate.this);
                            requestQueue2.add(candidaUploadRequest);
                            dialogInterface.dismiss();
                        }
                    });
                    confirmInput.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    LayoutInflater inflater = (AddCandidate.this).getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.candidate_confirm, null);
                    TextView candidateName = (TextView) dialogView.findViewById(R.id.candidate_confirm);
                    TextView partyName = (TextView) dialogView.findViewById(R.id.party_confirm);
                    ImageView symbolConfirm = (ImageView) dialogView.findViewById(R.id.symbol_confirm);
                    candidateName.setText(candidate_name);
                    partyName.setText(party_name);
                    symbolConfirm.setImageDrawable(symbol.getDrawable());
                    confirmInput.setView(dialogView);
                    confirmInput.create().show();
                }
            }
        });
    }

    private void initialize_candidate() {
        til_candidate_name = (TextInputLayout) findViewById(R.id.til_candidate_name);
        til_party_name = (TextInputLayout) findViewById(R.id.til_party_name);
        et_candidate_name = (TextInputEditText) findViewById(R.id.et_candidate_name);
        et_party_name = (TextInputEditText) findViewById(R.id.et_party_name);
        candidate_submit = (Button) findViewById(R.id.bt_submit);
        image_choose = (Button) findViewById(R.id.bt_choose);
        symbol = (ImageView) findViewById(R.id.symbol);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK && imageReturnedIntent != null && imageReturnedIntent.getData() != null) {
            Uri filePath = imageReturnedIntent.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                symbol.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private Boolean validateCandidName() {
        if (et_candidate_name.getText().toString().trim().isEmpty()) {
            til_candidate_name.setError("Enter The Candidate Name");
            requestFocus(et_candidate_name);
            return false;
        } else {
            til_candidate_name.setErrorEnabled(false);
        }
        return true;
    }

    private Boolean validatePartyName() {
        if (et_party_name.getText().toString().trim().isEmpty()) {
            til_party_name.setError("Enter The Party Name");
            requestFocus(et_party_name);
            return false;
        } else {
            til_party_name.setErrorEnabled(false);
        }
        return true;
    }
}

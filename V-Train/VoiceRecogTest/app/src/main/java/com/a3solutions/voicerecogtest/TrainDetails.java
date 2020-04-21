package com.a3solutions.voicerecogtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrainDetails extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_details);

        String title = getIntent().getStringExtra("title");
        setTitle(title);

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        String response = getIntent().getStringExtra("response");
        parseJSON(response);
        showData();
    }

    private void showData() {
        RecyclerView.Adapter adapter = new CardAdapter(getIntent().getStringExtra("from"),getIntent().getStringExtra("to"),getIntent().getStringExtra("date"),TrainConfig.trainId, TrainConfig.trainName, TrainConfig.arriveTime, TrainConfig.departTime, TrainConfig.classes, TrainDetails.this);
        recyclerView.setAdapter(adapter);
    }


    private void parseJSON(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray(TrainConfig.TAG_JSONARRAY);
            TrainConfig trainConfig = new TrainConfig(array.length());

            for (int i = 0; i < array.length(); i++) {
                JSONObject j = array.getJSONObject(i);
                TrainConfig.trainId[i] = getTrainId(j);
                TrainConfig.trainName[i] = getTrainName(j);
                TrainConfig.arriveTime[i] = getArriveTime(j);
                TrainConfig.departTime[i] = getDepartTime(j);
                TrainConfig.classes[i]=getClasses(j);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getTrainId(JSONObject jsonObject) {
        String trainId = null;
        try {
            trainId = jsonObject.getString(TrainConfig.TAG_TRAINID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trainId;
    }
    private JSONArray getClasses(JSONObject jsonObject) {
        JSONArray classes = null;
        try {
            classes = jsonObject.getJSONArray(TrainConfig.TAG_CLASSES);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private String getTrainName(JSONObject jsonObject) {
        String trainName = null;
        try {
            trainName = jsonObject.getString(TrainConfig.TAG_TRAINNAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trainName;
    }

    private String getArriveTime(JSONObject jsonObject) {
        String arriveTime = null;
        try {
            arriveTime = jsonObject.getString(TrainConfig.TAG_ARRIVETIME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arriveTime;
    }

    private String getDepartTime(JSONObject jsonObject) {
        String departTIme = null;
        try {
            departTIme = jsonObject.getString(TrainConfig.TAG_DEPARTTIME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return departTIme;
    }
}

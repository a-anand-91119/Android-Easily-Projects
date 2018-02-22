package com.androideasily.olachallenge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Anand on 16-12-2017.
 */

//Fragment class for SOngs List
public class FragmentList extends Fragment {
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;//Custom adapter for the cardview


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentListView = inflater.inflate(R.layout.fragment_list, container, false);
//initializing the recycler view, and cachinng to improve the performance, during scrolling
        recyclerView = (RecyclerView) fragmentListView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        TextInputLayout searchLayout = (TextInputLayout) fragmentListView.findViewById(R.id.searchField);

        //textWtcher to implement the ssong search feature
        searchLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //filter class. more details in the custom adapter
                songAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        return fragmentListView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Queue controller is a singleton class to ensure one instance of requestquere and favourite preference class
        QueueController queueController = QueueController.getInstance(getActivity().getApplicationContext());
        requestQueue = queueController.getRequestQueue();
        //fethcing the data frm the api
        fetchSongData();
    }

    //custom adapter initalization
    private void showData() {
        songAdapter = new SongAdapter(getActivity(),
                SongConfig.songNames,
                SongConfig.songAlbums,
                SongConfig.songArtists,
                SongConfig.songUrls);
        recyclerView.setAdapter(songAdapter);
    }

    /*method to fetch the JSONArray form the api
    *here im using Volley JSONArray request.
    * and parsing the json Object manually
    *
    * */
    private void fetchSongData() {
        String JSON_URL = "http://starlord.hackerearth.com/studio";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(JSON_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                parseJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", error.getMessage());
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    //parsing the JSON objects, utilizing the SongCOnfig Class
    private void parseJSON(JSONArray response) {
        SongConfig songConfig = new SongConfig(response.length());
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject jsonObject = response.getJSONObject(i);
                songConfig.songNames[i] = getSongName(jsonObject);
                songConfig.songAlbums[i] = getSongAlbum(jsonObject);
                songConfig.songUrls[i] = getSongUrl(jsonObject);
                songConfig.songArtists[i] = getSongArtist(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        showData();
    }

    /*All method below here is simply to extract a single nentry from the json object.
    *the SOngCOnfig class contans the object attributes to hold data as well the key for the JSON
    * */
    private String getSongName(JSONObject jsonObject) {
        String songName = null;
        try {
            songName = jsonObject.getString(SongConfig.SONG_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return songName;
    }

    private String getSongUrl(JSONObject jsonObject) {
        String songUrl = null;
        try {
            songUrl = jsonObject.getString(SongConfig.SONG_URL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return songUrl;
    }

    private String getSongAlbum(JSONObject jsonObject) {
        String songAlbum = null;
        try {
            songAlbum = jsonObject.getString(SongConfig.SONG_ALBUM);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return songAlbum;
    }

    private String getSongArtist(JSONObject jsonObject) {
        String songArtist = null;
        try {
            songArtist = jsonObject.getString(SongConfig.SONG_ARTISTS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return songArtist;
    }
}
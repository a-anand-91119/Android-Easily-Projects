package com.androideasily.olachallenge;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Anand on 16-12-2017.
 */

//The Favourites List
public class FragmentPlaylist extends Fragment {
    private ListView favoriteList;
    private FavouritePreference favouritePreference;
    private SongDataStream songDataStream;//Interface for fragment to activity communication - Play Selected song

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View favView = inflater.inflate(R.layout.fragment_playlist, container, false);
        favoriteList = (ListView) favView.findViewById(R.id.favoriteList);//
        return favView;
    }

    private void fetchFavourites() {
        //Shared Preferecnce class to access stored favourites information
        favouritePreference = QueueController.getInstance(getActivity().getApplicationContext()).getFavouritePreference();
        songDataStream = (SongDataStream) getActivity();
        prepareData();
    }

    //detecting whether the fragment is visible to the user ot not before loading
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            fetchFavourites();
        }
    }

    //Loading the data frm API
    private void prepareData() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching Favourites Information");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String JSON_URL = "http://starlord.hackerearth.com/studio";
        QueueController.getInstance(getActivity().getApplicationContext()).getRequestQueue().add(new JsonArrayRequest(JSON_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
                parseJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", error.getMessage());
            }
        }));
    }

    private void parseJSON(JSONArray response) {
        ArrayList<Favourite> preparedData = new ArrayList<>();
        for (int count = 0; count < response.length(); count++)
            try {
                JSONObject jsonObject = response.getJSONObject(count);
                if (favouritePreference.checkPreference(jsonObject.getString("song"))) {
                    preparedData.add(new Favourite(
                            jsonObject.getString("song"),
                            jsonObject.getString("artists"),
                            jsonObject.getString("url"),
                            jsonObject.getString("cover_image")));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        if (preparedData.isEmpty()) {
            //No user favourites
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("No Favourites Found");
            alertDialog.setMessage("You Have Not Added Any Songs To Favourites Yet");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        } else {
            Snackbar.make(getView(), "Choose The Song To Stream", Snackbar.LENGTH_LONG).show();
            final FavoriteAdapter favoriteAdapter = new FavoriteAdapter(getActivity(), preparedData);
            favoriteList.setAdapter(favoriteAdapter);
            favoriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    /*
                    * passign information regarding the seleclted song to be streamed to the main activity, and from there to the song player*/
                    JSONObject songToStreamInfo = new JSONObject();
                    try {
                        songToStreamInfo.put("name", favoriteAdapter.getSongName(position));
                        songToStreamInfo.put("url", favoriteAdapter.getSongUrl(position));
                        songToStreamInfo.put("artist", favoriteAdapter.getAlbumName(position));
                        songToStreamInfo.put("album", favoriteAdapter.getAlbumUrl(position));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    songDataStream.getSongData(songToStreamInfo);
                }
            });
        }
    }
}
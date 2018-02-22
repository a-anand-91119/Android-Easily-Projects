package com.androideasily.olachallenge;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Anand on 16-12-2017.
 */

//Custom list adapter to display the favourited songs
class FavoriteAdapter extends ArrayAdapter<Favourite> {
    private final ArrayList<Favourite> favourites;
    private final Context context;

//getter methods
    String getSongName(int position) {
        return favourites.get(position).getSongName();
    }

    String getSongUrl(int position) {
        return favourites.get(position).getSongUrl();
    }

    String getAlbumUrl(int position) {
        return favourites.get(position).getAlbumUrl();
    }

    String getAlbumName(int position) {
        return favourites.get(position).getAlbumName();
    }

    //contructor to initlaize the arraylsit
    FavoriteAdapter(@NonNull Context context, ArrayList<Favourite> favourites) {
        super(context, R.layout.favorite_list, favourites);
        this.context = context;
        this.favourites = favourites;
    }

    //drawing the custom view
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View favouritesListView = inflater.inflate(R.layout.favorite_list, parent, false);
        /*
        * tv_songName- Display the name of the song
        * tv_artistName - Dispays the name of the artists
        * */
        TextView tv_songName = (TextView) favouritesListView.findViewById(R.id.l_songName);
        TextView tv_artistName = (TextView) favouritesListView.findViewById(R.id.l_artistName);
        tv_songName.setText(favourites.get(position).getSongName());
        tv_artistName.setText(favourites.get(position).getAlbumName());
        return favouritesListView;
    }
}

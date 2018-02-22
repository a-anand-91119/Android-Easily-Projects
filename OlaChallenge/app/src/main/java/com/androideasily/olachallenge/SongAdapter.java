package com.androideasily.olachallenge;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Anand on 16-12-2017.
 */


//custom cardview adapter for the songs list
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> implements Filterable {

    private final Context context;
    private final List<Song> songs;
    private final List<Song> filteredSongs;
    private SongNameFilter songNameFilter;
    private final FavouritePreference favouritePreference;
    private final SongDataStream songDataStream;

    //constructor
    public SongAdapter(@NonNull Context context, String[] songNames, String[] songAlbums, String[] songArtists, String[] songUrls) {
        super();
        this.context = context;
        this.songs = new ArrayList<>();
        for (int i = 0; i < songNames.length; i++) {
            Song song = new Song();
            song.setSongName(songNames[i]);
            song.setAlbumUrl(songAlbums[i]);
            song.setSongArtists(songArtists[i]);
            song.setSongUrl(songUrls[i]);
            songs.add(song);
        }
        this.filteredSongs = songs;
        favouritePreference = QueueController.getInstance(context).getFavouritePreference();
        songDataStream = (SongDataStream) context;
    }

    //inflating the custom layout
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View songView = layoutInflater.inflate(R.layout.layout_song_list, parent, false);
        return new ViewHolder(songView);
    }

    //view binded, handling of ui elements
    @Override
    public void onBindViewHolder(final SongAdapter.ViewHolder holder, int position) {
        final int newPosition = position;
        /*
        * VARIALBES
        * songName -stores the name of the song
        * songArtists - stores the artist details
        * songAlbum - circularimage view (third party library used) to display the album image
        *           picasso library used to download and dislplay the image
        * favourite - imageview to display whetehr song is favourite or not
        *
        * */
        holder.songName.setText(songs.get(newPosition).getSongName());
        holder.songArtists.setText(songs.get(newPosition).getSongArtists());
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttpDownloader(context));
        builder.build()
                .load(songs.get(newPosition).getAlbumUrl())
                .fit()
                .into(holder.songAlbum);
        //checking whether favourote or not
        if (favouritePreference.checkPreference(songs.get(newPosition).getSongName()))
            holder.favourite.setBackgroundResource(R.drawable.fav_checked);
        else
            holder.favourite.setBackgroundResource(R.drawable.fav_unchecked);
        //on click listener to make or remove from favourites list
        holder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favouritePreference.checkPreference(songs.get(newPosition).getSongName())) {
                    holder.favourite.setBackgroundResource(R.drawable.fav_unchecked);
                    favouritePreference.updatePreference(songs.get(newPosition).getSongName(), false);
                    Snackbar.make(v, songs.get(newPosition).getSongName() + " Removed From Favourites", Snackbar.LENGTH_SHORT).show();
                } else {
                    holder.favourite.setBackgroundResource(R.drawable.fav_checked);
                    favouritePreference.updatePreference(songs.get(newPosition).getSongName(), true);
                    Snackbar.make(v, songs.get(newPosition).getSongName() + " Added To Favourites", Snackbar.LENGTH_SHORT).show();
                }

            }
        });
        //carvview click listener to stream the osnd
        holder.songCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject songToStreamInfo = new JSONObject();
                try {
                    songToStreamInfo.put("name", songs.get(newPosition).getSongName());
                    songToStreamInfo.put("url", songs.get(newPosition).getSongUrl());
                    songToStreamInfo.put("artist", songs.get(newPosition).getSongArtists());
                    songToStreamInfo.put("album", songs.get(newPosition).getAlbumUrl());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //interface to communicate with activity and other framents
                songDataStream.getSongData(songToStreamInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
    @Override
    public Filter getFilter() {
        if (songNameFilter == null)
            songNameFilter = new SongNameFilter(this, songs);
        return songNameFilter;
    }

    //viewholder class to hold the data inside a card
    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView songArtists, songName;
        final CircleImageView songAlbum;
        final CardView songCardView;
        final ImageView favourite;

        ViewHolder(View songView) {
            super(songView);
            songCardView = (CardView) itemView.findViewById(R.id.songCard);
            songName = (TextView) songView.findViewById(R.id.song_name);
            songArtists = (TextView) songView.findViewById(R.id.song_artists);
            songAlbum = (CircleImageView) songView.findViewById(R.id.album_image);
            favourite = (ImageView) songView.findViewById(R.id.favourite);
        }
    }//filter class to implement the search for songs
    private class SongNameFilter extends Filter {
        private final SongAdapter songAdapter;
        private final List<Song> originalSongList;//original fully populated list
        private final List<Song> filteredSongList;//empty list

        private SongNameFilter(SongAdapter songAdapter, List<Song> originalSongList) {
            super();
            this.songAdapter = songAdapter;
            this.originalSongList = new LinkedList<>(originalSongList);
            this.filteredSongList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            /*
            * method checks whether the entered string from the editext is ofund in any of the card views(sond name)
            * if found the that entry is added to the filtered list and the adpater is notified of the data change
            * both the edittext data and song name converted to lowercase for easier comaprison*/
            filteredSongList.clear();
            final FilterResults results = new FilterResults();
            Log.i("char", (String) constraint);
            if (constraint.length() == 0) {
                filteredSongList.addAll(originalSongList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                for (final Song user : originalSongList) {
                    if (user.getSongName().toLowerCase().contains(filterPattern)) {
                        filteredSongList.add(user);
                    }
                }
            }
            results.values = filteredSongList;
            results.count = filteredSongList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //notifying the changed dataset
            songAdapter.filteredSongs.clear();
            songAdapter.filteredSongs.addAll((ArrayList<Song>) results.values);
            songAdapter.notifyDataSetChanged();
        }
    }

}

package com.androideasily.olachallenge;

/**
 * Created by Anand on 16-12-2017.
 */

//model class for the cardview
class SongConfig {
    public static String[] songNames;
    public static String[] songArtists;
    public static String[] songAlbums;
    public static String[] songUrls;

    //keys for the JSON
    public static final String SONG_NAME = "song";
    public static final String SONG_ARTISTS = "artists";
    public static final String SONG_ALBUM = "cover_image";
    public static final String SONG_URL = "url";


    public SongConfig(int i) {
        songNames = new String[i];
        songArtists = new String[i];
        songAlbums = new String[i];
        songUrls = new String[i];
    }
}

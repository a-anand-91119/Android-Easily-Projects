package com.androideasily.olachallenge;

/**
 * Created by Anand on 16-12-2017.
 */

//model class to store the songs information form the API onyy for the  songs which are users favourite
class Favourite {
    private final String songName;
    private final String albumName;
    private final String songUrl;
    private final String albumUrl;

    //constructors and getter methods.
    public Favourite(String songName, String albumName, String songUrl, String albumUrl) {
        this.songName = songName;
        this.albumName = albumName;
        this.songUrl = songUrl;
        this.albumUrl = albumUrl;
    }

    public String getSongName() {
        return songName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public String getAlbumUrl() {
        return albumUrl;
    }

}

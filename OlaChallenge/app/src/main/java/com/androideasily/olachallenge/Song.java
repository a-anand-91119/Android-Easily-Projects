package com.androideasily.olachallenge;

/**
 * Created by Anand on 16-12-2017.
 */

/*model calss to store the song data from the api
  *no constructor used since indiviual methods are used to set the data in the FragmentList
  * */
class Song {
    private String songName;
    private String songUrl;
    private String albumUrl;
    private String songArtists;

    //getters and setters
    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getAlbumUrl() {
        return albumUrl;
    }

    public void setAlbumUrl(String albumUrl) {
        this.albumUrl = albumUrl;
    }

    public String getSongArtists() {
        return songArtists;
    }

    public void setSongArtists(String songArtists) {
        this.songArtists = songArtists;
    }

}

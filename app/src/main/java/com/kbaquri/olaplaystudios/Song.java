package com.kbaquri.olaplaystudios;

/**
 * Created by Sameer on 15-Dec-17.
 */

public class Song {

    private String song;
    private String url;
    private String artists;
    private String coverImage;

    public Song() {
    }

    public Song(String song, String url, String artists, String coverImage) {
        this.song = song;
        this.url = url;
        this.artists = artists;
        this.coverImage = coverImage;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSong() {
        return song;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getArtists() {
        return artists;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getCoverImage() {
        return coverImage;
    }

}

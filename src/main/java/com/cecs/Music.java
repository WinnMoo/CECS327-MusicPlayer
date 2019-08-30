package com.cecs;

public class Music {
    private Release release;
    private Artist artist;
    private Song song;

    public Music(Release release, Artist artist, Song song) {
        this.release = release;
        this.artist = artist;
        this.song = song;
    }

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}

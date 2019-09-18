package com.cecs;

import java.util.ArrayList;

public class Playlist {
    private String name;
    private ArrayList<Song> songs;

    public Playlist(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
    }

    public boolean addSong(Song songToAdd) {
        return songs.add(songToAdd);
    }

    public boolean removeSong(Song songToRemove) {
        return songs.remove(songToRemove);
    }

    public void clearPlaylist() {
        while (!songs.isEmpty()) {
            songs.remove(0);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}

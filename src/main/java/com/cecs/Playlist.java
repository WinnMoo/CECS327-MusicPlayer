package com.cecs;

import java.util.ArrayList;

public class Playlist {
    private ArrayList<Song> songs;

    public Playlist(){
        songs = new ArrayList<>();
    }

    public boolean addSong(Song songToAdd){
        return songs.add(songToAdd);
    }

    public boolean removeSong(Song songToRemove){
        return songs.remove(songToRemove);
    }

    public void clearPlaylist(){
        while(!songs.isEmpty()){
            songs.remove(0);
        }
    }
}

package com.cecs;

import java.util.ArrayList;

public class Playlist {
    public ArrayList<Song> songs;

    public Playlist(){
        songs = new ArrayList();
    }

    public boolean AddSong(Song songToAdd){
        return songs.add(songToAdd);
    }

    public boolean RemoveSong(Song songToRemove){
        return songs.remove(songToRemove);
    }

    public void ClearPlaylist(){
        while(!songs.isEmpty()){
            songs.remove(0);
        }
    }
}

package com.cecs;

import java.util.ArrayList;
import java.util.Random;

public class User {
    public String username;
    private String password;
    public ArrayList<Playlist> userPlaylists;

    public User(String pass){
        Random rand = new Random();
        int n = rand.nextInt(999999999); // If no username is provided, generate a random num for it

        this.username = String.valueOf(n);
        this.password = pass;
        this.userPlaylists = new ArrayList<Playlist>();
    }

    public User(String name, String pass){
        this.username = name;
        this.password = pass;
        this.userPlaylists = new ArrayList<Playlist>();
    }

    public void ChangeUsername(String newUsername){
        this.username = newUsername;
    }

    public boolean ChangePassword(String oldPassword, String newPassword){
        if(this.password.equals((oldPassword))){
            this.password = newPassword;
            return true;
        } else {
            return false;
        }
    }

    public boolean AddNewPlaylist(Playlist newPlaylist){
        return this.userPlaylists.add(newPlaylist);
    }

    public boolean DeletePlaylist(Playlist playlistToRemove){
        return this.userPlaylists.remove((playlistToRemove));
    }

    public boolean DeleteUser(String pass){
        if(this.password.equals(pass)){
            this.username = null;
            this.password = null;
            this.userPlaylists = null;
            return true;
        } else {
            return false;
        }
    }

    public boolean IsValidPassword(String pass){
        if(this.password.equals((pass))){
            return true;
        } else {
            return false;
        }
    }
}

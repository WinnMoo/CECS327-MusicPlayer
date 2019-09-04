package com.cecs;

import java.util.ArrayList;
import java.util.Random;

public class User {
    String username;
    private String password;
    public ArrayList<Playlist> userPlaylists;

    @Deprecated
    public User(String pass) {
        Random rand = new Random();
        int n = rand.nextInt(999999999); // If no username is provided, generate a random num for it

        this.username = String.valueOf(n);
        this.password = pass;
        this.userPlaylists = new ArrayList<Playlist>();
    }

    User(String name, String pass) {
        this.username = name;
        this.password = pass;
        this.userPlaylists = new ArrayList<Playlist>();
    }

    public void changeUsername(String newUsername) {
        this.username = newUsername;
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        if (this.password.equals((oldPassword))) {
            this.password = newPassword;
            return true;
        } else {
            return false;
        }
    }

    public boolean addNewPlaylist(Playlist newPlaylist) {
        return this.userPlaylists.add(newPlaylist);
    }

    public boolean deletePlaylist(Playlist playlistToRemove) {
        return this.userPlaylists.remove((playlistToRemove));
    }

    // I suggest naming this logout
    public void deleteUser() {
        this.username = null;
        this.password = null;
        this.userPlaylists = null;
    }

    public boolean IsValidPassword(String pass) {
        if (this.password.equals((pass))) {
            return true;
        } else {
            return false;
        }
    }
}

package com.cecs;

import java.util.ArrayList;

public class User implements Comparable<User> {
    String username;
    String password;
    public ArrayList<Playlist> userPlaylists;

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

    @Override
    public int compareTo(User other) {
        var userCmp = this.username.compareToIgnoreCase(other.username);
        var passCmp = this.password.compareTo(other.password);
        if (userCmp == 0) {
            return passCmp;
        }
        return userCmp;
    }
}

package com.cecs.controller;

import com.cecs.def.ProxyInterface;
import com.cecs.model.Music;
import com.cecs.model.Playlist;
import com.cecs.model.User;
import com.google.gson.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

/**
 * Class that handles services needed by the Gson library
 */
public class JsonService {
    private static ProxyInterface proxy = new Proxy(new Communication(), "MusicServices");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Extracts the byte array from a server response
     *
     * @param request The response from the server, in the form of a byte array
     * @return 8192-sized byte value representing sequence in a song
     */
    public static byte[] unpackBytes(JsonObject request) {
        var val = request.get("ret").getAsString();
        var g = gson.fromJson(val, String.class);
        return Base64.getDecoder().decode(g);
    }

    /**
     * Extracts the list of Playlists from a server response
     *
     * @param request The response from the server, in the form of a list of
     *                playlists, where a null signifies that the user credentials do
     *                not match
     * @return value stored in "ret" field, if possible
     */
    @Deprecated
    public static Playlist[] unpackPlaylists(JsonObject request) {
        var get = request.get("ret");
        if (get.isJsonNull()) {
            return null;
        } else {
            var arr = request.get("ret").getAsString();
            return gson.fromJson(arr, Playlist[].class);
        }
    }

    /**
     * Extracts the User data from a server response
     *
     * @param request The response from the server, in the form of a User, where a
     *                null signifies that the user credentials do not match
     * @return value stored in "ret" field, if possible
     */
    public static User unpackUser(JsonObject request) {
        var get = request.get("ret");
        var err = request.get("error");
        if (err != null || get.isJsonNull()) {
            if (err != null) {
                System.err.println(err.getAsString());
            }
            return null;
        } else {
            var arr = request.get("ret").getAsString();
            return gson.fromJson(arr, User.class);
        }
    }

    public static Music[] unpackMusic(JsonObject request) {
        var get = request.get("ret");
        var err = request.get("error");
        if (err != null || get.isJsonNull()) {
            if (err != null) {
                System.err.println(err.getAsString());
            }
            return null;
        } else {
            var arr = request.get("ret").getAsString();
            return gson.fromJson(arr, Music[].class);
        }
    }
    public static Playlist[] unpackPlaylist(JsonObject request) {
        var get = request.get("ret");
        var err = request.get("error");
        if (err != null || get.isJsonNull()) {
            if (err != null) {
                System.err.println(err.getAsString());
            }
            return null;
        } else {
            var arr = request.get("ret").getAsString();
            return gson.fromJson(arr, Playlist[].class);
        }
    }

    public static int unpackInt(JsonObject request) {
        var get = request.get("ret");
        var err = request.get("error");
        if (err != null || get.isJsonNull()) {
            if (err != null) {
                System.err.println(err.getAsString());
            }
            return 0;
        } else {
            var arr = request.get("ret").getAsString();
            return gson.fromJson(arr, Integer.class);
        }
    }

    /**
     * Extracts a boolean from a server response
     *
     * @param request The response from the server, in the form of either
     *                <code>true</code> or <code>false</code>
     * @return value stored in "ret" field, if possible, <code>false</code>
     *         otherwise
     */
    public static boolean unpackBool(JsonObject request) {
        var err = request.get("error");
        if (err != null) {
            System.err.println(err.getAsString());
            return false;
        } else {
            return request.get("ret").getAsBoolean();
        }
    }

    /**
     * Serializes a generic object before it is ready for transport across UDP
     *
     * @param obj The complex type that has to be serialized
     * @param <T> A type that can be serialized by Gson
     * @return A string representing the object
     */
    public static <T> String serialize(T obj) {
        return gson.toJson(obj, obj.getClass());
    }

    @Deprecated
    public static ObservableList<Music> loadDatabase() {
        var param = new String[] { "asdf" }; // Proxy requires some parameter for the request
        var songsRequest = proxy.synchExecution("loadSongs", param, Communication.Semantic.AT_LEAST_ONCE);
        var musics = unpackMusic(songsRequest);
        return FXCollections.observableArrayList(musics);
    }

    /**
     * Convenience function for calling proxy to get a sublist of songs from the
     * server
     * 
     * @param start Starting index of list
     * @param end   Ending index, does not include the value at this index
     * @param query The query used to get a list of songs that match it
     * @return <code>ObservableList</code> of songs within [start, end) of a list of
     *         songs satisfying the query
     */
    public static ObservableList<Music> loadDatabaseChunk(int start, int end, String query) {
        var params = new String[] { String.valueOf(start), String.valueOf(end), query };
        var request = proxy.synchExecution("loadChunk", params, Communication.Semantic.AT_LEAST_ONCE);
        var musics = unpackMusic(request);
        if (musics == null) {
            System.out.println("Music returned from server is null!");
        }
        return FXCollections.observableArrayList(musics);
    }

    public static ArrayList<Playlist> loadUserPlaylist(String username, String query) {
        var params = new String[] { username, query };
        var request = proxy.synchExecution("loadPlaylist", params, Communication.Semantic.AT_LEAST_ONCE);
        var playlists = unpackPlaylists(request);
        if (playlists == null) {
            System.out.println("Music returned from server is null!");
        }
        return new ArrayList<>(Arrays.asList(playlists));
    }
    /**
     * Convenience function for calling proxy to get the amount of of songs that
     * match a query
     *
     * @param query The query used to get the amount of songs that match it
     * @return Size of all songs that satisfy a query
     */
    public static int loadDatabaseChunkSize(String query) {
        var params = new String[] { query };
        var request = proxy.synchExecution("querySize", params, Communication.Semantic.AT_LEAST_ONCE);
        return unpackInt(request);
    }
}

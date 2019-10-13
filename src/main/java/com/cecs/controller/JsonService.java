package com.cecs.controller;

import com.cecs.App;
import com.cecs.model.Music;
import com.cecs.model.Playlist;
import com.cecs.model.User;
import com.google.gson.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static java.util.Arrays.binarySearch;

/**
 * Class that handles services needed by the Gson library
 */
public class JsonService {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static ObservableList<Music> loadDatabase() {
        var reader = new InputStreamReader(App.class.getResourceAsStream("/music.json"), StandardCharsets.UTF_8);
        var musics = new GsonBuilder().create().fromJson(reader, Music[].class);
        for (Music music : musics) {
            music.getSong().setArtist(music.getArtist().getName());
        }
        return FXCollections.observableArrayList(musics);
    }

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
}

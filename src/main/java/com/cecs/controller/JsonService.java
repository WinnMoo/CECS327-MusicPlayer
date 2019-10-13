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

    // TODO: Create use-case for testing
    /*
     * Function to delete a User and update users lists in json file
     */
    public static void DeleteAccount(User userToDelete) throws IOException {
        var users = loadUsers(gson);
        var newUsers = new User[users.length - 1];

        // Copy all Users to new array except for deleted User
        int deleteIdx = binarySearch(users, userToDelete);
        System.arraycopy(users, 0, newUsers, 0, users.length - 1);
        System.arraycopy(users, deleteIdx, newUsers, deleteIdx - 1, newUsers.length - deleteIdx);

        // Create string from array of Users and write to file
        var jsonUsers = gson.toJson(newUsers);
        var writer = new FileWriter("users.json");
        writer.write(jsonUsers);
        writer.close();
    }

    public static ObservableList<Music> loadDatabase() {
        var reader = new InputStreamReader(App.class.getResourceAsStream("/music.json"), StandardCharsets.UTF_8);
        var musics = new GsonBuilder().create().fromJson(reader, Music[].class);
        for (Music music : musics) {
            music.getSong().setArtist(music.getArtist().getName());
        }
        return FXCollections.observableArrayList(musics);
    }

    /**
     * Loads the users.json file into the program.
     *
     * @throws IOException If file could not be modified or created
     */
    private static User[] loadUsers(Gson gson) throws IOException {
        var file = new File("users.json");
        file.createNewFile();

        // Load current Users from user file
        var reader = new FileReader(file, StandardCharsets.UTF_8);
        return gson.fromJson(reader, User[].class);
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

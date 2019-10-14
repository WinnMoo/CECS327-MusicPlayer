package com.cecs.controller;

import com.cecs.App;
import com.cecs.def.ProxyInterface;
import com.cecs.model.Music;
import com.cecs.model.Song;
import com.cecs.model.Playlist;
import com.cecs.model.User;
import com.google.gson.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static java.util.Arrays.binarySearch;

/**
 * Class that handles services needed by the Gson library
 */
public class JsonService {
    private static ProxyInterface proxy = new Proxy(new Communication(), "MusicServices",
            Communication.Semantic.AT_LEAST_ONCE);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Function to create a new User and add the User to a JSON file
     * 
     * @param name Name of user
     * @param pass Password of user
     * 
     * @return <code>true</code> If new user is added to file, <code>false</code> if
     *         the username already exists
     * 
     * @throws IOException If file could not be modified or created
     */
    public static boolean createAccount(String name, String pass) throws IOException {
        var newUser = new User(name, pass);
        var users = loadUsers(gson);

        User[] newUsers;
        if (users == null) {
            newUsers = new User[] { newUser };
        } else {
            var len = users.length;
            newUsers = new User[len + 1];

            // Check is username is already taken
            for (var user : users) {
                if (newUser.username.equalsIgnoreCase(user.username)) {
                    return false;
                }
            }

            // Append new User to old User list
            System.arraycopy(users, 0, newUsers, 0, len);
            newUsers[len] = newUser;
            Arrays.sort(newUsers);
        }

        // Create string from array of Users and write to file
        var jsonUsers = gson.toJson(newUsers);
        var writer = new FileWriter("users.json");
        writer.write(jsonUsers);
        writer.close();

        return true;
    }

    // TODO: Create use-case for testing
    /**
     * Searches for user, and if found, returns deserialized object
     *
     * @param name Name of user
     * @param pass Password of user
     *
     * @return User if their credentials match ones in the JSON file and
     *         <code>null</code> otherwise
     *
     * @throws IOException If file could not be modified or created
     */
    public static User login(String name, String pass) throws IOException {
        var loginUser = new User(name, pass);
        var users = loadUsers(gson);

        return (users == null) ? null
                : Arrays.stream(users).filter(it -> it.username.equalsIgnoreCase(loginUser.username)
                        && it.password.equals(loginUser.password)).findFirst().orElse(null);
    }

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

    public static boolean updateUser(User newUser) throws IOException {
        var users = loadUsers(gson);

        if (users == null) {
            return false;
        } else {
            for (var user : users) {
                if (newUser.username.equalsIgnoreCase(user.username)) {
                    user.setUserPlaylists(newUser.getUserPlaylists());
                    break;
                }
            }
        }

        var jsonUsers = gson.toJson(users);
        var writer = new FileWriter("users.json");
        writer.write(jsonUsers);
        writer.close();
        return true;
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
    public static Playlist[] unpackPlaylists(JsonObject request) {
        var get = request.get("ret");
        if (get.isJsonNull()) {
            return null;
        } else {
            var arr = request.get("ret").getAsString();
            return gson.fromJson(arr, Playlist[].class);
        }
    }

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

    @Deprecated
    public static ObservableList<Music> loadDatabase() {
        var param = new String[] { "asdf" }; // Proxy requires some parameter for the request
        var songsRequest = proxy.synchExecution("loadSongs", param);
        var musics = unpackMusic(songsRequest);
        return FXCollections.observableArrayList(musics);
    }

    public static ObservableList<Music> loadDatabaseChunk(int start, int length, String query) {
        var params = new String[] { String.valueOf(start), String.valueOf(length), query };
        var request = proxy.synchExecution("loadChunk", params);
        var musics = unpackMusic(request);
        if (musics == null) {
            System.out.println("Music returned from server is null!");
        }
        return FXCollections.observableArrayList(musics);
    }

    public static int loadDatabaseChunkSize(String query) {
        var params = new String[] { query };
        var request = proxy.synchExecution("querySize", params);
        return unpackInt(request);
    }
}

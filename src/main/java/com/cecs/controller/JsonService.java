package com.cecs.controller;

import com.cecs.App;
import com.cecs.model.Music;
import com.cecs.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import static java.util.Arrays.binarySearch;

/**
 * Class that handles services needed by the Gson library
 */
public class JsonService {
    /**
     * Function to create a new User and add the User to a JSON file
     *
     * @param name Name of user
     * @param pass Password of user
     * @return <code>true</code> If new user is added to file, <code>false</code> if
     *         the username already exists
     * @throws IOException If file could not be modified or created
     */
    public static boolean createAccount(String name, String pass) throws IOException {
        var newUser = new User(name, pass);
        var gson = new GsonBuilder().setPrettyPrinting().create();
        var users = loadUsers(gson);

        User[] newUsers;
        if (users == null) {
            newUsers = new User[] { newUser };
        } else {
            var len = users.length;
            newUsers = new User[len + 1];

            // Check is username is already taken
            if (Arrays.stream(users).anyMatch(it -> it.username.equalsIgnoreCase(newUser.username))) {
                return false;
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

    /**
     * Searches for user, and if found, returns deserialized object
     *
     * @param name Name of user
     * @param pass Password of user
     * @return User if their credentials match ones in the JSON file and
     *         <code>null</code> otherwise
     * @throws IOException If file could not be modified or created
     */
    public static User login(String name, String pass) throws IOException {
        var loginUser = new User(name, pass);
        var gson = new GsonBuilder().setPrettyPrinting().create();
        var users = loadUsers(gson);

        // Check if login user is in JSON file
        return Arrays.stream(users).filter(
                it -> it.username.equalsIgnoreCase(loginUser.username) && it.password.equals(loginUser.password))
                .findFirst().orElse(null);
    }

    // TODO: Create use-case for testing
    /*
     * Function to delete a User and update users lists in json file
     */
    public static void DeleteAccount(User userToDelete) throws IOException {
        var gson = new GsonBuilder().setPrettyPrinting().create();
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

    public static boolean updateUser(User newUser) throws IOException {
        var gson = new GsonBuilder().setPrettyPrinting().create();
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

    static void save(User newerUser) throws IOException {
        var gson = new GsonBuilder().setPrettyPrinting().create();
        var users = loadUsers(gson);

        // Binary search won't help us
        var userIdx = IntStream.range(0, users.length)
                .filter(idx -> users[idx].username.equalsIgnoreCase(newerUser.username)).findFirst().getAsInt();

        // Don't modify the username stored in the database, just the playlist
        users[userIdx].userPlaylists = newerUser.userPlaylists;

        // Create string from array of Users and write to file
        var jsonUsers = gson.toJson(users);
        var writer = new FileWriter("users.json");
        writer.write(jsonUsers);
        writer.close();
    }
}

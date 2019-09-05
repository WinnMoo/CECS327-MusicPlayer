package com.cecs;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static java.util.Arrays.binarySearch;

class JsonService {
    /*
     * Function to create a new User and add the User to the users json file
     */
    static void createAccount(String user, String pass) throws IOException {
        var newUser = new User(user, pass);
        var gson = new GsonBuilder().setPrettyPrinting().create();

        // Load previous Users from user file
        var reader = new FileReader("users.json", StandardCharsets.UTF_8);
        var users = gson.fromJson(reader, User[].class);
        var newUsers = new User[users.length + 1];

        // Append new User to old User list
        System.arraycopy(users, 0, newUsers, 0, users.length);
        newUsers[users.length] = newUser;

        // Create string from array of Users and write to file
        var jsonUsers = gson.toJson(newUsers);
        var writer = new FileWriter("users.json");
        writer.write(jsonUsers);
        writer.close();
    }

    /**
     * Searches for user, and if found, returns serialized object
     */
    static User login(String user, String pass) {
        // TODO: Everything
        return new User("a", "b");
    }

    // TODO: Create use-case for testing
    /*
     * Function to delete a User and update users lists in json file
     */
    static void DeleteAccount(User userToDelete) throws IOException {
        var gson = new GsonBuilder().setPrettyPrinting().create();

        // Load current Users from user file
        var reader = new FileReader("users.json", StandardCharsets.UTF_8);
        var users = gson.fromJson(reader, User[].class);
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

    static ObservableList<Music> loadDatabase() {
        var reader = new InputStreamReader(MainPage.class.getResourceAsStream("/music.json"), StandardCharsets.UTF_8);
        var musics = new GsonBuilder().create().fromJson(reader, Music[].class);
        return FXCollections.observableArrayList(musics);
    }
}

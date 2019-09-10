package com.cecs;

import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static java.util.Arrays.binarySearch;

/**
 * Class that handles services needed by the Gson library
 */
class JsonService {
    /**
     * Function to create a new User and add the User to a JSON file
     * 
     * @throws IOException
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
        Arrays.sort(newUsers);

        // Create string from array of Users and write to file
        var jsonUsers = gson.toJson(newUsers);
        var writer = new FileWriter("users.json");
        writer.write(jsonUsers);
        writer.close();
    }

    // TODO: Create use-case for testing
    /**
     * Searches for user, and if found, returns deserialized object
     * 
     * @param name Name of user
     * @param pass Password of user
     * 
     * @return User if their credentials are found in the JSON file and
     *         <code>null</code> otherwise
     * 
     * @throws IOException
     */
    static User login(String name, String pass) throws IOException {
        var loginUser = new User(name, pass);
        var gson = new GsonBuilder().setPrettyPrinting().create();

        var reader = new FileReader("users.json", StandardCharsets.UTF_8);
        var users = gson.fromJson(reader, User[].class);

        // Check if login user is in JSON file
        for (var user : users) {
            if (user.username.equalsIgnoreCase(loginUser.username) && user.password.equals(loginUser.password)) {
                return loginUser;
            }
        }
        return null;
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

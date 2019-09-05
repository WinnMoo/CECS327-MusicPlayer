package com.cecs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
}

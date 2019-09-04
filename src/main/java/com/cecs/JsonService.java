package com.cecs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.binarySearch;

public class JsonService {

    /*
    Function to create a new User and add the User to the users json file
    */
    public static boolean CreateAccount(String user, String pass){
        User newUser = new User(user, pass);

        Gson gson = new Gson();

        try {
            var reader = new InputStreamReader(MainPage.class.getResourceAsStream("/users.json"),
                    StandardCharsets.UTF_8);
            User[] users = new GsonBuilder().create().fromJson(reader, User[].class); // Read users from users.json file

            User[] addedUsers = new User[users.length + 1]; // Create a new array for the new user

            addedUsers[users.length] = newUser;

            String jsonUsers = gson.toJson(addedUsers);

            FileWriter writer = new FileWriter("users.json"); // Write the new array of users to file
            writer.write(jsonUsers);
            writer.close();
            return true;
        } catch (NullPointerException e) {
            System.err.println("Instantiating input stream failed.");
            return false;
        } catch (JsonSyntaxException | JsonIOException e) {
            System.err.println("Could not populate music list.");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean DeleteAccount(User userToDelete){
        Gson gson = new Gson();

        try {
            var reader = new InputStreamReader(MainPage.class.getResourceAsStream("/users.json"),
                    StandardCharsets.UTF_8);
            User[] users = new GsonBuilder().create().fromJson(reader, User[].class); // Read users from users.json file

            int indexOfUserToDelete = binarySearch(users, userToDelete);
            users[indexOfUserToDelete] = userToDelete;

            String jsonUsers = gson.toJson(users);

            FileWriter writer = new FileWriter("users.json"); // Write the new array of users to file
            writer.write(jsonUsers);
            writer.close();
            return true;
        } catch (NullPointerException e) {
            System.err.println("Instantiating input stream failed.");
            return false;
        } catch (JsonSyntaxException | JsonIOException e) {
            System.err.println("Could not populate music list.");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

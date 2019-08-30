package com.cecs;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


class MainPage {
    static void show(Stage stage) {
        try{

            Reader reader = new InputStreamReader(MainPage.class.getResourceAsStream("/music.json"), "UTF-8");
            Gson gson = new GsonBuilder().create();
            Music[] musics = gson.fromJson(reader, Music[].class);
            for(Music music : musics)
            System.out.println(music.getArtist().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }


        var col = new VBox(new Label("Welcome"));
        var scene = new Scene(col);

        stage.setScene(scene);
        stage.show();
    }
}

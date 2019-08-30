package com.cecs;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.google.gson.GsonBuilder;


class MainPage {
    static void show(Stage stage, User user) {
        var musics = new Music[0];
        try {
            var reader = new InputStreamReader(MainPage.class.getResourceAsStream("/music.json"), StandardCharsets.UTF_8);
            musics = new GsonBuilder().create().fromJson(reader, Music[].class);
        } catch (NullPointerException e) {
            System.err.println("Instantiating input stream failed.");
        } catch (JsonSyntaxException | JsonIOException e) {
            System.err.println("Could not populate music list.");
        }

        var listOfMusic = FXCollections.observableArrayList(musics);

        final var label = new Text("Welcome back, " + user.username);
        label.setFont(Font.font(null, FontPosture.ITALIC, 24.0));

        var songs = new TableColumn<Music, String>("Song");
        songs.setCellValueFactory(new PropertyValueFactory<>("song"));
        var releases = new TableColumn<Music, String>("Release");
        releases.setCellValueFactory(new PropertyValueFactory<>("release"));
        var artists = new TableColumn<Music, String>("Artist");
        artists.setCellValueFactory(new PropertyValueFactory<>("artist"));

        var list = new FilteredList<>(listOfMusic, m -> true);
        var table = new TableView<>(list);
        table.setEditable(true);
        table.getColumns().addAll(songs, releases, artists);

        var searchBar = new TextField();
        searchBar.setPromptText("Search for artist, release, or song...");
        searchBar.setOnKeyReleased(keyEvent -> {
            list.setPredicate(p -> {
                final var query = searchBar.getText().toLowerCase().trim();
                return p.getArtist().toString().toLowerCase().contains(query) ||
                p.getRelease().toString().toLowerCase().contains(query) ||
                p.getSong().toString().toLowerCase().contains(query);
            });
        });

        final var col = new VBox(label, searchBar, table);
        col.setSpacing(10.0);
        col.setPadding(new Insets(25.0));
        final var scene = new Scene(col, 800, 600);

        stage.setScene(scene);
        stage.show();
    }
}

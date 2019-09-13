package com.cecs;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Slider;

import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicInteger;

class MainPage {
    static void show(Stage stage, User user) {
        int songIndex = 0;
        SongPlayer player = new SongPlayer();

        var listOfMusic = FXCollections.<Music>observableArrayList();
        Flowable.fromCallable(JsonService::loadDatabase).subscribe(listOfMusic::addAll, Throwable::printStackTrace);

        final var label = new Text("Welcome back, " + user.username);
        label.setFont(Font.font(null, FontPosture.ITALIC, 24.0));

        var playbackSlider = new Slider();
        var playButton = new Button("▶");
        var nextSongButton = new Button("⏭");
        var prevSongButton = new Button("⏮");
        var pauseSongButton = new Button("⏸");

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
                return p.getArtist().toString().toLowerCase().contains(query)
                        || p.getRelease().toString().toLowerCase().contains(query)
                        || p.getSong().toString().toLowerCase().contains(query);
            });
        });

        prevSongButton.setOnAction(action -> {
            int currentIndex = table.getSelectionModel().getSelectedIndex();
            int prevIndex = currentIndex - 1;
            var song = table.getItems().get(prevIndex);
            stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
            // SongPlayer.playSong(song.getSong().getId() + ".mp3");
        });

        playButton.setOnAction(action -> {
            try {
                var song = table.getSelectionModel().getSelectedItem();
                System.out.println("Playing " + song);
                stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
                player.playSong(song.getSong().getId() + ".mp3");
            } catch (NullPointerException e) { // Catch for case when there's no selected item
                var song = table.getItems().get(songIndex);
                System.out.println("Playing from catch block " + song);
                stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
                player.playSong(song.getSong().getId() + ".mp3");
            }
        });

        pauseSongButton.setOnAction(action -> {
            player.pauseSong();
            stage.setTitle("Music Player 1.0");
        });

        nextSongButton.setOnAction(action -> {
            int currentIndex = table.getSelectionModel().getSelectedIndex();
            int nextIndex = currentIndex + 1;
            var song = table.getItems().get(nextIndex);
            stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
            // SongPlayer.playSong(song.getSong().getId());
        });

        var controlButtonRow = new BorderPane();
        controlButtonRow.setLeft(prevSongButton);
        controlButtonRow.setLeft(playButton);
        controlButtonRow.setCenter(pauseSongButton);
        controlButtonRow.setRight(nextSongButton);
        controlButtonRow.setMaxWidth(250.0);

        final var col = new VBox(label, searchBar, table, playbackSlider, controlButtonRow);
        col.setSpacing(10.0);
        col.setPadding(new Insets(25.0));
        final var scene = new Scene(col, 800, 600);

        stage.setScene(scene);
        stage.show();
    }
}

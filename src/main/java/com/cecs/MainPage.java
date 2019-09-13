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

        var playbackSlider = new Slider();
        playbackSlider.setOnMouseReleased(it -> {
            player.updateTrack(playbackSlider.getValue());
        });
        player.getEvents().subscribe(playbackSlider::setValue, Throwable::printStackTrace);

        var prevSongButton = new Button("⏮");
        prevSongButton.setOnAction(action -> {
            int currentIndex = table.getSelectionModel().getSelectedIndex();
            int prevIndex = currentIndex - 1;
            var song = table.getItems().get(prevIndex);
            table.getSelectionModel().select(prevIndex);
            stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
            player.playSong(song.getSong().getId() + ".mp3");
        });

        var playButton = new Button("▶");
        playButton.setOnAction(action -> {
            if (playButton.getText().equals("▶")) { // lol
                try {
                    var song = table.getSelectionModel().getSelectedItem();
                    stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
                    player.playSong(song.getSong().getId() + ".mp3");
                } catch (NullPointerException e) { // Catch for case when there's no selected item
                    var song = table.getItems().get(songIndex);
                    stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
                    player.playSong(song.getSong().getId() + ".mp3");
                }
                playButton.setText("⏸");
            } else {
                player.pauseSong();
                stage.setTitle("Music Player 1.0");
                playButton.setText("▶");
            }
        });

        var nextSongButton = new Button("⏭");
        nextSongButton.setOnAction(action -> {
            int currentIndex = table.getSelectionModel().getSelectedIndex();
            int nextIndex = currentIndex + 1;
            var song = table.getItems().get(nextIndex);
            table.getSelectionModel().select(nextIndex);
            stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
            player.playSong(song.getSong().getId() + ".mp3");
        });

        var controlButtonRow = new BorderPane();
        controlButtonRow.setLeft(prevSongButton); // Currently throws Exception
        controlButtonRow.setCenter(playButton);
        controlButtonRow.setRight(nextSongButton);
        controlButtonRow.setMaxWidth(750.0);

        final var col = new VBox(label, searchBar, table, playbackSlider, controlButtonRow);
        col.setSpacing(10.0);
        col.setPadding(new Insets(25.0));
        final var scene = new Scene(col, 800, 600);

        stage.setScene(scene);
        stage.show();
    }
}

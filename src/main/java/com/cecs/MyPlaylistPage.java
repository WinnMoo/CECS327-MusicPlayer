package com.cecs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MyPlaylistPage {
    static void show(Stage stage, SongPlayer player, User user) {
        var label = new Text("Playlists of " + user.username);
        label.setFont(Font.font(null, FontPosture.ITALIC, 24.0));

        var btMainPage = new Button("Search songs");
        btMainPage.setOnAction(e -> MainPage.show(stage, player, user));

        ObservableList<Song> songs = FXCollections.observableArrayList();
        if (user.getUserPlaylists().size() > 0) {
            songs.addAll(user.getUserPlaylists().get(0).getSongs());
        }

        // Columns
        TableColumn<Song, String> titleCol = new TableColumn<>("Title");
        titleCol.setMinWidth(200);
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Song, String> artistCol = new TableColumn<>("Artist");
        artistCol.setMinWidth(200);
        artistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));

        TableView<Song> table = new TableView<>(songs);
        table.setEditable(true);
        table.getColumns().addAll(titleCol, artistCol);

        var obv = FXCollections.<String>observableArrayList();
        // var map = new HashMap<String, Playlist>();

        for (Playlist pl : user.getUserPlaylists()) {
            obv.add(pl.getName());
            // map.put(pl.getName(), pl);
        }

        var cbMyPlaylist = new ComboBox<>(obv);
        cbMyPlaylist.setValue((obv.isEmpty()) ? "" : obv.get(0));
        cbMyPlaylist.setOnAction(e -> {
            for (Playlist pl : user.getUserPlaylists()) {
                if (pl.getName().equals(cbMyPlaylist.getValue())) {
                    songs.setAll(pl.getSongs());
                }
            }
            table.setItems(songs);
        });

        var line = new HBox(cbMyPlaylist, btMainPage);
        line.setSpacing(10.0);

        // Track slider, controls when to stop/continue track updates
        var playbackSlider = new Slider();
        playbackSlider.setDisable(true);
        playbackSlider.setOnMouseReleased(it -> {
            player.unblockUpdates();
            player.updateTrack(playbackSlider.getValue());
        });
        playbackSlider.setOnMouseDragged(it -> {
            player.blockUpdates();
        });
        player.getEvents().subscribe(playbackSlider::setValue, Throwable::printStackTrace);

        var playButton = new Button("▶");
        playButton.setDisable(true);
        playButton.setOnAction(action -> {
            if (playButton.getText().equals("▶")) { // lol
                var song = table.getSelectionModel().getSelectedItem();
                stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getTitle());
                player.playSong(song.getId() + ".mp3");
                playButton.setText("⏸");
                playbackSlider.setDisable(false);
            } else {
                player.pauseSong();
                stage.setTitle("Music Player 1.0");
                playButton.setText("▶");
            }
        });

        var prevSongButton = new Button("⏮");
        prevSongButton.setDisable(true);
        prevSongButton.setOnAction(action -> {
            table.getSelectionModel().selectPrevious();
            var song = table.getSelectionModel().getSelectedItem();
            stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getTitle());
            player.playSong(song.getId() + ".mp3");
            playButton.setText("⏸");
        });

        var nextSongButton = new Button("⏭");
        nextSongButton.setDisable(true);
        nextSongButton.setOnAction(action -> {
            table.getSelectionModel().selectNext();
            var song = table.getSelectionModel().getSelectedItem();
            stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getTitle());
            player.playSong(song.getId() + ".mp3");
            playButton.setText("⏸");
        });

        table.getSelectionModel().selectedItemProperty().addListener((_0, _1, newSelection) -> {
            if (newSelection == null) {
                prevSongButton.setDisable(true);
                playButton.setDisable(true);
                nextSongButton.setDisable(true);
            } else {
                var currentIndex = table.getSelectionModel().getSelectedIndex();
                var prevDisabled = currentIndex == 0;
                var nextDisabled = currentIndex == table.getItems().size() - 1; // Not tested yet

                if (player.nowPlaying() != null) {
                    if (!player.nowPlaying().equals(newSelection.getId() + ".mp3")) {
                        playButton.setText("▶");
                    } else {
                        playButton.setText("⏸");
                    }
                } else {
                    prevDisabled = true;
                    nextDisabled = true;
                }

                prevSongButton.setDisable(prevDisabled);
                playButton.setDisable(false);
                nextSongButton.setDisable(nextDisabled);
            }
        });

        var controlButtonRow = new BorderPane();
        controlButtonRow.setLeft(prevSongButton);
        controlButtonRow.setCenter(playButton);
        controlButtonRow.setRight(nextSongButton);

        final var col = new VBox(label, line, table, playbackSlider, controlButtonRow);
        col.setSpacing(10.0);
        col.setPadding(new Insets(25.0));
        final var scene = new Scene(col, 800, 600);
        stage.setScene(scene);
        stage.show();

    }
}

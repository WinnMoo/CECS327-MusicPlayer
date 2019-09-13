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
        playbackSlider.setDisable(true);
        playbackSlider.setOnMouseReleased(it -> {
            player.unblockUpdates();;
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
                stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
                player.playSong(song.getSong().getId() + ".mp3");
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
            int currentIndex = table.getSelectionModel().getSelectedIndex();
            int prevIndex = currentIndex - 1;
            var song = table.getItems().get(prevIndex);
            table.getSelectionModel().select(prevIndex);
            stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
            player.playSong(song.getSong().getId() + ".mp3");
            playButton.setText("⏸");
        });

        var nextSongButton = new Button("⏭");
        nextSongButton.setDisable(true);
        nextSongButton.setOnAction(action -> {
            int currentIndex = table.getSelectionModel().getSelectedIndex();
            int nextIndex = currentIndex + 1;
            var song = table.getItems().get(nextIndex);
            table.getSelectionModel().select(nextIndex);
            stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
            player.playSong(song.getSong().getId() + ".mp3");
            playButton.setText("⏸");
        });

        table.getSelectionModel().selectedItemProperty().addListener((_0, oldSelection, newSelection) -> {
            System.out.println("Focus changed!");
            if (newSelection == null) {
                System.out.println("Nothing is selected.");
                prevSongButton.setDisable(true);
                playButton.setDisable(true);
                nextSongButton.setDisable(true);
            } else {
                var currentIndex = table.getSelectionModel().getSelectedIndex();
                var prevDisabled = currentIndex == 0;
                var nextDisabled = currentIndex == table.getItems().size() - 1;

                if (player.nowPlaying() != null) {
                    if (!player.nowPlaying().equals(newSelection.getSong().getId() + ".mp3")) {
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
        controlButtonRow.setMaxWidth(750.0);

        final var col = new VBox(label, searchBar, table, playbackSlider, controlButtonRow);
        col.setSpacing(10.0);
        col.setPadding(new Insets(25.0));
        final var scene = new Scene(col, 800, 600);

        stage.setScene(scene);
        stage.show();
    }
}

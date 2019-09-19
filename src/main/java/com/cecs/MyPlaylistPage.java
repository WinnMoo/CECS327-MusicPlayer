package com.cecs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import java.io.IOException;

class MyPlaylistPage {
    static void show(Stage stage, SongPlayer player, User user) {
        // Main Menu
        var viewAll = new MenuItem("View All");
        viewAll.setOnAction(action -> {
            System.out.println("View all selected");
            MainPage.show(stage, player, user);
        });
        var mainMenu = new Menu("All Songs", null, viewAll);

        // Playlist Menu
        var playlistItem = new MenuItem("Go to Playlists");
        playlistItem.setOnAction(action -> {
            MyPlaylistPage.show(stage, player, user);
        });
        var playlistMenu = new Menu("Playlists", null, playlistItem);

        // Profile Menu
        var profileMenu = new Menu("User Profile");

        // Settings Menu Items
        var menuSlider = new Slider(0, 100, 50);
        var customMenuItem = new CustomMenuItem();
        customMenuItem.setContent(menuSlider);
        customMenuItem.setHideOnClick(false);
        var otherSettingItem = new MenuItem("Other Settings Item");
        otherSettingItem.setOnAction(action -> {
            System.out.println("Other setting selected");
            // TODO: Functionality, use your imagination.
        });
        var settingsMenu = new Menu("Settings", null, customMenuItem, otherSettingItem);

        // Menu Bar
        var menuBar = new MenuBar(mainMenu, playlistMenu, profileMenu, settingsMenu);



        var label = new Text("Playlists of " + user.username);
        label.setFont(Font.font(null, FontPosture.ITALIC, 24.0));

        ObservableList<Song> songs = FXCollections.observableArrayList();
        if (user.getUserPlaylists().size() > 0) {
            songs.addAll(user.getUserPlaylists().get(0).getSongs());
        }

        var obv = FXCollections.<String>observableArrayList();

        for (Playlist pl : user.getUserPlaylists()) {
            obv.add(pl.getName());
        }
        var cbMyPlaylist = new ComboBox<>(obv);
        TableView<Song> table = new TableView<>(songs);
        // Columns
        TableColumn<Song, String> titleCol = new TableColumn<>("Title");
        titleCol.setMinWidth(200);
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Song, String> artistCol = new TableColumn<>("Artist");
        artistCol.setMinWidth(200);
        artistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));

        var colBtDelete = new TableColumn<Song, Void>("Delete Song");
        colBtDelete.setCellFactory((TableColumn<Song, Void> features) -> new TableCell<>() {

            private final Button btn = new Button("Delete" );
            {
                btn.setOnAction((ActionEvent event) -> {
                    Song song = getTableView().getItems().get(getIndex());
                    songs.remove(song);
                    table.setItems(songs);

                    user.findPlaylistByName(cbMyPlaylist.getValue()).removeSong(song);
                    try {
                        JsonService.updateUser(user);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        table.setEditable(true);
        table.getColumns().addAll(titleCol, artistCol, colBtDelete);

        cbMyPlaylist.setValue((obv.isEmpty()) ? "" : obv.get(0));
        cbMyPlaylist.setOnAction(e -> {
            songs.clear();
            for (Playlist pl : user.getUserPlaylists()) {
                if (pl.getName().equals(cbMyPlaylist.getValue())) {
                    songs.setAll(pl.getSongs());
                }
            }
            table.setItems(songs);
        });

        var btDelete = new Button("Delete");
        btDelete.setOnAction(e -> {
            user.deletePlaylist(user.findPlaylistByName(cbMyPlaylist.getValue()));
            obv.remove(cbMyPlaylist.getValue());
            cbMyPlaylist.setValue((obv.isEmpty()) ? "" : obv.get(0));

            try {
                JsonService.updateUser(user);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });

        var line = new HBox(cbMyPlaylist, btDelete);
        line.setSpacing(20.0);

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

        final var borderpane = new BorderPane();
        borderpane.setTop(menuBar);

        final var col = new VBox(label, line, table, playbackSlider);
        borderpane.setCenter(col);
        borderpane.setBottom(controlButtonRow);
        col.setSpacing(10.0);
        col.setPadding(new Insets(25.0));
        final var scene = new Scene(borderpane, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}

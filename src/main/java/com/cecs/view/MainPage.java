package com.cecs.view;

import com.cecs.controller.*;
import com.cecs.def.ProxyInterface;
import io.reactivex.Flowable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

import com.cecs.model.Music;
import com.cecs.model.Playlist;
import com.cecs.model.Song;
import com.cecs.model.User;

public class MainPage {
    private static ProxyInterface proxy = new Proxy(new Communication(), "UserServices",
            Communication.Semantic.AT_MOST_ONCE);

    public static void show(Stage stage, SongPlayer player, User user) {
        final int rowsPerPage = 20;
        final int listSize = 10000;
        final String[] query = new String[] { "" };

        // Main Menu
        var viewAll = new MenuItem("View All");
        viewAll.setOnAction(action -> MainPage.show(stage, player, user));
        var mainMenu = new Menu("All Songs", null, viewAll);

        // Playlist Menu
        var playlistItem = new MenuItem("Go to Playlists");
        playlistItem.setOnAction(action -> MyPlaylistPage.show(stage, player, user));
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
            // TODO: Possibly add account deletion and server configuration here!
        });
        var settingsMenu = new Menu("Settings", null, customMenuItem, otherSettingItem);

        // Menu Bar
        var menuBar = new MenuBar(mainMenu, playlistMenu, profileMenu, settingsMenu);

        var listOfMusic = FXCollections.<Music>observableArrayList();
        Flowable.fromCallable(() -> JsonService.loadDatabaseChunk(0, rowsPerPage, "")).subscribe(listOfMusic::addAll,
                Throwable::printStackTrace);
        final var label = new Text("Welcome back, " + user.username);
        label.setFont(Font.font(null, FontPosture.ITALIC, 24.0));

        // Create list of Playlist's name for comboBox cbMyPlaylist
        var obv = FXCollections.<String>observableArrayList();
        for (Playlist pl : user.getUserPlaylists()) {
            obv.add(pl.getName());
        }

        // column Add Playlist button
        var colBtn = new TableColumn<Music, Void>("Add to Playlist");
        var cbMyPlaylist = new ComboBox<>(obv);
        cbMyPlaylist.setEditable(true); // So, user can enter new playlist

        // Setup comboBox
        if (obv.isEmpty()) {
            cbMyPlaylist.setPromptText("Create new playlist here");
        } else {
            cbMyPlaylist.setValue(obv.get(0));
        }

        // Update button text when user change the playlist
        cbMyPlaylist.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                // update button text with t1
                colBtn.setCellFactory((TableColumn<Music, Void> features) -> new TableCell<>() {
                    // Create Add Playlist button
                    // String name = (obv.isEmpty()) ? "New Playlist" : (String) obv.get(0);
                    private final Button btn = new Button("Add to " + t1);
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Music music = getTableView().getItems().get(getIndex());
                            Song song = music.getSong();
                            String plName = cbMyPlaylist.getValue();
                            if (user.containsPlaylist(plName)) {
                                for (Playlist pl : user.getUserPlaylists()) {
                                    if (pl.getName().equals(plName))
                                        pl.addSong(song);
                                }
                            } else {
                                Playlist pl = new Playlist(plName, new ArrayList<>(List.of(song)));
                                // user.getUserPlaylists().add(pl);
                                user.addNewPlaylist(pl);
                                obv.add(plName);
                                // cbMyPlaylist.getItems().add(plName);
                            }
                            proxy.synchExecution("updateUser", new String[] { JsonService.serialize(user) });
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
            }
        });

        var songs = new TableColumn<Music, String>("Song");
        songs.setCellValueFactory(new PropertyValueFactory<>("song"));
        var releases = new TableColumn<Music, String>("Release");
        releases.setCellValueFactory(new PropertyValueFactory<>("release"));
        var artists = new TableColumn<Music, String>("Artist");
        artists.setCellValueFactory(new PropertyValueFactory<>("artist"));

        colBtn.setCellFactory((TableColumn<Music, Void> features) -> new TableCell<>() {
            // Create Add Playlist button
            String name = (obv.isEmpty()) ? "New Playlist" : obv.get(0);
            private final Button btn = new Button("Add to " + name);
            {
                btn.setOnAction((ActionEvent event) -> {
                    Music music = getTableView().getItems().get(getIndex());
                    Song song = music.getSong();
                    String plName = cbMyPlaylist.getValue();
                    if (user.containsPlaylist(plName)) {
                        for (Playlist pl : user.getUserPlaylists()) {
                            if (pl.getName().equals(plName))
                                pl.addSong(song);
                        }
                    } else {
                        String name = (plName == null) ? "Playlist" : plName;
                        Playlist pl = new Playlist(name, new ArrayList<>(List.of(song)));
                        user.getUserPlaylists().add(pl);
                        obv.add(name);
                        cbMyPlaylist.setValue(name);
                    }
                    proxy.synchExecution("updateUser", new String[] { JsonService.serialize(user) });
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

        var table = new TableView<>(listOfMusic);

        table.setEditable(true);
        table.getColumns().addAll(songs, releases, artists, colBtn);

        // Pagination

        Pagination pagination = new Pagination(listSize / rowsPerPage + 1, 0);
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * rowsPerPage;
            int toIndex = Math.min(fromIndex + rowsPerPage, listSize);
            System.out.println("Loading data from " + fromIndex + " to " + toIndex);
            var musics = JsonService.loadDatabaseChunk(fromIndex, toIndex, query[0]);
            listOfMusic.setAll(musics);

            return new BorderPane(table);
        });

        var searchButton = new Button("Search");
        var searchBar = new TextField();

        searchButton.setOnAction(action -> {
            query[0] = searchBar.getText().toLowerCase().trim();
            var musics = JsonService.loadDatabaseChunk(0, 20, query[0]);
            var size = JsonService.loadDatabaseChunkSize(query[0]);

            listOfMusic.setAll(musics);
            pagination.setCurrentPageIndex(0);
            pagination.setPageCount(size / rowsPerPage + 1);
        });

        searchBar.setPromptText("Search for artist, release, or song...");
        searchBar.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                searchButton.fire();
            }
        });
        searchBar.prefWidthProperty().bind(Bindings.divide(stage.widthProperty(), 2));

        // De-clutter bottom of track slider
        var searchRow = new HBox(searchBar, searchButton, cbMyPlaylist);
        searchRow.setSpacing(10.0);

        // Track slider, controls when to stop/continue track updates
        var playbackSlider = new Slider();
        playbackSlider.setDisable(true);
        playbackSlider.setOnMouseReleased(it -> {
            player.unblockUpdates();
            player.updateTrack(playbackSlider.getValue());
        });
        playbackSlider.setOnMouseDragged(it -> player.blockUpdates());
        player.getEvents().subscribe(playbackSlider::setValue, Throwable::printStackTrace);

        var playButton = new Button("▶");
        var prevSongButton = new Button("⏮");
        var nextSongButton = new Button("⏭");

        playButton.setDisable(true);
        playButton.setOnAction(action -> {
            if (playButton.getText().equals("▶")) { // lol
                var song = table.getSelectionModel().getSelectedItem();
                stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
                player.playSong(song.getSong().getId() + ".mp3");
                playButton.setText("⏸");
                playbackSlider.setDisable(false);

                var idx = table.getSelectionModel().getSelectedIndex();
                if (Utils.canPlayPrev(idx)) {
                    prevSongButton.setDisable(false);
                }
                if (Utils.canPlayNext(idx, table.getItems().size())) {
                    nextSongButton.setDisable(false);
                }
            } else {
                player.pauseSong();
                stage.setTitle("Music Player 1.0");
                playButton.setText("▶");
            }
        });

        prevSongButton.setDisable(true);
        prevSongButton.setOnAction(action -> {
            table.getSelectionModel().selectPrevious();
            var song = table.getSelectionModel().getSelectedItem();
            stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
            player.playSong(song.getSong().getId() + ".mp3");
            playButton.setText("⏸");
        });

        nextSongButton.setDisable(true);
        nextSongButton.setOnAction(action -> {
            table.getSelectionModel().selectNext();
            var song = table.getSelectionModel().getSelectedItem();
            stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
            player.playSong(song.getSong().getId() + ".mp3");
            playButton.setText("⏸");
        });

        // Convoluted logic for determining when a play previous/next is warranted
        table.getSelectionModel().selectedItemProperty().addListener((_0, _1, newSelection) -> {
            if (newSelection == null) {
                prevSongButton.setDisable(true);
                playButton.setDisable(true);
                nextSongButton.setDisable(true);
            } else {
                var currentIndex = table.getSelectionModel().getSelectedIndex();
                var prevDisabled = !Utils.canPlayPrev(currentIndex);
                var nextDisabled = !Utils.canPlayNext(currentIndex, table.getItems().size());

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

        var vbox = new VBox(label, searchRow, pagination, playbackSlider);
        vbox.setSpacing(10.0);
        vbox.setPadding(new Insets(25.0));

        var controlButtonRow = new BorderPane();
        controlButtonRow.setLeft(prevSongButton);
        controlButtonRow.setCenter(playButton);
        controlButtonRow.setRight(nextSongButton);
        controlButtonRow.setPadding(new Insets(20.0));

        final var borderPane = new BorderPane();
        borderPane.setTop(menuBar);
        borderPane.setCenter(vbox);
        borderPane.setBottom(controlButtonRow);
        final var scene = new Scene(borderPane, 800, 600);

        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(close -> {
            player.pauseSong();
        });
    }
}
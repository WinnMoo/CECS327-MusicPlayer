package com.cecs;

import io.reactivex.Flowable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
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
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MainPage {
    static void show(Stage stage, User user) {
        // Initialize Song Player service
        SongPlayer player = new SongPlayer();

        // Main Menu
        var viewAll = new MenuItem("View All");
        viewAll.setOnAction(action -> {
            System.out.println("View all selected");
            // TODO: Functionality, use your imagination.
        });
        var mainMenu = new Menu("All Songs", null, viewAll);

        // Playlist Menu
        var playlistItem = new MenuItem("Go to Playlists");
        playlistItem.setOnAction(action -> {
            PlaylistPage.show(stage, user);
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


        /*Menu MainMenu = new Menu("All Songs");
        Menu PlaylistMenu = new Menu("Playlists");
        Menu ProfileMenu = new Menu("User Profile");
        Menu SettingsMenu = new Menu("Settings");

        //Main Menu
        Button mainpageButton = new Button("View All");
        CustomMenuItem customMenuItem_Main = new CustomMenuItem();
        customMenuItem_Main.setContent(mainpageButton);
        customMenuItem_Main.setHideOnClick(false);
        MainMenu.getItems().add(customMenuItem_Main);

        //Main Menu
        Button PlaylistMenuButton = new Button("View All");
        CustomMenuItem playlistItem = new CustomMenuItem();
        playlistItem.setContent(PlaylistMenuButton);
        playlistItem.setHideOnClick(false);
        PlaylistMenu.getItems().add(playlistItem);


        //Settings Menu Items
        Slider slider = new Slider(0, 100, 50);
        CustomMenuItem customMenuItem = new CustomMenuItem();
        customMenuItem.setContent(slider);
        customMenuItem.setHideOnClick(false);
        SettingsMenu.getItems().add(customMenuItem);

        Button customButton = new Button("Custom Menu Item Button");
        CustomMenuItem customMenuItem2 = new CustomMenuItem();
        customMenuItem2.setContent(customButton);
        customMenuItem2.setHideOnClick(false);
        SettingsMenu.getItems().add(customMenuItem2);

        MainMenu.getItems().add(customMenuItem_Main);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(MainMenu);
        menuBar.getMenus().add(PlaylistMenu);
        menuBar.getMenus().add(ProfileMenu);
        menuBar.getMenus().add(SettingsMenu);

        menuBar.prefWidthProperty().bind(stage.widthProperty());*/

        var listOfMusic = FXCollections.<Music>observableArrayList();
        Flowable.fromCallable(JsonService::loadDatabase).subscribe(listOfMusic::addAll, Throwable::printStackTrace);
        final var label = new Text("Welcome back, " + user.username);
        label.setFont(Font.font(null, FontPosture.ITALIC, 24.0));

        var myPlaylist = new Button("My Playlist");

        var songs = new TableColumn<Music, String>("Song");
        songs.setCellValueFactory(new PropertyValueFactory<>("song"));
        var releases = new TableColumn<Music, String>("Release");
        releases.setCellValueFactory(new PropertyValueFactory<>("release"));
        var artists = new TableColumn<Music, String>("Artist");
        artists.setCellValueFactory(new PropertyValueFactory<>("artist"));

        var list = new FilteredList<>(listOfMusic, m -> true);
        var table = new TableView<>(list);

        TableColumn<Music, Void> colBtn = new TableColumn("Add to Playlist");

        List<String> pls = new ArrayList<>();
        Map<String, Playlist> map = new HashMap<>();

        for(Playlist pl: user.getUserPlaylists()){
            pls.add(pl.getName());
            map.put(pl.getName(), pl);

        }

        //ObservableList<String> playlists = FXCollections.observableArrayList(pls);
        ComboBox cbMyPlaylist = new ComboBox<>();
        cbMyPlaylist.getItems().addAll(pls);

        cbMyPlaylist.setEditable(true);
        cbMyPlaylist.setValue((pls.isEmpty()) ? "" : pls.get(0));

        // Create Add Playlist button
        Callback<TableColumn<Music, Void>, TableCell<Music, Void>> btnCellFact = new Callback<TableColumn<Music, Void>, TableCell<Music, Void>>() {
            @Override
            public TableCell<Music, Void> call(final TableColumn<Music, Void> param) {
                final TableCell<Music, Void> cell = new TableCell<Music, Void>() {

                    private final Button btn = new Button("Add");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Music music = getTableView().getItems().get(getIndex());
                            Song song =  music.getSong();
                            String plName = (String) cbMyPlaylist.getValue();
                            if(user.containsPlaylist(plName)){
                                for(Playlist pl : user.getUserPlaylists()){
                                    if(pl.getName().equals(plName))
                                        pl.addSong(song);
                                }
                            }
                            else {
                                Playlist pl = new Playlist();
                                pl.setName(plName);
                                pl.addSong(song);
                                user.getUserPlaylists().add(pl);
                                pls.add(plName);
                                cbMyPlaylist.getItems().add(plName);
                            }
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
                };
                return cell;
            }
        };

        colBtn.setCellFactory(btnCellFact);

        table.setEditable(true);
        table.getColumns().addAll(songs, releases, artists, colBtn);

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
            table.getSelectionModel().selectPrevious();
            var song = table.getSelectionModel().getSelectedItem();
            stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
            player.playSong(song.getSong().getId() + ".mp3");
            playButton.setText("⏸");
        });

        var nextSongButton = new Button("⏭");
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
                var prevDisabled = currentIndex == 0;
                var nextDisabled = currentIndex == table.getItems().size() - 1; // Not tested yet

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
        controlButtonRow.setPadding(new Insets(20.0));
        var headline = new HBox(label, myPlaylist);
        headline.setSpacing(30.0);
        var vbox = new VBox(label, searchBar, table, playbackSlider);
        vbox.getChildren().add(headline);
        vbox.getChildren().add(cbMyPlaylist);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(25.0));

        final var borderPane = new BorderPane();
        borderPane.setCenter(vbox);
        borderPane.setTop(menuBar);
        borderPane.setBottom(controlButtonRow);
        // col.setPadding(new Insets(25.0));
        // Go to MyPlaylistPage
        myPlaylist.setOnAction(actionEvent -> MyPlaylistPage.show(stage, user));
        final var scene = new Scene(borderPane, 800, 600);

        
        stage.setScene(scene);
        stage.show();
    }
}

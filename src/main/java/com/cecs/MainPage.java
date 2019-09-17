package com.cecs;

import io.reactivex.Flowable;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
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
import javafx.scene.control.MenuBar;


class MainPage {
    static void show(Stage stage, User user) {


        Menu MainMenu = new Menu("All Songs");
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

        menuBar.prefWidthProperty().bind(stage.widthProperty());






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

        table.getSelectionModel().selectedItemProperty().addListener((_0, _1, newSelection) -> {
            if (newSelection == null) {
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

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(25,25,25,25));
        vbox.getChildren().add(label);
        vbox.getChildren().add(searchBar);
        vbox.getChildren().add(table);
        vbox.getChildren().add(playbackSlider);

//        final var musicRow = new vBox(label, searchBar, table, playbackSlider);
//        musicRow.setSpacing(10.0);
//        musicRow.setPadding(new Insets(10.0));

        BorderPane menuPane = new BorderPane();
        menuPane.setTop(menuBar);


        //final var col = new VBox(menuPane, label, searchBar, table, playbackSlider, controlButtonRow);
        final var col = new VBox(menuPane, vbox, controlButtonRow);
        col.setSpacing(10.0);
        //col.setPadding(new Insets(25.0));
        final var scene = new Scene(col, 800, 600);


        stage.setScene(scene);
        stage.show();
    }
}

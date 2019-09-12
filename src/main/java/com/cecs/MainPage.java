package com.cecs;

import io.reactivex.Flowable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.util.Callback;

import javafx.event.ActionEvent;

class MainPage {
    static void show(Stage stage, User user) {
        int songIndex = 0;

        var listOfMusic = FXCollections.<Music>observableArrayList();
        Flowable.fromCallable(JsonService::loadDatabase).subscribe(listOfMusic::addAll, Throwable::printStackTrace);

        final var label = new Text("Welcome back, " + user.username);
        label.setFont(Font.font(null, FontPosture.ITALIC, 24.0));

        var playbackSlider  = new Slider();
        var playButton = new Button("|>");
        var nextSongButton = new Button(">>");
        var prevSongButton = new Button("<<");

        var songs = new TableColumn<Music, String>("Song");
        songs.setCellValueFactory(new PropertyValueFactory<>("song"));
        var releases = new TableColumn<Music, String>("Release");
        releases.setCellValueFactory(new PropertyValueFactory<>("release"));
        var artists = new TableColumn<Music, String>("Artist");
        artists.setCellValueFactory(new PropertyValueFactory<>("artist"));

        var list = new FilteredList<>(listOfMusic, m -> true);
        var table = new TableView<>(list);

        // add Add Playlist button
        TableColumn<Music, Void> colBtn = new TableColumn("Add to Playlist");

        Callback<TableColumn<Music, Void>, TableCell<Music, Void>> btnCellFact = new Callback<TableColumn<Music, Void>, TableCell<Music, Void>>() {
            @Override
            public TableCell<Music, Void> call(final TableColumn<Music, Void> param) {
                final TableCell<Music, Void> cell = new TableCell<Music, Void>() {

                    private final Button btn = new Button("Add");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Music music = getTableView().getItems().get(getIndex());
                            System.out.println("selected song: " + music.getSong().getId());
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

        // Playlists ComboBox
        TableColumn<Music, Playlist> colCob = new TableColumn("My Playlists");
        ObservableList<Playlist> playlists = FXCollections.observableArrayList(user.userPlaylists);
        colCob.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<Playlist> call(TableColumn.CellDataFeatures<Music, Playlist> userVoidCellDataFeatures) {

                Playlist playlist = null;
                String playlistName;
                if(!user.userPlaylists.isEmpty()) {
                    playlist = user.userPlaylists.get(0);
                    playlistName = playlist.getName();
                }

                return new SimpleObjectProperty<>(playlist);
            }
        });
        colCob.setCellFactory(tableCol -> {
            ComboBoxTableCell<Music, Playlist> ct = new ComboBoxTableCell<>();
            ct.getItems().addAll(user.userPlaylists);
            ct.setComboBoxEditable(true);

            return ct;
        });
        /*

        colCob.setCellFactory(ComboBoxTableCell.forTableColumn(playlists));

        colCob.setOnEditCommit((TableColumn.CellEditEvent<Music, Playlist> event) -> {
            TablePosition<Music, Playlist> pos = event.getTablePosition();

            int row = pos.getRow();

        });*/

       // colCob.setMinWidth(120);

        table.setEditable(true);
        table.getColumns().addAll(songs, releases, artists, colCob, colBtn);

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
            //SongPlayer.playSong(song.getSong().getId() + ".mp3");
        });

        playButton.setOnAction(action -> {
            try {
                var song = table.getSelectionModel().getSelectedItem();
                stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
                SongPlayer.playSong(song.getSong().getId() + ".mp3");
            } catch(NullPointerException e) { // Catch for case when there's no selected item
                var song = table.getItems().get(songIndex);
                stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
                SongPlayer.playSong(song.getSong().getId() + ".mp3");
            }
        });

        nextSongButton.setOnAction(action -> {
            int currentIndex = table.getSelectionModel().getSelectedIndex();
            int nextIndex = currentIndex + 1;
            var song = table.getItems().get(nextIndex);
            stage.setTitle("Music Player 1.0" + " - Now Playing: " + song.getSong().getTitle());
            //SongPlayer.playSong(song.getSong().getId());
        });

        var controlButtonRow = new BorderPane();
        controlButtonRow.setLeft(prevSongButton);
        controlButtonRow.setCenter(playButton);
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

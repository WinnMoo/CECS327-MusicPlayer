package com.cecs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

public class MyPlaylistPage {
    static void show(Stage stage, User user){

        ObservableList<Song> songs = FXCollections.observableArrayList();
        songs.addAll(user.getUserPlaylists().get(0).getSongs());

        //Columns
        TableColumn<Song, String> titleCol = new TableColumn<>("Title");
        titleCol.setMinWidth(200);
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Song, String> artistCol = new TableColumn<>("Artist");
        artistCol.setMinWidth(200);
        artistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));

        TableView<Song> table = new TableView<>(songs);

        table.setEditable(true);
        table.getColumns().addAll(titleCol, artistCol);

        final var label = new Text("Playlists of " + user.username);
        var btMainPage = new Button("Search songs");
        btMainPage.setOnAction(e -> MainPage.show(stage, user));
        label.setFont(Font.font(null, FontPosture.ITALIC, 24.0));
        var line = new HBox(label, btMainPage);
        line.setSpacing(30.0);

        List<String> pls = new ArrayList<>();
        Map<String, Playlist> map = new HashMap<>();

        for(Playlist pl: user.getUserPlaylists()){
            pls.add(pl.getName());
            map.put(pl.getName(), pl);
        }

        ComboBox cbMyPlaylist = new ComboBox<>();
        cbMyPlaylist.getItems().addAll(pls);

        cbMyPlaylist.setValue((pls.isEmpty()) ? "" : pls.get(0));
        cbMyPlaylist.setOnAction(e -> {
            for(Playlist pl : user.getUserPlaylists()){
                if(pl.getName().equals(cbMyPlaylist.getValue())){
                    songs.setAll(pl.getSongs());
                }
            }
            table.setItems(songs);
        });
        final var col = new VBox(line, cbMyPlaylist, table);
        col.setSpacing(10.0);
        col.setPadding(new Insets(25.0));
        final var scene = new Scene(col, 800, 600);
        stage.setScene(scene);
        stage.show();

    }
}

package com.cecs;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PlaylistPage {




    //Load files prior to application running
    public void init () throws Exception {
        System.out.println("Before application");
    }

    //show application visible to user
    public static void show (Stage stage, User user){
        BorderPane root = new BorderPane();
        stage.setTitle("Stage Title");


        //VBox root = new VBox();
        //var button1 = new Button("Sign In");


        Menu MainMenu = new Menu("All Songs");
        Menu PlaylistMenu = new Menu("Playlists");
        Menu ProfileMenu = new Menu("User Profile");
        Menu SettingsMenu = new Menu("Settings");

        Slider slider = new Slider(0, 100, 50);
        CustomMenuItem customMenuItem = new CustomMenuItem();
        customMenuItem.setContent(slider);
        customMenuItem.setHideOnClick(false);
        SettingsMenu.getItems().add(customMenuItem);

        Button button = new Button("Custom Menu Item Button");
        CustomMenuItem customMenuItem2 = new CustomMenuItem();
        customMenuItem2.setContent(button);
        customMenuItem2.setHideOnClick(false);
        SettingsMenu.getItems().add(customMenuItem2);

        Button mainpageButton = new Button("View All");
        CustomMenuItem customMenuItem_Main = new CustomMenuItem();
        customMenuItem_Main.setContent(mainpageButton);


        customMenuItem_Main.setHideOnClick(false);


        MainMenu.getItems().add(customMenuItem_Main);


        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(MainMenu);
        menuBar.getMenus().add(PlaylistMenu);
        menuBar.getMenus().add(ProfileMenu);
        menuBar.getMenus().add(SettingsMenu);

        menuBar.prefWidthProperty().bind(stage.widthProperty());
        root.setTop(menuBar);

        //initialize variable
        var musics = new Music[0];
        //try catch to retrieve music from json file
        try {
            var reader = new InputStreamReader(MainPage.class.getResourceAsStream("/music2.json"), StandardCharsets.UTF_8);
            musics = new GsonBuilder().create().fromJson(reader, Music[].class);
        } catch (NullPointerException e) {
            System.err.println("Instantiating input stream failed.");
        } catch (JsonSyntaxException | JsonIOException e) {
            System.err.println("Could not populate music list.");
        }

        //use FX to display array of music
        var listOfMusic = FXCollections.observableArrayList(musics);

        //Display text above music
        final var label = new Text("Playlist 1");
        label.setFont(Font.font(null, FontPosture.ITALIC, 24.0));

        //Song Array UI
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
                return p.getArtist().toString().toLowerCase().contains(query) ||
                        p.getRelease().toString().toLowerCase().contains(query) ||
                        p.getSong().toString().toLowerCase().contains(query);
            });
        });

        final var col = new VBox(menuBar, label, searchBar, table);
        col.setSpacing(10.0);
        col.setPadding(new Insets(10.0));
        final var scene = new Scene(col, 800, 600);



        stage.setScene(scene);
        stage.show();
    }


}
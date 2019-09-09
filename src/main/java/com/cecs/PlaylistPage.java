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

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PlaylistPage {




    //Load files prior to application running
        public void init () throws Exception {
            System.out.println("Before application");
        }

        //show application visible to user
        public static void show (Stage stage){
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
            mainpageButton.setOnAction(action -> {
                var u = 'f';
                var p = 'f';
                MainPage.show(stage, new User(u, p));
            });


            customMenuItem_Main.setHideOnClick(false);


            MainMenu.getItems().add(customMenuItem_Main);


            MenuBar menuBar = new MenuBar();
            menuBar.getMenus().add(MainMenu);
            menuBar.getMenus().add(PlaylistMenu);
            menuBar.getMenus().add(ProfileMenu);
            menuBar.getMenus().add(SettingsMenu);

            menuBar.prefWidthProperty().bind(stage.widthProperty());
            root.setTop(menuBar);


            final var col = new VBox(menuBar);
            col.setSpacing(10.0);
            col.setPadding(new Insets(25.0));
            final var scene = new Scene(col, 800, 600);


            stage.setScene(scene);
            stage.show();
        }


}
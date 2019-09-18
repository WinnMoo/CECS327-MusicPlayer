package com.cecs;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import io.reactivex.Flowable;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

class PlaylistPage {
    static void show(Stage parentStage, User user) {
        //show application visible to user
        var stage = new Stage();
        var signUp = new Text("Add Playlist");
        signUp.setFont(new Font(null, 36.0));

        // Playlist entry
        var playlistLabel = new Label("Playlist Name");
        var playlistField = new TextField();

        // Organize and apply layout to form
        var entry = new HBox(playlistLabel, playlistField);
        entry.setSpacing(20.0);
        entry.setAlignment(Pos.CENTER);
        entry.maxWidth(225.0);

        // Appears beside the register button whenever an error occurs during
        // registration
        var errorMessage = new Label("");
        errorMessage.setTextFill(Color.color(1.0, 0.2, 0.2));

        // Button used to confirm adding a new entry to the user list
        // Its action method will catch any exceptions raised by JsonService
        var addButton = new Button("Add Playlist");
        addButton.setOnAction(action -> {
            var name = playlistField.getText();
            if (!name.isBlank()) {
                user.userPlaylists.add(new Playlist(name));
                stage.close();
            } else {
                errorMessage.setText("Cannot leave field empty!");
            }
        });
        playlistField.setOnKeyReleased(actionEvent -> {
            if (actionEvent.getCode() == KeyCode.ENTER) {
                addButton.fire();
            }
        });

        // Organize and apply layout to error message and button
        var buttonRow = new BorderPane();
        buttonRow.setLeft(errorMessage);
        buttonRow.setRight(addButton);
        buttonRow.setMaxWidth(250.0);

        // Align everything in a column
        var col = new VBox(signUp, entry, buttonRow);
        col.setSpacing(10.0);
        col.setAlignment(Pos.CENTER);
        col.setPadding(new Insets(25.0));

        // Show this window on top of login page and prevent activity there until this
        // window is gone
        stage.setScene(new Scene(col, 300, 150));
        stage.initOwner(parentStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
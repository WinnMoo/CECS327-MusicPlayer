package com.cecs;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

class MainPage {
    static void show(Stage stage) {
        var col = new VBox(new Label("Welcome"));
        var scene = new Scene(col);

        stage.setScene(scene);
        stage.show();
    }
}

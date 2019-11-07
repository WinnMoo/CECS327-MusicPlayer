package com.cecs.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static com.cecs.App.comm;

public class ConfigureConnectionPage {
    static void showAndWait(Stage parentStage) {
        var stage = new Stage();

        // TODO: Insert code here
        // comm.remoteRef.setPort(3000);

        // Align everything in a column
        var col = new VBox();
        col.setSpacing(10.0);
        col.setAlignment(Pos.CENTER);
        col.setPadding(new Insets(25.0));

        // Show this window on top of login page and prevent activity there until this
        // window is gone
        stage.setScene(new Scene(col, 500, 400));
        stage.initOwner(parentStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}

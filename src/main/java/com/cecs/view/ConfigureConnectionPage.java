package com.cecs.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static com.cecs.App.comm;

public class ConfigureConnectionPage {
    static void showAndWait(Stage parentStage) {
        var stage = new Stage();

        // TODO: Insert code here
        var portLabel = new Label("Port number");
        var portField = new TextField(String.valueOf(comm.remoteRef.getPort()));
        var portRow = new HBox(portLabel, portField);
        portRow.setAlignment(Pos.CENTER);
        portRow.setSpacing(10.0);

        var btnApply = new Button("Apply");
        var btnOk = new Button("Ok");
        var btnCancel = new Button("Cancel");

        btnApply.setOnAction(actionEvent -> {
            var res = modifyConnection(portField.getText());
            if (!res) {
                alert();
            }
        });

        btnCancel.setOnAction(actionEvent -> stage.close());

        btnOk.setOnAction(actionEvent -> {
            var res = modifyConnection(portField.getText());
            if (res) {
                stage.close();
            } else {
                alert();
            }
        });

        var btnRow = new HBox(btnOk, btnCancel, btnApply);
        btnRow.setAlignment(Pos.BOTTOM_RIGHT);
        btnRow.setSpacing(10.0);

        // Align everything in a column
        var pane = new BorderPane();
        pane.setCenter(portRow);
        pane.setBottom(btnRow);
        pane.setPadding(new Insets(25.0));

        // Show this window on top of login page and prevent activity there until this
        // window is gone
        stage.setScene(new Scene(pane, 500, 400));
        stage.initOwner(parentStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private static boolean modifyConnection(String text) {
        try {
            var portNumber = (int)Short.parseShort(text);
            if (portNumber == 0) {
                return false;
            }
            comm.remoteRef.setPort(portNumber);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void alert() {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Could not apply port number, please enter a valid number between 1 and 65535");
        alert.showAndWait();
    }
}

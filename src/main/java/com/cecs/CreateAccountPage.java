package com.cecs;

import io.reactivex.Flowable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

class CreateAccountPage {
    static void showAndWait(Stage parentStage) {
        var signUp = new Text("Sign Up");
        signUp.setFont(new Font(null, 36.0));

        var userLabel = new Label("Username");
        var passLabel = new Label("Password");
        var rePassLabel = new Label("Retype Password");
        var labels = new VBox(userLabel, passLabel, rePassLabel);
        labels.setSpacing(15.0);
        labels.setAlignment(Pos.CENTER);

        var userField = new TextField();
        var passField = new PasswordField();
        var rePassField = new PasswordField();
        var fields = new VBox(userField, passField, rePassField);
        fields.setSpacing(15.0);
        fields.setAlignment(Pos.CENTER);

        var entries = new HBox(labels, fields);
        entries.setSpacing(20.0);
        entries.setAlignment(Pos.CENTER);
        entries.maxWidth(225.0);

        var registerButton = new Button("Register");
        registerButton.setOnAction(action -> {
            var u = userField.getText();
            var p1 = passField.getText();
            var p2 = passField.getText();
            Flowable.fromCallable(() -> register(u, p1, p2)).subscribe(code -> {
                // TODO: After call is successful, do stuff
            });
        });

        var col = new VBox(signUp, entries, registerButton);
        col.setSpacing(10.0);
        col.setAlignment(Pos.CENTER);
        col.setPadding(new Insets(25.0));

        var stage = new Stage();
        stage.setScene(new Scene(col, 320, 400));
        stage.initOwner(parentStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private static boolean register(String user, String pass1, String pass2) {
        return true;
    }
}

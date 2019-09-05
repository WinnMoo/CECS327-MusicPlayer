package com.cecs;

import java.io.IOException;

import io.reactivex.Flowable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

class CreateAccountPage {
    enum RegisterCode {
        SUCCESS, INVALID_USER, INVALID_PASS1, INVALID_PASS2, NO_MATCH
    }

    static void showAndWait(Stage parentStage) {
        var stage = new Stage();
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

        var errorMessage = new Label("");
        errorMessage.setTextFill(Color.color(1.0, 0.2, 0.2));

        var registerButton = new Button("Register");
        registerButton.setOnAction(action -> {
            var u = userField.getText();
            var p1 = passField.getText();
            var p2 = rePassField.getText();
            Flowable.fromCallable(() -> register(u, p1, p2)).subscribe(code -> {
                switch (code) {
                case SUCCESS: {
                    var alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText(null);
                    alert.setContentText("Registration successful!");
                    alert.showAndWait();

                    stage.close();
                    break;
                }
                case INVALID_USER: {
                    errorMessage.setText("Username cannot be blank");
                    break;
                }
                case INVALID_PASS1: {
                    errorMessage.setText("Password cannot be blank");
                    break;
                }
                case INVALID_PASS2: {
                    errorMessage.setText("Retype Password cannot be blank");
                    break;
                }
                case NO_MATCH: {
                    errorMessage.setText("Passwords do not match");
                    break;
                }
                }
            }, Throwable::printStackTrace);
        });

        var createAccRow = new HBox(errorMessage, registerButton);
        createAccRow.setAlignment(Pos.CENTER);

        var col = new VBox(signUp, entries, createAccRow);
        col.setSpacing(10.0);
        col.setAlignment(Pos.CENTER);
        col.setPadding(new Insets(25.0));

        stage.setScene(new Scene(col, 320, 400));
        stage.initOwner(parentStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    /**
     * Registers a user by inserting new data into a JSON file.
     * @return `true` if neither the fields are blank and the password fields match,
     *         `false` otherwise
     * @throws IOException if the JsonService input stream fails, and other errors otherwise.
     */
    private static RegisterCode register(String user, String pass1, String pass2) throws IOException {
        if (user.isBlank()) {
            return RegisterCode.INVALID_USER;
        }
        if (pass1.isBlank()) {
            return RegisterCode.INVALID_PASS1;
        }
        if (pass2.isBlank()) {
            return RegisterCode.INVALID_PASS2;
        }
        if (!pass1.equals(pass2)) {
            return RegisterCode.NO_MATCH;
        }
        JsonService.createAccount(user, pass1);
        return RegisterCode.SUCCESS;
    }
}

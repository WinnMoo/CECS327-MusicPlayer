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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

class CreateAccountPage {
    enum RegisterCode {
        SUCCESS, INVALID_USER, INVALID_PASS1, INVALID_PASS2, NO_MATCH, NAME_TAKEN
    }

    static void showAndWait(Stage parentStage) {
        var stage = new Stage();
        var signUp = new Text("Sign Up");
        signUp.setFont(new Font(null, 36.0));

        // Form labels
        var userLabel = new Label("Username");
        var passLabel = new Label("Password");
        var rePassLabel = new Label("Retype Password");
        var labels = new VBox(userLabel, passLabel, rePassLabel);
        labels.setSpacing(15.0);
        labels.setAlignment(Pos.CENTER);

        // Form textfields
        var userField = new TextField();
        var passField = new PasswordField();
        var rePassField = new PasswordField();
        var fields = new VBox(userField, passField, rePassField);
        fields.setSpacing(15.0);
        fields.setAlignment(Pos.CENTER);

        // Organize and apply layout to form
        var entries = new HBox(labels, fields);
        entries.setSpacing(20.0);
        entries.setAlignment(Pos.CENTER);
        entries.maxWidth(225.0);

        // Appears beside the register button whenever an error occurs during
        // registration
        var errorMessage = new Label("");
        errorMessage.setTextFill(Color.color(1.0, 0.2, 0.2));

        // Button used to confirm adding a new entry to the user list
        // Its action method will catch any exceptions raised by JsonService
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
                case NAME_TAKEN: {
                    errorMessage.setText("User with this name already exists");
                    break;
                }
                }
            }, Throwable::printStackTrace);
        });
        userField.setOnKeyReleased(actionEvent -> {
            if (actionEvent.getCode() == KeyCode.ENTER) {
                registerButton.fire();
            }
        });
        passField.setOnKeyReleased(actionEvent -> {
            if (actionEvent.getCode() == KeyCode.ENTER) {
                registerButton.fire();
            }
        });
        rePassField.setOnKeyReleased(actionEvent -> {
            if (actionEvent.getCode() == KeyCode.ENTER) {
                registerButton.fire();
            }
        });

        // Organize and apply layout to error message and button
        var buttonRow = new BorderPane();
        buttonRow.setLeft(errorMessage);
        buttonRow.setRight(registerButton);
        buttonRow.setMaxWidth(250.0);

        // Align everything in a column
        var col = new VBox(signUp, entries, buttonRow);
        col.setSpacing(10.0);
        col.setAlignment(Pos.CENTER);
        col.setPadding(new Insets(25.0));

        // Show this window on top of login page and prevent activity there until this
        // window is gone
        stage.setScene(new Scene(col, 320, 400));
        stage.initOwner(parentStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    /**
     * Registers a user by inserting new data into a JSON file.
     * 
     * @param name  Username of person
     * @param pass1 Password of person
     * @param pass2 Second password which should match <code>pass1</code>
     * 
     * @return The <code>RegisterCode</code> describing whether any variable passed
     *         in is blank or whether the passwords match or not
     * 
     * @throws IOException if the <code>JsonService</code> input stream fails, and
     *                     other errors thrown by <code>JsonService</code>
     *                     otherwise.
     */
    private static RegisterCode register(String name, String pass1, String pass2) throws IOException {
        if (name.isBlank()) {
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
        if (!JsonService.createAccount(name, pass1)) {
            return RegisterCode.NAME_TAKEN;
        }
        return RegisterCode.SUCCESS;
    }
}

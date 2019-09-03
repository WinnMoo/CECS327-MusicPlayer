package com.cecs;

import io.reactivex.Flowable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

class LoginPage {
    enum LoginCode {
        SUCCESS, INVALID_USER, INVALID_PASS,
    }

    static void show(Stage stage) {
        var userLabel = new Label("Username");
        var passLabel = new Label("Password");
        var labels = new VBox(userLabel, passLabel);
        labels.setSpacing(15.0);
        labels.setAlignment(Pos.CENTER);

        var userField = new TextField();
        var passField = new PasswordField();
        var fields = new VBox(userField, passField);
        fields.setSpacing(15.0);
        fields.setAlignment(Pos.CENTER);

        var entries = new HBox(labels, fields);
        entries.setSpacing(20.0);
        entries.setAlignment(Pos.CENTER);
        entries.maxWidth(225.0);

        var signIn = new Text("Sign In");
        signIn.setFont(new Font(null, 36.0));

        var button = new Button("Sign In");
        var errorMessage = new Label("");
        errorMessage.setTextFill(Color.color(1.0, 0.2, 0.2));

        var createAcc = new Text("Create Account");
        createAcc.setFont(new Font(null, 36.0));

        var createAccButton = new Button("Create Account");

        createAccButton.setOnAction(action -> {
            CreateAccountPage.show(stage);
        });

        button.setOnAction(action -> {
            var u = userField.getText();
            var p = passField.getText();
            Flowable.fromCallable(() -> authenticate(u, p)).subscribe(code -> {
                switch (code) {
                case SUCCESS: {
                    MainPage.show(stage, new User(u, p));
                    break;
                }
                case INVALID_USER: {
                    errorMessage.setText("Username cannot be blank");
                    break;
                }
                case INVALID_PASS: {
                    errorMessage.setText("Password cannot be blank");
                }
                }
            });
        });
        userField.setOnKeyReleased(actionEvent -> {
            if (actionEvent.getCode() == KeyCode.ENTER) {
                button.fire();
            }
        });
        passField.setOnKeyReleased(actionEvent -> {
            if (actionEvent.getCode() == KeyCode.ENTER) {
                button.fire();
            }
        });

        var buttonRow = new BorderPane();
        buttonRow.setLeft(createAccButton);
        buttonRow.setCenter(errorMessage);
        buttonRow.setRight(button);
        buttonRow.setMaxWidth(400.0);

        var col = new VBox(signIn, entries, buttonRow);
        col.setSpacing(10.0);
        col.setAlignment(Pos.CENTER);
        col.setPadding(new Insets(25.0));

        stage.setScene(new Scene(col, 800, 600));
        stage.setTitle("Music Player 1.0");
        stage.show();
    }

    private static LoginCode authenticate(String user, String pass) {
        if (user.isBlank())
            return LoginCode.INVALID_USER;
        if (pass.isBlank())
            return LoginCode.INVALID_PASS;
        return LoginCode.SUCCESS;
    }
}

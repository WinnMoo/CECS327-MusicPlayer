package com.cecs.view;

import com.cecs.App;
import com.cecs.controller.Communication;
import com.cecs.controller.JsonService;
import com.cecs.controller.Proxy;
import com.cecs.controller.SongPlayer;
import com.cecs.def.ProxyInterface;
import com.cecs.model.User;

import io.reactivex.Flowable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

public class LoginPage {
    enum LoginCode {
        SUCCESS, INVALID_USER, INVALID_PASS, INCORRECT_CREDENTIALS,
    }

    private static ProxyInterface proxy = new Proxy(App.comm, "UserServices", Communication.Semantic.AT_LEAST_ONCE);

    public static void show(Stage stage) {
        var signIn = new Text("Sign In");
        signIn.setFont(new Font(null, 36.0));

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

        var loginButton = new Button("Sign In");
        var errorMessage = new Label("");
        errorMessage.setTextFill(Color.color(1.0, 0.2, 0.2));

        var createAccText = new Text("Need an account? ");
        var createAccLink = new Hyperlink("Sign Up");
        var createAccRow = new HBox(createAccText, createAccLink);
        createAccRow.setAlignment(Pos.CENTER);

        createAccLink.setOnAction(action -> CreateAccountPage.showAndWait(stage));

        loginButton.setOnAction(action -> Flowable
                .fromCallable(() -> authenticate(userField.getText(), passField.getText())).subscribe(pair -> {
                    switch (pair.getKey()) {
                    case SUCCESS: {
                        MainPage.show(stage, new SongPlayer(), pair.getValue());
                        break;
                    }
                    case INVALID_USER: {
                        errorMessage.setText("Username cannot be blank");
                        break;
                    }
                    case INVALID_PASS: {
                        errorMessage.setText("Password cannot be blank");
                        break;
                    }
                    case INCORRECT_CREDENTIALS: {
                        errorMessage.setText("Incorrect username or password");
                        break;
                    }
                    }
                }, error -> {
                    System.err.println("An error has occurred.\n");
                    error.printStackTrace();
                }));
        userField.setOnKeyReleased(actionEvent -> {
            if (actionEvent.getCode() == KeyCode.ENTER) {
                loginButton.fire();
            }
        });
        passField.setOnKeyReleased(actionEvent -> {
            if (actionEvent.getCode() == KeyCode.ENTER) {
                loginButton.fire();
            }
        });

        var buttonRow = new BorderPane();
        buttonRow.setLeft(errorMessage);
        buttonRow.setRight(loginButton);
        buttonRow.setMaxWidth(250.0);

        var col = new VBox(signIn, entries, buttonRow, createAccRow);
        col.setSpacing(10.0);
        col.setAlignment(Pos.CENTER);
        col.setPadding(new Insets(25.0));

        stage.setScene(new Scene(col, 800, 600));
        stage.setTitle("Music Player 1.0");
        stage.show();
    }

    private static Pair<LoginCode, User> authenticate(String name, String pass) {
        User user = null;
        var code = LoginCode.SUCCESS;
        if (name.isBlank()) {
            code = LoginCode.INVALID_USER;
        } else if (pass.isBlank()) {
            code = LoginCode.INVALID_PASS;
        } else {
            var request = proxy.synchExecution("login", new String[] { name, pass });
            user = JsonService.unpackUser(request);
            if (user == null) {
                code = LoginCode.INCORRECT_CREDENTIALS;
            }
        }
        return new Pair<>(code, user);
    }
}
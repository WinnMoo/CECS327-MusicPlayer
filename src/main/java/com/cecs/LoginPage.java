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
import javafx.stage.Stage;

class LoginPage {
    enum LoginCode {
        SUCCESS, INVALID_USER, INVALID_PASS,
    }

    static void show(Stage stage) {
        var userLabel = new Label("Username");
        var userField = new TextField();
        var user = new HBox(userLabel, userField);
        user.setSpacing(20.0);
        user.setAlignment(Pos.CENTER);

        var passLabel = new Label("Password");
        var passField = new PasswordField();
        var pass = new HBox(passLabel, passField);
        pass.setSpacing(20.0);
        pass.setAlignment(Pos.CENTER);

        var entries = new VBox(user, pass);
        entries.setSpacing(15.0);
        entries.setAlignment(Pos.CENTER);

        var signIn = new Text("Sign In");
        signIn.setFont(new Font(null, 36.0));

        var button = new Button("Sign In");
        button.setOnAction(action -> {
            var u = userField.getText();
            var p = passField.getText();
            Flowable.fromCallable(() -> authenticate(u, p)).subscribe(code -> {
                switch (code) {
                case SUCCESS: {
                    MainPage.show(new Stage());
                    stage.close();
                    break;
                }
                case INVALID_USER: {
                    break;
                    /* TODO: Inform user when name is incorrect */
                }
                case INVALID_PASS: {
                    /* TODO: Inform user when pass is incorrect */
                }
                }
            });
        });

        var col = new VBox(signIn, entries, button);
        col.setSpacing(30.0);
        col.setAlignment(Pos.CENTER);
        col.setPadding(new Insets(25.0));

        stage.setScene(new Scene(col));
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

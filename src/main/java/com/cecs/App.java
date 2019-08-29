package com.cecs;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        LoginPage.show(stage);
    }

    public static void main(String[] args) {
        launch();
    }

}
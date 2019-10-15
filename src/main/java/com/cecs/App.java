package com.cecs;

import com.cecs.controller.Communication;
import com.cecs.view.LoginPage;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {
    public static Communication comm = new Communication();

    @Override
    public void start(Stage stage) {
        LoginPage.show(stage);
    }

    public static void main(String[] args) {
        launch();
    }

}
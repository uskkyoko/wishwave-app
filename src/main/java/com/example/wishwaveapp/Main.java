package com.example.wishwaveapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            Font.loadFont(getClass().getResource("PressStart2P-Regular.ttf").toExternalForm(), 10);
            Font.loadFont(getClass().getResource("Inconsolata-VariableFont_wdth,wght.ttf").toExternalForm(), 10);
            Font.loadFont(getClass().getResource("GildaDisplay-Regular.ttf").toExternalForm(), 10);

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("index.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1440, 744);

            Object controller = fxmlLoader.getController();
            if (controller instanceof WishlistController) {
                WishlistController wishlistController = (WishlistController) controller;
                wishlistController.setStage(stage);
                wishlistController.initializeData();
            }

            stage.setTitle("WishWave");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
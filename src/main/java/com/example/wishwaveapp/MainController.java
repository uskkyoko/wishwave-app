package com.example.wishwaveapp;

import com.example.wishwaveapp.util.AnimationUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


public class MainController implements Navigator {

    @FXML
    private Label logoLabel;
    @FXML
    private Button goToWishlists;
    @FXML
    private ImageView gift1;
    @FXML
    private ImageView gift2;

    @FXML
    public void initialize() {
        logoLabel.setOnMouseClicked(event -> {
            navigateToPage("index.fxml", "style.css");
        });

        goToWishlists.setOnAction(event -> {
            navigateToPage("wishlist.fxml", "wishlist.css");
        });

        AnimationUtils.setupFloatingAnimation(gift1, 4000, 0.5);
        AnimationUtils.setupFloatingAnimation(gift2, 4000, 1.0);
    }

    @Override
    public void navigateToPage(String fxmlFile, String styleSheet) {
        try {
            Stage stage = (Stage) goToWishlists.getScene().getWindow();

            URL fxmlUrl = getClass().getResource(fxmlFile);
            if (fxmlUrl == null) {
                throw new IOException("Cannot find " + fxmlFile);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof WishlistController) {
                WishlistController wishlistController = (WishlistController) controller;
                wishlistController.setStage(stage);
                wishlistController.initializeData();
            }

            Scene scene = new Scene(root);

            URL cssUrl = getClass().getResource(styleSheet);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            stage.setScene(scene);

        } catch (IOException e) {
            System.err.println("Error navigating to " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
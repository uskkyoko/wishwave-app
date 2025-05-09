package com.example.wishwaveapp.util;

import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class AnimationUtils {

    public static void setupFloatingAnimation(ImageView image, int durationMillis, double delaySeconds) {

        TranslateTransition upTransition = new TranslateTransition(Duration.millis(durationMillis / 2), image);
        upTransition.setByY(-10);
        upTransition.setDelay(Duration.seconds(delaySeconds));


        TranslateTransition downTransition = new TranslateTransition(Duration.millis(durationMillis / 2), image);
        downTransition.setByY(10);

        upTransition.setOnFinished(event -> downTransition.play());
        downTransition.setOnFinished(event -> upTransition.play());

        upTransition.play();
    }
}


module com.example.wishwaveapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.wishwaveapp to javafx.fxml;
    exports com.example.wishwaveapp;
    exports com.example.wishwaveapp.model;
    opens com.example.wishwaveapp.model to javafx.fxml;
    exports com.example.wishwaveapp.util;
    opens com.example.wishwaveapp.util to javafx.fxml;
}
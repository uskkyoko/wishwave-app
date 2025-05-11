package com.example.wishwaveapp;

import com.example.wishwaveapp.model.User;
import com.example.wishwaveapp.model.Wish;
import com.example.wishwaveapp.model.Wishlist;
import com.example.wishwaveapp.util.FileManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class WishlistController implements Navigator {

    @FXML private Label logoLabel;
    @FXML private ImageView wishlistIcon;
    @FXML private ImageView user;
    @FXML private ImageView settings;
    @FXML private Button goToWishlists;
    @FXML private Pane profilePic;
    @FXML private Label username;
    @FXML private Label bio;
    @FXML private HBox categories;
    @FXML private GridPane wishlistGrid;

    private User currentUser;
    private Wishlist currentWishlist;
    private Stage primaryStage;
    private final String USER_DATA_FILE = "user_data.dat";

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    public void initialize() {
        setupEventHandlers();
    }

    public void initializeData() {
        loadUserData();

        updateUserDisplay();

        updateCategoryBar();

        if (!currentUser.getWishlists().isEmpty()) {
            setCurrentWishlist(currentUser.getWishlists().get(0));
        }
    }

    private void setupEventHandlers() {
        wishlistIcon.setOnMouseClicked(event -> showAddWishDialog());
        user.setOnMouseClicked(event -> showUserSettingsDialog());
        settings.setOnMouseClicked(event -> showWishlistSettingsDialog());
        logoLabel.setOnMouseClicked(event -> navigateToPage("index.fxml", "style.css"));
        goToWishlists.setOnAction(event -> navigateToWishlists());
    }

    private void loadUserData() {
        File dataFile = new File(USER_DATA_FILE);
        if (dataFile.exists()) {
            currentUser = FileManager.loadUserData(USER_DATA_FILE);
        } else {
            currentUser = new User("user1234", "No bio yet", null);

            /// SAMPLES FOR QUICK HOMEWORK
            Wishlist techWishlist = new Wishlist("Tech Gadgets");
            techWishlist.addWish(new Wish("MacBook Pro", "16-inch, M1 Pro, Space Gray", 2399.99, "https://apple.com", null));
            techWishlist.addWish(new Wish("AirPods Pro", "Active Noise Cancellation", 249.99, "https://apple.com", null));

            Wishlist booksWishlist = new Wishlist("Books");
            booksWishlist.addWish(new Wish("Dune", "Frank Herbert's sci-fi classic", 15.99, "https://amazon.com", null));

            currentUser.addWishlist(techWishlist);
            currentUser.addWishlist(booksWishlist);

            FileManager.saveUserData(currentUser, USER_DATA_FILE);
        }
    }

    private void updateUserDisplay() {
        username.setText(currentUser.getUsername());
        bio.setText(currentUser.getBio());

        if (currentUser.getProfileImagePath() != null && !currentUser.getProfileImagePath().isEmpty()) {
            try {
                BackgroundImage bgImage = new BackgroundImage(
                        new Image(currentUser.getProfileImagePath()),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(100, 100, true, true, true, false)
                );
                profilePic.setBackground(new Background(bgImage));
            } catch (Exception e) {
                System.err.println("Error loading profile image: " + e.getMessage());
            }
        }
    }

    private void updateCategoryBar() {
        categories.getChildren().clear();

        for (Wishlist wishlist : currentUser.getWishlists()) {
            Button categoryBtn = new Button(wishlist.getName());
            categoryBtn.getStyleClass().add("category");

            categoryBtn.setOnAction(event -> setCurrentWishlist(wishlist));

            categories.getChildren().add(categoryBtn);
        }

        ComboBox<String> sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll("Price (Low to High)", "Price (High to Low)", "Name (A-Z)", "Date Added (Newest)");
        sortComboBox.setValue("Sort By");
        sortComboBox.setPromptText("Sort By");
        sortComboBox.setStyle("-fx-background-color: #51A3A3; -fx-text-fill: #0D0C1D; -fx-font-size: 24px; -fx-margin: 40;");
        sortComboBox.setOnMouseEntered(e -> sortComboBox.setCursor(Cursor.HAND));
        sortComboBox.setOnMouseExited(e -> sortComboBox.setCursor(Cursor.DEFAULT));

        sortComboBox.setOnAction(event -> {
            if (currentWishlist != null) {
                String selectedSort = sortComboBox.getValue();
                switch (selectedSort) {
                    case "Price (Low to High)":
                        currentWishlist.sortByPriceAscending();
                        break;
                    case "Price (High to Low)":
                        currentWishlist.sortByPriceDescending();
                        break;
                    case "Name (A-Z)":
                        currentWishlist.sortByName();
                        break;
                    case "Date Added (Newest)":
                        currentWishlist.sortByDateAdded();
                        break;
                }

                displayWishlistItems();
            }
        });

        HBox sortContainer = new HBox(sortComboBox);
        sortContainer.setAlignment(Pos.CENTER);
        sortContainer.setPadding(new Insets(0, 10, 0, 10));

        categories.getChildren().add(sortContainer);
    }

    private void setCurrentWishlist(Wishlist wishlist) {
        this.currentWishlist = wishlist;
        displayWishlistItems();
    }

    private void displayWishlistItems() {
        wishlistGrid.getChildren().clear();

        if (currentWishlist == null) return;

        URL cssUrl = getClass().getResource("wishlist.css");
        if (cssUrl != null) {
            wishlistGrid.getStylesheets().add(cssUrl.toExternalForm());
        }

        int col = 0;
        int row = 0;

        for (Wish wish : currentWishlist.getWishes()) {
            VBox itemBox = new VBox(5);
            itemBox.getStyleClass().add("wishlist-item");

            ImageView imageView = new ImageView();
            imageView.setFitHeight(250);
            imageView.setFitWidth(310);
            imageView.setPreserveRatio(true);

            if (wish.getImage() != null) {
                try {
                    imageView.setImage(wish.getImage());
                    imageView.getStyleClass().add("wish-image");
                } catch (Exception e) {
                    System.err.println("Error displaying wish image: " + e.getMessage());
                    e.printStackTrace();
                    imageView.setStyle("-fx-background-color: #f0f0f0;");
                }
            } else {
                imageView.setStyle("-fx-background-color: #f0f0f0;");
            }

            Button editBtn = new Button("⋮");
            editBtn.setStyle("-fx-background-color: #DD7373; -fx-text-fill: #0D0C1D; -fx-font-size: 16px;");
            editBtn.getStyleClass().add("small-button");
            editBtn.setOnAction(event -> {
                Bounds boundsInScreen = editBtn.localToScreen(editBtn.getBoundsInLocal());
                double x = boundsInScreen.getMinX();
                double y = boundsInScreen.getMinY();
                showEditWishDialog(wish, x, y);
            });

            VBox detailsBox = new VBox();
            detailsBox.getStyleClass().add("item-details");

            Label nameLabel = new Label(wish.getName());
            nameLabel.getStyleClass().add("item-title");

            Label descLabel = new Label(wish.getDescription());
            descLabel.getStyleClass().add("item-description");
            descLabel.setWrapText(true);

            HBox buttonsBox = new HBox();
            buttonsBox.getStyleClass().add("item-buttons");

            Button priceBtn = new Button("$" + String.format("%.2f", wish.getPrice()));
            priceBtn.getStyleClass().add("price-btn");

            Button linkBtn = new Button("Website");
            linkBtn.getStyleClass().add("link-btn");
            linkBtn.setOnAction(event -> {
                if (wish.getLink() != null && !wish.getLink().isEmpty()) {
                    try {
                        java.awt.Desktop.getDesktop().browse(new java.net.URI(wish.getLink()));
                    } catch (Exception e) {
                        showErrorDialog("Could not open link: " + e.getMessage());
                    }
                }
            });

            buttonsBox.getChildren().addAll(priceBtn, linkBtn);

            detailsBox.getChildren().addAll(nameLabel, descLabel);

            HBox editBox = new HBox(editBtn);
            editBox.setAlignment(Pos.TOP_RIGHT);

            itemBox.getChildren().addAll(editBox, imageView, detailsBox, buttonsBox);

            wishlistGrid.add(itemBox, col, row);

            col++;
            if (col > 3) {
                col = 0;
                row++;
            }
        }
    }

    private void showEditWishDialog(Wish wish, double screenX, double screenY) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("Edit Wish");
        editItem.setOnAction(e -> editWish(wish));

        MenuItem deleteItem = new MenuItem("Delete Wish");
        deleteItem.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Wish");
            alert.setHeaderText("Delete " + wish.getName());
            alert.setContentText("Are you sure you want to delete this wish?");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("wishlist.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-alert");

            ((Button) dialogPane.lookupButton(ButtonType.OK)).getStyleClass().add("dialog-button");
            ((Button) dialogPane.lookupButton(ButtonType.CANCEL)).getStyleClass().add("dialog-button");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                currentWishlist.removeWish(wish);
                FileManager.saveUserData(currentUser, USER_DATA_FILE);
                displayWishlistItems();
            }
        });

        contextMenu.getItems().addAll(editItem, deleteItem);
        contextMenu.show(primaryStage, screenX, screenY);
    }


    private void editWish(Wish wish) {
        Dialog<Wish> dialog = new Dialog<>();
        dialog.setTitle("Edit Wish");
        dialog.setHeaderText("Update wish details");

        URL cssUrl = getClass().getResource("wishlist.css");
        if (cssUrl != null) {
            dialog.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
        }

        dialog.getDialogPane().getStyleClass().add("custom-dialog");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        saveButton.getStyleClass().add("dialog-button");
        cancelButton.getStyleClass().add("dialog-button");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("dialog-container");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.getStyleClass().add("vbox");

        TextField nameField = new TextField(wish.getName());
        nameField.setPromptText("Name");
        nameField.getStyleClass().add("text-field");

        TextArea descField = new TextArea(wish.getDescription());
        descField.setPromptText("Description");
        descField.setPrefRowCount(3);
        descField.getStyleClass().add("text-field");

        TextField priceField = new TextField(String.format("%.2f", wish.getPrice()));
        priceField.setPromptText("Price");
        priceField.getStyleClass().add("text-field");

        TextField linkField = new TextField(wish.getLink());
        linkField.setPromptText("Link to item");
        linkField.getStyleClass().add("text-field");

        ComboBox<Wishlist> wishlistCombo = new ComboBox<>();
        wishlistCombo.setItems(FXCollections.observableArrayList(currentUser.getWishlists()));
        wishlistCombo.setPromptText("Select Wishlist");
        wishlistCombo.setValue(currentWishlist);

        wishlistCombo.setConverter(new StringConverter<Wishlist>() {
            @Override
            public String toString(Wishlist wishlist) {
                return wishlist == null ? "" : wishlist.getName();
            }

            @Override
            public Wishlist fromString(String string) {
                return null;
            }
        });

        ImageView imagePreview = new ImageView();
        imagePreview.setFitHeight(100);
        imagePreview.setFitWidth(100);
        imagePreview.setPreserveRatio(true);

        if (wish.getImage() != null) {
            imagePreview.setImage(wish.getImage());
        }

        Button addImageButton = new Button("Choose Image");
        addImageButton.setId("addImageButton");

        final String[] selectedImagePath = {wish.getImagePath()};

        addImageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                try {
                    selectedImagePath[0] = selectedFile.toURI().toString();
                    Image image = new Image(selectedImagePath[0]);
                    imagePreview.setImage(image);
                } catch (Exception e) {
                    showErrorDialog("Could not load image: " + e.getMessage());
                }
            }
        });

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Link:"), 0, 3);
        grid.add(linkField, 1, 3);
        grid.add(new Label("Wishlist:"), 0, 4);
        grid.add(wishlistCombo, 1, 4);
        grid.add(new Label("Image:"), 0, 5);
        grid.add(imagePreview, 1, 5);
        grid.add(addImageButton, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText();
                    String description = descField.getText();
                    double price;
                    try {
                        price = Double.parseDouble(priceField.getText());
                    } catch (NumberFormatException e) {
                        price = 0;
                    }
                    String link = linkField.getText();

                    wish.setName(name);
                    wish.setDescription(description);
                    wish.setPrice(price);
                    wish.setLink(link);
                    wish.setImagePath(selectedImagePath[0]);

                    return wish;
                } catch (Exception e) {
                    showErrorDialog("Error updating wish: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Wish> result = dialog.showAndWait();
        result.ifPresent(updatedWish -> {
            Wishlist selectedWishlist = wishlistCombo.getValue();

            if (selectedWishlist != currentWishlist) {
                currentWishlist.removeWish(wish);
                selectedWishlist.addWish(wish);

                if (selectedWishlist == currentWishlist) {
                    displayWishlistItems();
                }
            } else {
                displayWishlistItems();
            }

            FileManager.saveUserData(currentUser, USER_DATA_FILE);
        });
    }

    private void showAddWishDialog() {
        Dialog<Wish> dialog = new Dialog<>();
        dialog.setTitle("Add New Wish");
        dialog.setHeaderText("Enter your wish details");

        URL cssUrl = getClass().getResource("wishlist.css");
        if (cssUrl != null) {
            dialog.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
        }

        dialog.getDialogPane().getStyleClass().add("custom-dialog");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        saveButton.getStyleClass().add("dialog-button");
        cancelButton.getStyleClass().add("dialog-button");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("dialog-container");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.getStyleClass().add("vbox");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.getStyleClass().add("text-field");

        TextArea descField = new TextArea();
        descField.setPromptText("Description");
        descField.setPrefRowCount(3);
        descField.getStyleClass().add("text-field");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        priceField.getStyleClass().add("text-field");

        TextField linkField = new TextField();
        linkField.setPromptText("Link to item");
        linkField.getStyleClass().add("text-field");

        ComboBox<Wishlist> wishlistCombo = new ComboBox<>();
        wishlistCombo.setItems(FXCollections.observableArrayList(currentUser.getWishlists()));
        wishlistCombo.setPromptText("Select Wishlist");
        wishlistCombo.setValue(currentWishlist);

        wishlistCombo.setConverter(new StringConverter<Wishlist>() {
            @Override
            public String toString(Wishlist wishlist) {
                return wishlist == null ? "" : wishlist.getName();
            }

            @Override
            public Wishlist fromString(String string) {
                return null;
            }
        });

        ImageView imagePreview = new ImageView();
        imagePreview.setFitHeight(100);
        imagePreview.setFitWidth(100);
        imagePreview.setPreserveRatio(true);

        Button addImageButton = new Button("Choose Image");
        addImageButton.setId("addImageButton");

        final String[] selectedImagePath = {null};

        addImageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                try {
                    selectedImagePath[0] = selectedFile.toURI().toString();
                    Image image = new Image(selectedImagePath[0]);
                    imagePreview.setImage(image);
                } catch (Exception e) {
                    showErrorDialog("Could not load image: " + e.getMessage());
                }
            }
        });

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Link:"), 0, 3);
        grid.add(linkField, 1, 3);
        grid.add(new Label("Wishlist:"), 0, 4);
        grid.add(wishlistCombo, 1, 4);
        grid.add(new Label("Image:"), 0, 5);
        grid.add(imagePreview, 1, 5);
        grid.add(addImageButton, 1, 6);

        dialog.getDialogPane().setContent(grid);

        nameField.requestFocus();

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText();
                    String description = descField.getText();
                    double price = 0;
                    try {
                        price = Double.parseDouble(priceField.getText());
                    } catch (NumberFormatException e) {
                    }
                    String link = linkField.getText();

                    return new Wish(name, description, price, link, selectedImagePath[0]);
                } catch (Exception e) {
                    showErrorDialog("Error creating wish: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Wish> result = dialog.showAndWait();
        result.ifPresent(wish -> {
            Wishlist selectedWishlist = wishlistCombo.getValue();
            if (selectedWishlist != null) {
                selectedWishlist.addWish(wish);
                if (selectedWishlist == currentWishlist) {
                    displayWishlistItems();
                }
                FileManager.saveUserData(currentUser, USER_DATA_FILE);
            }
        });
    }

    private void showUserSettingsDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("User Settings");
        dialog.setHeaderText("Edit your profile");

        URL cssUrl = getClass().getResource("wishlist.css");
        if (cssUrl != null) {
            dialog.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
        }

        dialog.getDialogPane().getStyleClass().add("custom-dialog");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        saveButton.getStyleClass().add("dialog-button");
        cancelButton.getStyleClass().add("dialog-button");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("dialog-container");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.getStyleClass().add("vbox");

        TextField usernameField = new TextField(currentUser.getUsername());
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("text-field");

        TextArea bioField = new TextArea(currentUser.getBio());
        bioField.setPromptText("Bio");
        bioField.setPrefRowCount(3);
        bioField.getStyleClass().add("text-field");

        ImageView imagePreview = new ImageView();
        imagePreview.setFitHeight(100);
        imagePreview.setFitWidth(100);
        imagePreview.setPreserveRatio(true);

        if (currentUser.getProfileImagePath() != null && !currentUser.getProfileImagePath().isEmpty()) {
            try {
                imagePreview.setImage(new Image(currentUser.getProfileImagePath()));
            } catch (Exception e) {
                System.err.println("Error loading profile image in settings: " + e.getMessage());
            }
        }

        Button addImageButton = new Button("Choose Profile Picture");
        addImageButton.setId("addImageButton");

        imagePreview.getStyleClass().add("image-view");

        final String[] selectedImagePath = {currentUser.getProfileImagePath()};

        addImageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Profile Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                try {
                    selectedImagePath[0] = selectedFile.toURI().toString();
                    Image image = new Image(selectedImagePath[0]);
                    imagePreview.setImage(image);
                } catch (Exception e) {
                    showErrorDialog("Could not load image: " + e.getMessage());
                }
            }
        });

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Bio:"), 0, 1);
        grid.add(bioField, 1, 1);
        grid.add(new Label("Profile Picture:"), 0, 2);
        grid.add(imagePreview, 1, 2);
        grid.add(addImageButton, 1, 3);

        dialog.getDialogPane().setContent(grid);

        usernameField.requestFocus();

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                currentUser.setUsername(usernameField.getText());
                currentUser.setBio(bioField.getText());
                currentUser.setProfileImagePath(selectedImagePath[0]);
                return currentUser;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            updateUserDisplay();

            FileManager.saveUserData(currentUser, USER_DATA_FILE);
        });
    }

    private void showWishlistSettingsDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Wishlist Settings");
        dialog.setHeaderText("Manage your wishlists");

        URL cssUrl = getClass().getResource("wishlist.css");
        if (cssUrl != null) {
            dialog.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
        }

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);

        VBox content = new VBox(10);
        content.getStyleClass().add("dialog-container");
        content.setPadding(new Insets(20));
        content.getStyleClass().add("vbox");

        Label newWishlistLabel = new Label("Create New Wishlist");
        newWishlistLabel.getStyleClass().add("section-title");

        TextField nameField = new TextField();
        nameField.setPromptText("Wishlist Name");
        nameField.getStyleClass().add("text-field");


        Button createButton = new Button("Create Wishlist");
        createButton.setOnAction(event -> {
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                showErrorDialog("Wishlist name cannot be empty");
                return;
            }

            Wishlist newWishlist = new Wishlist(name);
            currentUser.addWishlist(newWishlist);

            updateCategoryBar();

            if (currentUser.getWishlists().size() == 1) {
                setCurrentWishlist(newWishlist);
            }

            FileManager.saveUserData(currentUser, USER_DATA_FILE);

            nameField.clear();
        });

        HBox createWishlistBox = new HBox(10);
        createWishlistBox.setAlignment(Pos.CENTER_RIGHT);
        createWishlistBox.getChildren().add(createButton);

        Label existingWishlistsLabel = new Label("Existing Wishlists");
        existingWishlistsLabel.getStyleClass().add("section-title");

        ListView<Wishlist> wishlistsListView = new ListView<>();
        wishlistsListView.setItems(FXCollections.observableArrayList(currentUser.getWishlists()));
        wishlistsListView.setPrefHeight(150);


        wishlistsListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Wishlist item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setOnAction(event -> {
            Wishlist selected = wishlistsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Wishlist");
                alert.setHeaderText("Delete " + selected.getName());
                alert.setContentText("Are you sure you want to delete this wishlist? All items will be lost.");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {

                    currentUser.removeWishlist(selected);

                    wishlistsListView.setItems(FXCollections.observableArrayList(currentUser.getWishlists()));
                    updateCategoryBar();

                    if (selected == currentWishlist) {
                        if (!currentUser.getWishlists().isEmpty()) {
                            setCurrentWishlist(currentUser.getWishlists().get(0));
                        } else {
                            currentWishlist = null;
                            wishlistGrid.getChildren().clear();
                        }
                    }

                    FileManager.saveUserData(currentUser, USER_DATA_FILE);
                }
            }
        });

        Label importExportLabel = new Label("Import/Export Wishlists");
        importExportLabel.getStyleClass().add("section-title");

        Button exportButton = new Button("Export to TXT");
        exportButton.setOnAction(event -> {
            Wishlist selected = wishlistsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Export Wishlist");
                fileChooser.setInitialFileName(selected.getName() + ".txt");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Text Files", "*.txt")
                );
                File file = fileChooser.showSaveDialog(primaryStage);
                if (file != null) {
                    FileManager.exportWishlistToText(selected, file.getAbsolutePath());
                }
            } else {
                showErrorDialog("Please select a wishlist to export");
            }
        });

        Button importButton = new Button("Import from TXT");
        importButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import Wishlist");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt")
            );
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                Wishlist imported = FileManager.importWishlistFromText(file.getAbsolutePath());
                if (imported != null) {
                    currentUser.addWishlist(imported);

                    wishlistsListView.setItems(FXCollections.observableArrayList(currentUser.getWishlists()));
                    updateCategoryBar();

                    FileManager.saveUserData(currentUser, USER_DATA_FILE);
                }
            }
        });

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(deleteButton);
        HBox FileButtonBox = new HBox(10);
        FileButtonBox.getChildren().addAll(exportButton, importButton);

        content.getChildren().addAll(
                newWishlistLabel,
                nameField,
                createWishlistBox,
                new Separator(),
                existingWishlistsLabel,
                wishlistsListView,
                buttonBox,
                new Separator(),
                importExportLabel,
                FileButtonBox
        );

        dialog.getDialogPane().setContent(content);

        dialog.showAndWait();
    }

    private void navigateToWishlists() {
        showWishlistSettingsDialog();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

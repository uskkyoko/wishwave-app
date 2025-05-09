package com.example.wishwaveapp.model;

import javafx.scene.image.Image;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Wish implements Serializable {
    private String name;
    private String description;
    private double price;
    private String link;
    private String imagePath;
    private transient Image image;
    private LocalDateTime dateAdded;

    public Wish(String name, String description, double price, String link, String imagePath) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.link = link;
        this.imagePath = imagePath;
        this.dateAdded = LocalDateTime.now();

        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                this.image = new Image(imagePath);
            } catch (Exception e) {
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                this.image = new Image(imagePath);
            } catch (Exception e) {
            }
        }
    }

    public Image getImage() {
        if (image == null && imagePath != null && !imagePath.isEmpty()) {
            try {
                this.image = new Image(imagePath);
                System.out.println("Successfully loaded image from: " + imagePath);
            } catch (Exception e) {
                System.err.println("Failed to load image from path: " + imagePath);
                e.printStackTrace();
            }
        }
        return image;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    @Override
    public String toString() {
        return name;
    }
}
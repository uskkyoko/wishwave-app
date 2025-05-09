package com.example.wishwaveapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String username;
    private String bio;
    private String profileImagePath;
    private List<Wishlist> wishlists;

    public User(String username, String bio, String profileImagePath) {
        this.username = username;
        this.bio = bio;
        this.profileImagePath = profileImagePath;
        this.wishlists = new ArrayList<>();
    }

    public void addWishlist(Wishlist wishlist) {
        wishlists.add(wishlist);
    }

    public void removeWishlist(Wishlist wishlist) {
        wishlists.remove(wishlist);
    }

    public List<Wishlist> getWishlists() {
        return new ArrayList<>(wishlists);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
}

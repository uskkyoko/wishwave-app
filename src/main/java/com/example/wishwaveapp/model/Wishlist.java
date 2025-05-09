package com.example.wishwaveapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Wishlist implements Serializable {
    private String name;
    private List<Wish> wishes;

    public Wishlist(String name) {
        this.name = name;
        this.wishes = new ArrayList<>();
    }

    public void addWish(Wish wish) {
        wishes.add(wish);
    }

    public void removeWish(Wish wish) {
        wishes.remove(wish);
    }

    public List<Wish> getWishes() {
        return new ArrayList<>(wishes);
    }

    public void sortByPriceAscending() {
        wishes.sort(Comparator.comparing(Wish::getPrice));
    }

    public void sortByPriceDescending() {
        wishes.sort(Comparator.comparing(Wish::getPrice).reversed());
    }

    public void sortByName() {
        wishes.sort(Comparator.comparing(Wish::getName));
    }

    public void sortByDateAdded() {
        wishes.sort(Comparator.comparing(Wish::getDateAdded).reversed());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

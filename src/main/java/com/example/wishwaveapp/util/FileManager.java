package com.example.wishwaveapp.util;

import com.example.wishwaveapp.model.User;
import com.example.wishwaveapp.model.Wish;
import com.example.wishwaveapp.model.Wishlist;

import java.io.*;

public class FileManager {

    public static void saveUserData(User user, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(user);
            System.out.println("User data saved successfully to " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static User loadUserData(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            User user = (User) ois.readObject();
            System.out.println("User data loaded successfully from " + filePath);
            return user;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading user data: " + e.getMessage());
            return createDefaultUser();
        }
    }

    public static void exportWishlistToText(Wishlist wishlist, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Wishlist: " + wishlist.getName());
            writer.println("\nItems:");

            for (Wish wish : wishlist.getWishes()) {
                writer.println("\n- " + wish.getName());

                if (wish.getDescription() != null && !wish.getDescription().isEmpty()) {
                    writer.println("  Description: " + wish.getDescription());
                }
                writer.println("  Price: $" + wish.getPrice());
                writer.println("  Link: " + wish.getLink());
                if (wish.getImagePath() != null && !wish.getImagePath().isEmpty()) {
                    writer.println("  Image: " + wish.getImagePath());
                }
            }

            System.out.println("Wishlist exported successfully to " + filePath);
        } catch (IOException e) {
            System.err.println("Error exporting wishlist: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Wishlist importWishlistFromText(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            if (line == null || !line.startsWith("Wishlist: ")) {
                throw new IOException("Invalid wishlist file format - missing wishlist name");
            }
            String name = line.substring("Wishlist: ".length());
            Wishlist wishlist = new Wishlist(name);

            boolean itemsFound = false;
            while ((line = reader.readLine()) != null) {
                if (line.equals("Items:")) {
                    itemsFound = true;
                    break;
                }
            }

            if (!itemsFound) {
                throw new IOException("Invalid wishlist file format - 'Items:' section not found");
            }

            String wishName = null;
            double wishPrice = 0.0;
            String wishLink = "";
            String wishImagePath = null;
            String wishDescription = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith("- ")) {
                    if (wishName != null) {
                        wishlist.addWish(new Wish(wishName, wishDescription, wishPrice, wishLink, wishImagePath));
                    }

                    wishName = line.substring("- ".length());
                    wishPrice = 0.0;
                    wishLink = "";
                    wishImagePath = null;
                    wishDescription = "";
                }
                else if (line.startsWith("Price: $")) {
                    try {
                        String priceStr = line.substring("Price: $".length());
                        wishPrice = Double.parseDouble(priceStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing price: " + line);
                    }
                }
                else if (line.startsWith("Link: ")) {
                    wishLink = line.substring("Link: ".length());
                }
                else if (line.startsWith("Image: ")) {
                    wishImagePath = line.substring("Image: ".length());
                }
                else if (line.startsWith("Description: ")) {
                    wishDescription = line.substring("Description: ".length());
                }
            }

            if (wishName != null) {
                wishlist.addWish(new Wish(wishName, wishDescription, wishPrice, wishLink, wishImagePath));
            }

            System.out.println("Wishlist imported successfully from " + filePath);
            return wishlist;
        } catch (IOException e) {
            System.err.println("Error importing wishlist: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    //DEFAULT FOR HOMEWORK
    private static User createDefaultUser() {
        User user = new User("ulianasova", "<3>", "/C:/Uliana/images/art/Geto Suguru.jpg");

        Wishlist techWishlist = new Wishlist("Tech Gadgets");
        techWishlist.addWish(new Wish("MacBook Pro", "16-inch, M1 Pro, Space Gray", 2399.99, "https://apple.com", ""));
        techWishlist.addWish(new Wish("AirPods Pro", "Active Noise Cancellation", 249.99, "https://apple.com", ""));

        Wishlist booksWishlist = new Wishlist("Books");
        booksWishlist.addWish(new Wish("Dune", "Frank Herbert's sci-fi classic", 15.99, "https://amazon.com", ""));
        booksWishlist.addWish(new Wish("The Hobbit", "J.R.R. Tolkien", 12.99, "https://amazon.com", ""));

        user.addWishlist(techWishlist);
        user.addWishlist(booksWishlist);

        return user;
    }
}
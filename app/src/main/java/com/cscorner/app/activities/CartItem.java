package com.cscorner.app.activities;

import java.io.Serializable;

public class CartItem implements Serializable {
    public String cartId;
    public String bookName;
    public String author;
    public String price;
    public String img;
    public int quantity;

    // Required empty constructor for Firestore
    public CartItem() {}

    public CartItem(String bookName, String author, String price, String img, int quantity) {
        this.bookName = bookName;
        this.author = author;
        this.price = price;
        this.img = img;
        this.quantity = quantity;
    }
}

package com.cscorner.app.models;

import androidx.annotation.NonNull;

public class Book {

    private String bookName;
    private String author;
    private String category;
    private String price;
    private String type;
    private String description;
    private String img;
    private int quantity;

    public Book() {} // Firestore requires default constructor

    public Book(String bookName, String author, String category, String price,
                String type, String description, String img, int quantity) {
        this.bookName = bookName;
        this.author = author;
        this.category = category;
        this.price = price;
        this.type = type;
        this.description = description;
        this.img = img;
        this.quantity = quantity;
    }

    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Book)) return false;
        Book other = (Book) obj;
        return bookName != null && bookName.equals(other.bookName);
    }

    @Override
    public int hashCode() {
        return bookName != null ? bookName.hashCode() : 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "Book{" + "bookName='" + bookName + '\'' + ", author='" + author + '\'' +
                ", category='" + category + '\'' + ", price='" + price + '\'' +
                ", type='" + type + '\'' + ", quantity=" + quantity + '}';
    }
}

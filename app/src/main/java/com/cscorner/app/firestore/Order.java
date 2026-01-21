package com.cscorner.app.firestore;

public class Order {
    public String uid;
    public String name;
    public String phone;
    public String address;
    public String orderSummary;
    public double amount;
    public String date;

    // Default constructor required for Firestore
    public Order() {}

    public Order(String uid, String name, String phone, String address, String orderSummary, double amount, String date) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.orderSummary = orderSummary;
        this.amount = amount;
        this.date = date;
    }
}

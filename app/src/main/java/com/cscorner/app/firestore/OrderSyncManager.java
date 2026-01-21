package com.cscorner.app.firestore;

import android.content.Context;

import com.cscorner.app.OrderHistoryManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class OrderSyncManager {

    public static void processOrder(Context context,
                                    String name,
                                    String phone,
                                    String address,
                                    String orderSummary,
                                    double amount) {

        String date = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(new Date());

        // 1️⃣ Save locally (SharedPreferences)
        saveLocally(context, name, phone, address, orderSummary, amount, date);

        // 2️⃣ Save to Firebase
        saveToFirebase(name, phone, address, orderSummary, amount, date);
    }

    private static void saveLocally(Context context,
                                    String name,
                                    String phone,
                                    String address,
                                    String orderSummary,
                                    double amount,
                                    String date) {

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        String prefName = "OrderHistory_" + uid;
        String keyOrders = "all_orders";
        android.content.SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);

        String entry = name + " | " + phone + " | " + address + " | " + orderSummary
                + " | $" + String.format("%.2f", amount) + " | " + date;

        Set<String> orders = prefs.getStringSet(keyOrders, new HashSet<>());
        Set<String> updatedOrders = new HashSet<>(orders);
        updatedOrders.add(entry);
        prefs.edit().putStringSet(keyOrders, updatedOrders).apply();
    }

    private static void saveToFirebase(String name,
                                       String phone,
                                       String address,
                                       String orderSummary,
                                       double amount,
                                       String date) {

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Order order = new Order(uid, name, phone, address, orderSummary, amount, date);
        db.collection("Orders").add(order);
    }
}

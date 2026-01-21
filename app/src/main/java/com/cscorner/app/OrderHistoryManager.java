package com.cscorner.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.cscorner.app.firestore.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class OrderHistoryManager {

    // Save order locally + Firebase
    public static void saveOrder(Context context, String name, String phone, String address, String orderSummary, double amount) {
        String date = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(new Date());
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        // --- Save locally ---
        String prefName = "OrderHistory_" + uid;
        String keyOrders = "all_orders";
        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        String entry = name + " | " + phone + " | " + address + " | " + orderSummary + " | $" + String.format("%.2f", amount) + " | " + date;
        Set<String> orders = prefs.getStringSet(keyOrders, new HashSet<>());
        Set<String> updatedOrders = new HashSet<>(orders);
        updatedOrders.add(entry);
        prefs.edit().putStringSet(keyOrders, updatedOrders).apply();

        // --- Save to Firestore ---
        Order firebaseOrder = new Order(uid, name, phone, address, orderSummary, amount, date);
        FirebaseFirestore.getInstance().collection("Orders").add(firebaseOrder);
    }

    // Get current user orders locally
    public static Set<String> getOrders(Context context) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return new HashSet<>();
        String prefName = "OrderHistory_" + uid;
        String keyOrders = "all_orders";
        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return prefs.getStringSet(keyOrders, new HashSet<>());
    }
}

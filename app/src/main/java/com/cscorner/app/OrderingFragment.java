package com.cscorner.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cscorner.app.adapters.CartAdapter;
import com.cscorner.app.activities.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderingFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<CartItem> cartList;
    private TextView tvTotal;
    private Button btnPayNow;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_ordering, container, false);

        recyclerView = view.findViewById(R.id.rvCartItems);
        tvTotal = view.findViewById(R.id.tvTotal);
        btnPayNow = view.findViewById(R.id.btnPayNow);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartList = new ArrayList<>();
        adapter = new CartAdapter(getContext(), cartList);
        recyclerView.setAdapter(adapter);

        adapter.setOnCartItemChangeListener(this::loadCartItems);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        loadCartItems();

        btnPayNow.setOnClickListener(v -> reduceStockThenProceed());

        return view;
    }

    private void loadCartItems() {
        if (currentUser == null) return;

        CollectionReference cartRef = db.collection("Users")
                .document(currentUser.getUid())
                .collection("Cart");

        cartRef.get().addOnSuccessListener(snapshot -> {
            cartList.clear();
            for (QueryDocumentSnapshot doc : snapshot) {
                CartItem item = doc.toObject(CartItem.class);
                if (item != null) {
                    item.cartId = doc.getId();
                    cartList.add(item);
                }
            }
            adapter.notifyDataSetChanged();
            calculateTotal();
        }).addOnFailureListener(e -> {
            if (getContext() != null)
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void calculateTotal() {
        double total = 0;
        for (CartItem item : cartList) {
            total += parsePrice(item.price) * item.quantity;
        }
        tvTotal.setText("Total: $" + String.format("%.2f", total));
    }

    private double parsePrice(String price) {
        try {
            return Double.parseDouble(price.replace("$", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void reduceStockThenProceed() {
        if (cartList.isEmpty()) {
            Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        for (CartItem item : cartList) {
            DocumentReference bookRef = db.collection("Books")
                    .document(item.bookName);

            int buyQty = item.quantity;

            db.runTransaction(transaction -> {
                DocumentSnapshot snapshot = transaction.get(bookRef);

                if (!snapshot.exists()) {
                    throw new RuntimeException("Book not found");
                }

                long currentQty = snapshot.getLong("quantity");

                if (currentQty < buyQty) {
                    throw new RuntimeException("Not enough stock");
                }

                long newQty = currentQty - buyQty;
                transaction.update(bookRef, "quantity", newQty);

                return null;
            });
        }

        clearCart();
        goToPaymentInfo();
    }

    private void clearCart() {
        CollectionReference cartRef = db.collection("Users")
                .document(currentUser.getUid())
                .collection("Cart");

        for (CartItem item : cartList) {
            cartRef.document(item.cartId).delete();
        }
    }

    private void goToPaymentInfo() {
        double totalAmount = 0;
        for (CartItem item : cartList) {
            totalAmount += parsePrice(item.price) * item.quantity;
        }

        Bundle bundle = new Bundle();
        bundle.putDouble("totalAmount", totalAmount);
        bundle.putSerializable("cartList", new ArrayList<>(cartList));

        PaymentInfoFragment fragment = new PaymentInfoFragment();
        fragment.setArguments(bundle);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}

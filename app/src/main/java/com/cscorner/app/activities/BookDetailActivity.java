package com.cscorner.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cscorner.app.BottomActivity;
import com.cscorner.app.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookDetailActivity extends AppCompatActivity {

    private ImageView imgBook;
    private TextView tvName, tvAuthor, tvPrice, tvType, tvDescription, tvQuantity, tvStock;
    private Button btnIncrease, btnDecrease;
    private MaterialButton btnAddToCart;
    private LinearLayout btnBack;

    private int quantity = 1;
    private int maxQuantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        //  Initialize views
        btnBack = findViewById(R.id.btnBack);
        imgBook = findViewById(R.id.imgBook);
        tvName = findViewById(R.id.tvBookName);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvPrice = findViewById(R.id.tvPrice);
        tvType = findViewById(R.id.tvType);
        tvDescription = findViewById(R.id.tvDescription);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvStock = findViewById(R.id.tvStock);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        // Get intent data
        String name = getIntent().getStringExtra("bookName");
        String author = getIntent().getStringExtra("author");
        String price = getIntent().getStringExtra("price");
        String type = getIntent().getStringExtra("type");
        String description = getIntent().getStringExtra("description");
        String img = getIntent().getStringExtra("img");
        maxQuantity = getIntent().getIntExtra("quantity", 10);

        // Fetch latest quantity from Firestore
        FirebaseFirestore.getInstance()
                .collection("Books")
                .document(name)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.getLong("quantity") != null) {
                        maxQuantity = documentSnapshot.getLong("quantity").intValue();
                        tvStock.setText("Stock: " + maxQuantity);
                    }
                });

        //  Set data to views
        tvName.setText(name);
        tvAuthor.setText("Author: " + author);
        tvPrice.setText(price);
        tvType.setText("Type: " + type);
        tvDescription.setText(description);
        tvQuantity.setText(String.valueOf(quantity));
        tvStock.setText("Stock: " + maxQuantity);

        if (img != null && !img.isEmpty()) {
            int drawableId = getResources().getIdentifier(img, "drawable", getPackageName());
            if (drawableId != 0) Glide.with(this).load(drawableId).into(imgBook);
        }

        // 🔹 Back button
        btnBack.setOnClickListener(v -> finish());

        // 🔹 Increase quantity
        btnIncrease.setOnClickListener(v -> {
            if (quantity < maxQuantity) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "Maximum stock reached", Toast.LENGTH_SHORT).show();
            }
        });

        // Decrease quantity
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        //  Add to cart
        btnAddToCart.setOnClickListener(v -> {
            btnAddToCart.setEnabled(false);

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                btnAddToCart.setEnabled(true);
                return;
            }

            CollectionReference cartRef = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(currentUser.getUid())
                    .collection("Cart");

            CartItem cartItem = new CartItem(name, author, price, img, quantity);

            cartRef.add(cartItem)
                    .addOnSuccessListener(documentReference -> {
                        cartItem.cartId = documentReference.getId();
                        Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, BottomActivity.class);
                        intent.putExtra("open_ordering_tab", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnAddToCart.setEnabled(true);
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}

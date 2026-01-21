package com.cscorner.app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cscorner.app.R;
import com.cscorner.app.activities.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<CartItem> cartList;
    private OnCartItemChangeListener listener;

    public interface OnCartItemChangeListener {
        void onCartItemDeleted();
    }

    public void setOnCartItemChangeListener(OnCartItemChangeListener listener) {
        this.listener = listener;
    }

    public CartAdapter(Context context, List<CartItem> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartList.get(position);

        holder.tvName.setText(item.bookName);
        holder.tvQty.setText("Qty: " + item.quantity);
        holder.tvPrice.setText("$" + item.price);

        int drawableId = context.getResources()
                .getIdentifier(item.img, "drawable", context.getPackageName());
        if (drawableId != 0) {
            Glide.with(context).load(drawableId).into(holder.imgBook);
        } else {
            holder.imgBook.setImageResource(R.drawable.ic_launcher_foreground);
        }

        holder.btnDelete.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && item.cartId != null) {
                FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(user.getUid())
                        .collection("Cart")
                        .document(item.cartId)
                        .delete()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show();
                            if (listener != null) listener.onCartItemDeleted();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("CartAdapter", "Failed delete: " + e.getMessage());
                            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBook, btnDelete;
        TextView tvName, tvQty, tvPrice;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.itemImg);
            tvName = itemView.findViewById(R.id.itemName);
            tvQty = itemView.findViewById(R.id.itemQty);
            tvPrice = itemView.findViewById(R.id.itemPrice);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

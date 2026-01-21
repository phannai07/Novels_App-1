package com.cscorner.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cscorner.app.R;
import com.cscorner.app.activities.BookDetailActivity;
import com.cscorner.app.models.Book;

import java.util.List;

public class BookAdapterAll extends RecyclerView.Adapter<BookAdapterAll.BookViewHolder> {

    private Context context;
    private List<Book> bookList;

    public BookAdapterAll(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.book_item_in_all, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {

        Book book = bookList.get(position);

        holder.bookName.setText(book.getBookName());
        holder.author.setText(book.getAuthor());
        holder.category.setText(book.getCategory());
        holder.price.setText(book.getPrice());
        holder.type.setText(book.getType());
        holder.description.setText(book.getDescription());

        int drawableId = context.getResources()
                .getIdentifier(book.getImg(), "drawable", context.getPackageName());
        Glide.with(context).load(drawableId).into(holder.img);

        holder.btnShop.setOnClickListener(v -> {
            Toast.makeText(
                    context,
                    "Added to cart: " + book.getBookName(),
                    Toast.LENGTH_SHORT
            ).show();
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookDetailActivity.class);
            intent.putExtra("bookName", book.getBookName());
            intent.putExtra("author", book.getAuthor());
            intent.putExtra("category", book.getCategory());
            intent.putExtra("price", book.getPrice());
            intent.putExtra("type", book.getType());
            intent.putExtra("description", book.getDescription());
            intent.putExtra("img", book.getImg());
            intent.putExtra("quantity", book.getQuantity());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView bookName, author, category, price, type, description;
        ImageButton btnShop;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            bookName = itemView.findViewById(R.id.bookName);
            author = itemView.findViewById(R.id.author);
            category = itemView.findViewById(R.id.category);
            price = itemView.findViewById(R.id.price);
            type = itemView.findViewById(R.id.type);
            description = itemView.findViewById(R.id.description);
            btnShop = itemView.findViewById(R.id.btnShop);

            itemView.setClickable(true);
            btnShop.setFocusable(false);
        }
    }
}

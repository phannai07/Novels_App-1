package com.cscorner.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.cscorner.app.R;
import com.cscorner.app.activities.BookDetailActivity;
import com.cscorner.app.models.Book;
import java.util.List;

public class BookAdapterHome extends RecyclerView.Adapter<BookAdapterHome.BookViewHolder> {

    private final Context context;
    private final List<Book> bookList;

    public BookAdapterHome(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_item_in_home, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.bookName.setText(book.getBookName());
        holder.author.setText(book.getAuthor());
        holder.price.setText(book.getPrice());

        int drawableId = context.getResources().getIdentifier(book.getImg(), "drawable", context.getPackageName());
        Glide.with(context)
                .load(drawableId)
                .placeholder(R.drawable.slide1)
                .into(holder.img);

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
    public int getItemCount() { return bookList.size(); }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView bookName, author, price;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            bookName = itemView.findViewById(R.id.bookName);
            author = itemView.findViewById(R.id.author);
            price = itemView.findViewById(R.id.price);
        }
    }
}

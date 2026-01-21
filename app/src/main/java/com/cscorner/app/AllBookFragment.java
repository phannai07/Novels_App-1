package com.cscorner.app;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cscorner.app.adapters.BookAdapterAll;
import com.cscorner.app.models.Book;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllBookFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookAdapterAll adapter;
    private List<Book> bookList;
    private List<Book> filteredList;
    private FirebaseFirestore db;

    private Button btnHorror, btnRomance, btnFantasy;
    private EditText searchBooks;
    private FrameLayout fragmentContainer;
    private ImageView cartIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_all_book_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fragmentContainer = view.findViewById(R.id.fragment_container);
        fragmentContainer.setVisibility(View.GONE); // Hidden initially

        bookList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new BookAdapterAll(getContext(), filteredList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        searchBooks = view.findViewById(R.id.searchBooks);

        // Category buttons
        btnHorror = view.findViewById(R.id.btnHorror);
        btnRomance = view.findViewById(R.id.btnRomance);
        btnFantasy = view.findViewById(R.id.btnFantasy);

        btnHorror.setOnClickListener(v -> openTypePage("Horror"));
        btnRomance.setOnClickListener(v -> openTypePage("Romance"));
        btnFantasy.setOnClickListener(v -> openTypePage("Fantasy"));

        // Cart icon
        cartIcon = view.findViewById(R.id.cartIcon);
        cartIcon.setOnClickListener(v -> {
            // Open ordering/cart fragment
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new OrderingFragment())
                    .addToBackStack(null)
                    .commit();
            fragmentContainer.setVisibility(View.VISIBLE);
        });

        // Search functionality
        searchBooks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBooks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadAllBooks();

        return view;
    }

    private void loadAllBooks() {
        db.collection("Books")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    bookList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Book book = doc.toObject(Book.class);
                        bookList.add(book);
                    }
                    filteredList.clear();
                    filteredList.addAll(bookList);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed to load books",
                                Toast.LENGTH_SHORT).show()
                );
    }

    // Open TypeBookFragment inside the FrameLayout
    private void openTypePage(String type) {
        fragmentContainer.setVisibility(View.VISIBLE);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, TypeBookFragment.newInstance(type))
                .addToBackStack(null)
                .commit();
    }

    // Filter books by search query
    private void filterBooks(String query) {
        filteredList.clear();
        for (Book book : bookList) {
            if (book.getBookName().toLowerCase().contains(query.toLowerCase()) ||
                    book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(book);
            }
        }
        adapter.notifyDataSetChanged();
    }
}

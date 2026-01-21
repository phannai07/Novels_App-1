package com.cscorner.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class TypeBookFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    private String type;

    private RecyclerView recyclerView;
    private BookAdapterAll adapter;
    private List<Book> bookList;
    private FirebaseFirestore db;

    // ✅ ADD: Back button
    private ImageView btnBack;

    public static TypeBookFragment newInstance(String type) {
        TypeBookFragment fragment = new TypeBookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_type_book, container, false);

        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }

        // ✅ ADD: find back button
        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v ->
                getFragmentManager().popBackStack()
        );

        recyclerView = view.findViewById(R.id.recyclerViewType);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bookList = new ArrayList<>();
        adapter = new BookAdapterAll(getContext(), bookList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadTypeBooks();

        return view;
    }

    private void loadTypeBooks() {
        db.collection("Books")
                .whereEqualTo("type", type)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    bookList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Book book = doc.toObject(Book.class);
                        bookList.add(book);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed to load books",
                                Toast.LENGTH_SHORT).show()
                );
    }
}

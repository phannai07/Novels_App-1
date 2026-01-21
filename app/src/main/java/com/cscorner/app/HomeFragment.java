package com.cscorner.app;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.cscorner.app.adapters.BookAdapterHome;
import com.cscorner.app.models.Book;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewBestSeller, recyclerViewRecommended, recyclerViewAllBooks;
    private BookAdapterHome adapterBestSeller, adapterRecommended, adapterAllBooks;

    private final ArrayList<Book> bestSellers = new ArrayList<>();
    private final ArrayList<Book> recommended = new ArrayList<>();
    private final ArrayList<Book> allBooks = new ArrayList<>();
    private final ArrayList<Book> allBooksBackup = new ArrayList<>();

    private FirebaseFirestore db;
    private ViewPager2 viewPager2;
    private EditText searchBooks;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        // ===== Search =====
        searchBooks = view.findViewById(R.id.searchBooks);

        // ===== Slider =====
        viewPager2 = view.findViewById(R.id.homeImageSlider);
        int[] images = {R.drawable.slide1, R.drawable.slide2, R.drawable.slide3};
        viewPager2.setAdapter(new SliderAdapter(images));

        // ===== Best Seller =====
        recyclerViewBestSeller = view.findViewById(R.id.recyclerViewBooks1);
        recyclerViewBestSeller.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        recyclerViewBestSeller.setNestedScrollingEnabled(false);
        adapterBestSeller = new BookAdapterHome(getContext(), bestSellers);
        recyclerViewBestSeller.setAdapter(adapterBestSeller);

        // ===== Recommended =====
        recyclerViewRecommended = view.findViewById(R.id.recyclerViewBooks2);
        recyclerViewRecommended.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        recyclerViewRecommended.setNestedScrollingEnabled(false);
        adapterRecommended = new BookAdapterHome(getContext(), recommended);
        recyclerViewRecommended.setAdapter(adapterRecommended);

        // ===== All Books =====
        recyclerViewAllBooks = view.findViewById(R.id.recyclerViewBooksAll);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerViewAllBooks.setLayoutManager(gridLayoutManager);
        recyclerViewAllBooks.setNestedScrollingEnabled(true);
        adapterAllBooks = new BookAdapterHome(getContext(), allBooks);
        recyclerViewAllBooks.setAdapter(adapterAllBooks);

        // ===== Firestore =====
        db = FirebaseFirestore.getInstance();
        loadBooks();

        // ===== Search Logic =====
        searchBooks.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                allBooks.clear();
                String query = s.toString().trim().toLowerCase();

                if (query.isEmpty()) {
                    allBooks.addAll(allBooksBackup);
                } else {
                    for (Book book : allBooksBackup) {
                        if (book.getBookName() != null &&
                                book.getBookName().toLowerCase().contains(query)) {
                            allBooks.add(book);
                        }
                    }
                }

                adapterAllBooks.notifyDataSetChanged();
                recyclerViewAllBooks.scrollToPosition(0);
            }
        });

        return view;
    }

    private void loadBooks() {
        db.collection("Books")
                .get()
                .addOnSuccessListener(snapshot -> {

                    bestSellers.clear();
                    recommended.clear();
                    allBooks.clear();
                    allBooksBackup.clear();

                    for (QueryDocumentSnapshot doc : snapshot) {
                        Book book = doc.toObject(Book.class);

                        // always add to ALL BOOKS
                        allBooksBackup.add(book);

                        String category = book.getCategory();
                        if (category != null) {
                            if (category.equalsIgnoreCase("Best Seller")) {
                                bestSellers.add(book);
                            } else if (category.equalsIgnoreCase("Recommended")) {
                                recommended.add(book);
                            }
                        }
                    }

                    allBooks.addAll(allBooksBackup);

                    adapterBestSeller.notifyDataSetChanged();
                    adapterRecommended.notifyDataSetChanged();
                    adapterAllBooks.notifyDataSetChanged();

                })
                .addOnFailureListener(e ->
                        Log.e("HomeFragment", "Firebase error", e)
                );
    }

    // ===== Auto Slider =====
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager2 == null) return;
            int next = (viewPager2.getCurrentItem() + 1) % 3;
            viewPager2.setCurrentItem(next, true);
            handler.postDelayed(this, 3000);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(sliderRunnable);
    }

    // ===== Slider Adapter =====
    static class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.Holder> {
        private final int[] images;
        SliderAdapter(int[] images) { this.images = images; }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView img = new ImageView(parent.getContext());
            img.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new Holder(img);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.image.setImageResource(images[position]);
        }

        @Override
        public int getItemCount() { return images.length; }

        static class Holder extends RecyclerView.ViewHolder {
            ImageView image;
            Holder(@NonNull View itemView) { super(itemView); image = (ImageView) itemView; }
        }
    }
}

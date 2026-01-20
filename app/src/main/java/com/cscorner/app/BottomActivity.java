package com.cscorner.app;

import android.content.Intent;
import android.os.Bundle;

import android.app.Fragment; // ✅ changed from androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BottomActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // 🔐 Protect screen
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Check if we need to open Ordering tab
        boolean openOrdering = getIntent().getBooleanExtra("open_ordering_tab", false);

        // Load default fragment
        if (savedInstanceState == null) {
            if (openOrdering) {
                bottomNavigationView.setSelectedItemId(R.id.ordering);
                loadFragment(new OrderingFragment());
            } else {
                loadFragment(new HomeFragment());
            }
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            int id = item.getItemId();
            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (id == R.id.nav_all_book) {
                fragment = new AllBookFragment();
            } else if (id == R.id.ordering) {
                fragment = new OrderingFragment();
            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }
            return loadFragment(fragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment == null) return false;

        // ✅ Use old FragmentManager since fragments are android.app.Fragment
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        return true;
    }
}

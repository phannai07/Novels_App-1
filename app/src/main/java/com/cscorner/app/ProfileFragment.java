package com.cscorner.app;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvEmail;
    private AppCompatButton btnLogout, btnOrderHistory, btnLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile_fragment, container, false);

        // Bind views
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnOrderHistory = view.findViewById(R.id.btnOrderHistory);
        btnLocation = view.findViewById(R.id.btnLocation);

        // Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (user != null) {
            tvEmail.setText(user.getEmail());
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            tvUsername.setText(documentSnapshot.getString("username"));
                        }
                    });
        }

        // Order History
        btnOrderHistory.setOnClickListener(v -> {
            try {
                HistoryFragment historyFragment = new HistoryFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, historyFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(),
                        "Navigation Error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

        // 📍 Location → Google Maps (RUPP - your link)
        btnLocation.setOnClickListener(v -> {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://maps.app.goo.gl/qrGA7VBeSpCyJ7He8")
            );
            startActivity(intent);
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });

        return view;
    }
}

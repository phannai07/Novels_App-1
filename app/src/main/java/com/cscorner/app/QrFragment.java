package com.cscorner.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class QrFragment extends Fragment {

    private LinearLayout btnBack;
    private TextView tvPaymentAmount;
    private ImageView ivQrCode;
    private Button btnPaid;

    private double totalAmount;
    private String userName, userPhone, userAddress, orderSummary;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_qr_fragment, container, false);

        btnBack = view.findViewById(R.id.btnBack);
        tvPaymentAmount = view.findViewById(R.id.tvPaymentAmount);
        ivQrCode = view.findViewById(R.id.ivQrCode);
        btnPaid = view.findViewById(R.id.btnPaid);

        // Receive Data
        if (getArguments() != null) {
            totalAmount = getArguments().getDouble("totalAmount", 0.0);
            userName = getArguments().getString("userName", "Customer");
            userPhone = getArguments().getString("userPhone", "");
            userAddress = getArguments().getString("userAddress", "Delivery Order");
            orderSummary = getArguments().getString("orderSummary", "");
            tvPaymentAmount.setText("Amount: $" + String.format("%.2f", totalAmount));
        }

        // ✅ FIX: use getFragmentManager() instead of getParentFragmentManager()
        btnBack.setOnClickListener(v -> getFragmentManager().popBackStack());

        btnPaid.setOnClickListener(v -> {
            // ✅ FIX: use getActivity() instead of requireContext()
            OrderHistoryManager.saveOrder(getActivity(), userName, userPhone, userAddress, orderSummary, totalAmount);

            Toast.makeText(getActivity(), "Order Saved & Synced to Cloud!", Toast.LENGTH_SHORT).show();

            // ✅ FIX: clear checkout stack
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });

        return view;
    }
}

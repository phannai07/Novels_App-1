package com.cscorner.app;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;

import com.cscorner.app.activities.CartItem;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PaymentInfoFragment extends Fragment {

    private EditText etUserName, etUserPhone, etUserAddress;
    private RadioGroup rgDeliveryOption;
    private MaterialButton btnConfirmPayment;
    private TextView tvTotalAmount, tvItemsCount, tvBookNames;

    private double totalAmount = 0;
    private int totalItems = 0;
    private List<CartItem> cartList;
    private String deliveryOption = "Delivery";
    private final double DELIVERY_FEE = 2.0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_payment_info, container, false);

        // Bind views
        etUserName = view.findViewById(R.id.etUserName);
        etUserPhone = view.findViewById(R.id.etUserPhone);
        etUserAddress = view.findViewById(R.id.etUserAddress);
        rgDeliveryOption = view.findViewById(R.id.rgDeliveryOption);
        btnConfirmPayment = view.findViewById(R.id.btnConfirmPayment);
        tvTotalAmount = view.findViewById(R.id.tvPaymentTotal);
        tvItemsCount = view.findViewById(R.id.tvItemsCount);
        tvBookNames = view.findViewById(R.id.tvBookNames);
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        });

        // Get cart data from arguments
        if (getArguments() != null) {
            totalAmount = getArguments().getDouble("totalAmount", 0);
            cartList = (List<CartItem>) getArguments().getSerializable("cartList");

            if (cartList != null && !cartList.isEmpty()) {
                totalItems = 0;
                StringBuilder bookNamesBuilder = new StringBuilder();
                for (CartItem item : cartList) {
                    // Use CartItem quantity directly
                    totalItems += item.quantity;
                    bookNamesBuilder.append("• ").append(item.bookName)
                            .append(" x").append(item.quantity).append("\n");
                }
                tvBookNames.setText(bookNamesBuilder.toString().trim());
            } else {
                tvBookNames.setText("No items in cart");
                totalItems = 0;
            }
        }

        updateTotal();

        // Delivery options
        rgDeliveryOption.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDelivery) {
                deliveryOption = "Delivery";
                etUserAddress.setHint("Enter Delivery Address");
            } else {
                deliveryOption = "Pick-up";
                etUserAddress.setHint("");
            }
            updateTotal();
        });

        btnConfirmPayment.setOnClickListener(v -> completeOrder());

        return view;
    }

    private void updateTotal() {
        double displayTotal = totalAmount;
        if ("Delivery".equals(deliveryOption)) displayTotal += DELIVERY_FEE;

        tvTotalAmount.setText("Total: $" + String.format("%.2f", displayTotal));
        tvItemsCount.setText("Items: " + totalItems);
    }

    private void completeOrder() {
        String name = etUserName.getText().toString().trim();
        String phone = etUserPhone.getText().toString().trim();
        String address = etUserAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) ||
                ("Delivery".equals(deliveryOption) && TextUtils.isEmpty(address))) {
            Toast.makeText(getContext(), "Please fill in all required details", Toast.LENGTH_SHORT).show();
            return;
        }

        double finalTotal = totalAmount;
        if ("Delivery".equals(deliveryOption)) finalTotal += DELIVERY_FEE;

        String orderSummary = buildOrderSummary(cartList);

        QrFragment qrFragment = new QrFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userName", name);
        bundle.putString("userPhone", phone);
        bundle.putString("userAddress", address);
        bundle.putString("orderSummary", orderSummary);
        bundle.putDouble("totalAmount", finalTotal);
        qrFragment.setArguments(bundle);

        if (getFragmentManager() != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, qrFragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }

    private String buildOrderSummary(List<CartItem> cartList) {
        if (cartList == null || cartList.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();
        for (CartItem item : cartList) {
            builder.append(item.bookName)
                    .append(" x")
                    .append(item.quantity)
                    .append("; ");
        }
        return builder.toString().trim();
    }
}
package com.cscorner.app;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryFragment extends Fragment {

    private RecyclerView rvOrderHistory;
    private LinearLayout btnBack;
    private TextView tvEmptyHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_order_history_fragment, container, false);

        btnBack = view.findViewById(R.id.btnBack);
        rvOrderHistory = view.findViewById(R.id.rvOrderHistory);
        tvEmptyHistory = view.findViewById(R.id.tvEmptyHistory);

        // ✅ FIX: use getFragmentManager() instead of getParentFragmentManager()
        btnBack.setOnClickListener(v -> getFragmentManager().popBackStack());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrderHistory();
    }

    private void loadOrderHistory() {
        if (getContext() == null) return;

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        Set<String> orders = getContext()
                .getSharedPreferences("OrderHistory_" + uid, Context.MODE_PRIVATE)
                .getStringSet("all_orders", new HashSet<>());

        if (orders.isEmpty()) {
            tvEmptyHistory.setVisibility(View.VISIBLE);
            rvOrderHistory.setVisibility(View.GONE);
        } else {
            tvEmptyHistory.setVisibility(View.GONE);
            rvOrderHistory.setVisibility(View.VISIBLE);

            List<String> historyList = new ArrayList<>(orders);
            Collections.reverse(historyList);

            rvOrderHistory.setLayoutManager(new LinearLayoutManager(getContext()));
            rvOrderHistory.setAdapter(new HistoryAdapter(historyList));
        }
    }

    private static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

        private final List<String> data;

        HistoryAdapter(List<String> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String entry = data.get(position);
            String[] parts = entry.split(" \\| ");

            if (parts.length >= 6) {
                holder.tvName.setText("Name: " + parts[0]);
                holder.tvPhone.setText("Phone: " + parts[1]);
                holder.tvAddress.setText("Address: " + parts[2]);
                holder.tvOrderSummary.setText(parts[3]);
                holder.tvAmount.setText(parts[4]);
                holder.tvDate.setText(parts[5]);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvPhone, tvAddress, tvOrderSummary, tvAmount, tvDate;

            ViewHolder(View v) {
                super(v);
                tvName = v.findViewById(R.id.tvHistoryName);
                tvPhone = v.findViewById(R.id.tvHistoryPhone);
                tvAddress = v.findViewById(R.id.tvHistoryAddress);
                tvOrderSummary = v.findViewById(R.id.tvHistoryOrderSummary);
                tvAmount = v.findViewById(R.id.tvHistoryAmount);
                tvDate = v.findViewById(R.id.tvHistoryDate);
            }
        }
    }
}

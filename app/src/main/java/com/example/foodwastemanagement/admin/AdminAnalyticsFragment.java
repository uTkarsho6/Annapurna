package com.example.foodwastemanagement.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.foodwastemanagement.R;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AdminAnalyticsFragment extends Fragment {

    private TextView tvTotalDonationsCount;
    private TextView tvTotalClaimedCount;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_analytics, container, false);

        tvTotalDonationsCount = view.findViewById(R.id.tvTotalDonationsCount);
        tvTotalClaimedCount = view.findViewById(R.id.tvTotalClaimedCount);
        db = FirebaseFirestore.getInstance();

        fetchAnalytics();

        return view;
    }

    private void fetchAnalytics() {
        // Count total donations
        Query totalQuery = db.collection("donations");
        AggregateQuery countTotalQuery = totalQuery.count();
        countTotalQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                tvTotalDonationsCount.setText(String.valueOf(snapshot.getCount()));
            } else {
                Toast.makeText(getContext(), "Failed to count total donations", Toast.LENGTH_SHORT).show();
            }
        });

        // Count claimed donations
        Query claimedQuery = db.collection("donations").whereEqualTo("status", "claimed");
        AggregateQuery countClaimedQuery = claimedQuery.count();
        countClaimedQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                tvTotalClaimedCount.setText(String.valueOf(snapshot.getCount()));
            } else {
                Toast.makeText(getContext(), "Failed to count claimed food", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.foodwastemanagement.receiver;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodwastemanagement.R;
import com.example.foodwastemanagement.adapters.DonationAdapter;
import com.example.foodwastemanagement.models.Donation;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class BrowseSurplusFragment extends Fragment implements DonationAdapter.OnDonationClickListener {

    private RecyclerView rvDonations;
    private ProgressBar pbBrowse;
    private TextView tvEmptyState;

    private DonationAdapter adapter;
    private final List<Donation> donationList = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse_surplus, container, false);

        rvDonations = view.findViewById(R.id.rvDonations);
        pbBrowse = view.findViewById(R.id.pbBrowse);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        db = FirebaseFirestore.getInstance();

        // Setup RecyclerView
        adapter = new DonationAdapter(donationList, this);
        rvDonations.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDonations.setAdapter(adapter);

        fetchAvailableDonations();

        return view;
    }

    private void fetchAvailableDonations() {
        pbBrowse.setVisibility(View.VISIBLE);
        rvDonations.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);

        // Real-time listener — updates automatically when data changes (T-29)
        db.collection("donations")
                .whereEqualTo("status", "available")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        pbBrowse.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error loading donations: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    donationList.clear();
                    if (snapshots != null) {
                        for (var doc : snapshots.getDocuments()) {
                            Donation donation = doc.toObject(Donation.class);
                            if (donation != null) {
                                donation.setDonationId(doc.getId());
                                donationList.add(donation);
                            }
                        }
                        
                        // Sort locally to avoid needing a Firestore Composite Index
                        donationList.sort((d1, d2) -> {
                            if (d1.getCreatedAt() == null || d2.getCreatedAt() == null) return 0;
                            return d2.getCreatedAt().compareTo(d1.getCreatedAt()); // DESC
                        });
                    }

                    pbBrowse.setVisibility(View.GONE);

                    if (donationList.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                        rvDonations.setVisibility(View.GONE);
                    } else {
                        rvDonations.setVisibility(View.VISIBLE);
                        tvEmptyState.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
    @Override
    public void onViewDetails(Donation donation) {
        Intent intent = new Intent(getContext(), DonationDetailActivity.class);
        intent.putExtra("DONATION_ID", donation.getDonationId());
        startActivity(intent);
    }
}

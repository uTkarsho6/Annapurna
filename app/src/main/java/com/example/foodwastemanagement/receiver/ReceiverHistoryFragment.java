package com.example.foodwastemanagement.receiver;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodwastemanagement.R;
import com.example.foodwastemanagement.adapters.DonationAdapter;
import com.example.foodwastemanagement.models.Donation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ReceiverHistoryFragment extends Fragment implements DonationAdapter.OnDonationClickListener {

    private RecyclerView rvHistory;
    private ProgressBar pbHistory;
    private TextView tvHistoryEmptyState;

    private DonationAdapter adapter;
    private final List<Donation> historicalList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receiver_history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        pbHistory = view.findViewById(R.id.pbHistory);
        tvHistoryEmptyState = view.findViewById(R.id.tvHistoryEmptyState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        adapter = new DonationAdapter(historicalList, this);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setAdapter(adapter);

        fetchClaimsHistory();

        return view;
    }

    private void fetchClaimsHistory() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();

        pbHistory.setVisibility(View.VISIBLE);
        rvHistory.setVisibility(View.GONE);
        tvHistoryEmptyState.setVisibility(View.GONE);

        // Fetch donations where status == 'claimed' AND claimedBy == this user
        db.collection("donations")
                .whereEqualTo("status", "claimed")
                .whereEqualTo("claimedBy", uid)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        pbHistory.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error loading history: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    historicalList.clear();
                    if (snapshots != null) {
                        for (var doc : snapshots.getDocuments()) {
                            Donation donation = doc.toObject(Donation.class);
                            if (donation != null) {
                                donation.setDonationId(doc.getId());
                                historicalList.add(donation);
                            }
                        }
                        
                        // Sort locally (newest claim first)
                        historicalList.sort((d1, d2) -> {
                            if (d1.getClaimedAt() == null || d2.getClaimedAt() == null) return 0;
                            return d2.getClaimedAt().compareTo(d1.getClaimedAt());
                        });
                    }

                    pbHistory.setVisibility(View.GONE);

                    if (historicalList.isEmpty()) {
                        tvHistoryEmptyState.setVisibility(View.VISIBLE);
                        rvHistory.setVisibility(View.GONE);
                    } else {
                        rvHistory.setVisibility(View.VISIBLE);
                        tvHistoryEmptyState.setVisibility(View.GONE);
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

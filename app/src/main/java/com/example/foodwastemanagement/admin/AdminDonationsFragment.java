package com.example.foodwastemanagement.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodwastemanagement.R;
import com.example.foodwastemanagement.adapters.DonationAdapter;
import com.example.foodwastemanagement.models.Donation;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class AdminDonationsFragment extends Fragment implements DonationAdapter.OnDonationClickListener {

    private RecyclerView rvAdminDonations;
    private ProgressBar pbAdminDonations;
    private DonationAdapter adapter;
    private final List<Donation> donationList = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_donations, container, false);

        rvAdminDonations = view.findViewById(R.id.rvAdminDonations);
        pbAdminDonations = view.findViewById(R.id.pbAdminDonations);
        db = FirebaseFirestore.getInstance();

        adapter = new DonationAdapter(donationList, this);
        rvAdminDonations.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAdminDonations.setAdapter(adapter);

        fetchDonations();

        return view;
    }

    private void fetchDonations() {
        pbAdminDonations.setVisibility(View.VISIBLE);
        rvAdminDonations.setVisibility(View.GONE);

        db.collection("donations")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        pbAdminDonations.setVisibility(View.GONE);
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
                        
                        // Sort locally by created time DESC
                        donationList.sort((d1, d2) -> {
                            if (d1.getCreatedAt() == null || d2.getCreatedAt() == null) return 0;
                            return d2.getCreatedAt().compareTo(d1.getCreatedAt()); 
                        });
                    }

                    pbAdminDonations.setVisibility(View.GONE);
                    rvAdminDonations.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onViewDetails(Donation donation) {
        // Since Admins shouldn't claim food, we simply show a toast or we could explicitly show detail screen without claim button
        // For now, we will just show a Toast showing who claimed it if applicable.
        String statusMessage = "Status: " + donation.getStatus() + "\nQuantity: " + donation.getQuantity() + "\nPhone: " + donation.getContactNumber();
        Toast.makeText(getContext(), statusMessage, Toast.LENGTH_LONG).show();
    }
}

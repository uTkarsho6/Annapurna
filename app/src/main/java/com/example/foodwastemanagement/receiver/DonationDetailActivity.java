package com.example.foodwastemanagement.receiver;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.foodwastemanagement.R;
import com.example.foodwastemanagement.models.Donation;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DonationDetailActivity extends AppCompatActivity {

    private Donation donation;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private MaterialButton btnClaimNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_detail);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String donationId = getIntent().getStringExtra("DONATION_ID");
        if (donationId == null) {
            Toast.makeText(this, "Error loading donation details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection("donations").document(donationId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        donation = documentSnapshot.toObject(Donation.class);
                        if (donation != null) {
                            donation.setDonationId(documentSnapshot.getId());
                            initUI();
                        }
                    } else {
                        Toast.makeText(this, "Donation no longer exists", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void initUI() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        TextView tvFoodName = findViewById(R.id.tvDetailFoodName);
        TextView tvQuantity = findViewById(R.id.tvDetailQuantity);
        TextView tvDeadline = findViewById(R.id.tvDetailDeadline);
        TextView tvContact = findViewById(R.id.tvDetailContact);
        TextView tvLocation = findViewById(R.id.tvDetailLocation);
        btnClaimNow = findViewById(R.id.btnClaimNow);

        tvFoodName.setText(donation.getFoodName());
        tvQuantity.setText(donation.getQuantity());
        tvContact.setText(donation.getContactNumber());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault());
        if (donation.getPickupTime() != null) {
            tvDeadline.setText(dateFormat.format(donation.getPickupTime().toDate()));
        }

        if (!TextUtils.isEmpty(donation.getManualAddress())) {
            tvLocation.setText(donation.getManualAddress());
        } else if (donation.getLatitude() != null && donation.getLongitude() != null) {
            tvLocation.setText("Lat: " + donation.getLatitude() + ", Lng: " + donation.getLongitude());
        } else {
            tvLocation.setText("Location not provided.");
        }

        btnClaimNow.setOnClickListener(v -> claimDonationWithTransaction());
    }

    private void claimDonationWithTransaction() {
        if (mAuth.getCurrentUser() == null) return;
        
        btnClaimNow.setEnabled(false);
        btnClaimNow.setText("Processing Claim...");

        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference donationRef = db.collection("donations").document(donation.getDonationId());

        db.runTransaction(transaction -> {

            Donation latestDonation = transaction.get(donationRef).toObject(Donation.class);
            if (latestDonation == null) {
                throw new FirebaseFirestoreException("Donation no longer exists.", FirebaseFirestoreException.Code.ABORTED);
            }
            if (!"available".equals(latestDonation.getStatus())) {
                throw new FirebaseFirestoreException("Sorry, this donation was just claimed by someone else!", FirebaseFirestoreException.Code.ABORTED);
            }

            // Perform the atomic update
            transaction.update(donationRef, "status", "claimed");
            transaction.update(donationRef, "claimedBy", uid);
            transaction.update(donationRef, "claimedAt", new Timestamp(new Date()));

            return null; // Success
        }).addOnSuccessListener(result -> {
            Toast.makeText(DonationDetailActivity.this, "Successfully claimed!", Toast.LENGTH_LONG).show();
            // Finish and return to browse list
            finish();
        }).addOnFailureListener(e -> {
            btnClaimNow.setEnabled(true);
            btnClaimNow.setText("Claim This Food");
            Toast.makeText(DonationDetailActivity.this, "Claim failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}

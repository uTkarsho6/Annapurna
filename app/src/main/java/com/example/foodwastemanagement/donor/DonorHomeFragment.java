package com.example.foodwastemanagement.donor;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.example.foodwastemanagement.R;
import com.example.foodwastemanagement.models.Donation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DonorHomeFragment extends Fragment {

    private TextInputEditText etFoodName, etQuantity, etContact, etManualAddress;
    private TextView tvDeadline, tvLocationStatus;
    private MaterialButton btnDeadline, btnSubmit;
    
    private Calendar calendar = Calendar.getInstance();
    private Double latitude, longitude;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donor_home, container, false);

        // Initialize Views
        etFoodName = view.findViewById(R.id.etFoodName);
        etQuantity = view.findViewById(R.id.etQuantity);
        etContact = view.findViewById(R.id.etContact);
        etManualAddress = view.findViewById(R.id.etManualAddress);
        tvDeadline = view.findViewById(R.id.tvDeadline);
        tvLocationStatus = view.findViewById(R.id.tvLocationStatus);
        btnDeadline = view.findViewById(R.id.btnDeadline);
        btnSubmit = view.findViewById(R.id.btnSubmitDonation);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Listeners
        btnDeadline.setOnClickListener(v -> showDatePicker());
        btnSubmit.setOnClickListener(v -> submitDonation());

        checkLocationPermissions();

        return view;
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Pickup Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            calendar.setTimeInMillis(selection);
            showTimePicker();
        });
        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void showTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Pickup Time")
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault());
            tvDeadline.setText(dateFormat.format(calendar.getTime()));
        });
        timePicker.show(getParentFragmentManager(), "TIME_PICKER");
    }

    private void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation();
        }
    }

    private void getLastLocation() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    tvLocationStatus.setText(String.format(Locale.getDefault(), "GPS Locked: %.4f, %.4f", latitude, longitude));
                    tvLocationStatus.setTextColor(getResources().getColor(R.color.primary));
                } else {
                    tvLocationStatus.setText("GPS failed. Please enter manual address.");
                    tvLocationStatus.setTextColor(getResources().getColor(R.color.error));
                }
            });
        } catch (SecurityException e) {
            tvLocationStatus.setText("Permission denied.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                tvLocationStatus.setText("Location permission denied. Use manual entry.");
                tvLocationStatus.setTextColor(getResources().getColor(R.color.error));
            }
        }
    }

    private void submitDonation() {
        String foodName = etFoodName.getText().toString().trim();
        String quantity = etQuantity.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String manualAddress = etManualAddress.getText().toString().trim();
        String deadlineText = tvDeadline.getText().toString();

        if (TextUtils.isEmpty(foodName) || TextUtils.isEmpty(quantity) || TextUtils.isEmpty(contact)) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (deadlineText.equals("Not set")) {
            Toast.makeText(getContext(), "Please set a pickup deadline", Toast.LENGTH_SHORT).show();
            return;
        }

        if (latitude == null && TextUtils.isEmpty(manualAddress)) {
            Toast.makeText(getContext(), "GPS failed. Manual address is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Posting...");

        String userId = mAuth.getUid();
        String donationId = db.collection("donations").document().getId();
        
        Donation donation = new Donation(
                donationId,
                userId,
                foodName,
                quantity,
                new Timestamp(calendar.getTime()),
                latitude,
                longitude,
                manualAddress,
                contact,
                "available",
                new Timestamp(new Date())
        );

        db.collection("donations").document(donationId)
                .set(donation)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Donation posted successfully!", Toast.LENGTH_LONG).show();
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Post Donation");
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearForm() {
        etFoodName.setText("");
        etQuantity.setText("");
        etContact.setText("");
        etManualAddress.setText("");
        tvDeadline.setText("Not set");
        btnSubmit.setEnabled(true);
        btnSubmit.setText("Post Donation");
    }
}

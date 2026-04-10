package com.example.foodwastemanagement.receiver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.foodwastemanagement.R;
import com.example.foodwastemanagement.models.Donation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.ui.IconGenerator;
import android.graphics.Bitmap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ReceiverMapFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap mMap;
    private ProgressBar pbMapLoading;
    private FirebaseFirestore db;
    private final Map<String, Donation> markerDonationMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receiver_map, container, false);
        pbMapLoading = view.findViewById(R.id.pbMapLoading);
        db = FirebaseFirestore.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        enableUserLocation();

        // When user taps info window, take them to DonationDetailActivity
        mMap.setOnInfoWindowClickListener(marker -> {
            Donation donation = markerDonationMap.get(marker.getId());
            if (donation != null) {
                Intent intent = new Intent(getContext(), DonationDetailActivity.class);
                intent.putExtra("DONATION_ID", donation.getDonationId());
                startActivity(intent);
            }
        });

        fetchAvailableDonations();
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                Toast.makeText(getContext(), "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchAvailableDonations() {
        pbMapLoading.setVisibility(View.VISIBLE);

        db.collection("donations")
                .whereEqualTo("status", "available")
                .addSnapshotListener((snapshots, error) -> {
                    pbMapLoading.setVisibility(View.GONE);
                    if (error != null) {
                        Toast.makeText(getContext(), "Failed to load map data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (mMap != null) {
                        mMap.clear();
                        markerDonationMap.clear();

                        if (snapshots != null) {
                            LatLng lastKnownLocation = null;
                            for (var doc : snapshots.getDocuments()) {
                                Donation donation = doc.toObject(Donation.class);
                                if (donation != null && donation.getLatitude() != null && donation.getLongitude() != null) {
                                    donation.setDonationId(doc.getId());
                                    LatLng location = new LatLng(donation.getLatitude(), donation.getLongitude());
                                    
                                // Generate a text-based marker using android-maps-utils
                                    IconGenerator iconFactory = new IconGenerator(getContext());
                                    // Use a customized style or default
                                    iconFactory.setStyle(IconGenerator.STYLE_GREEN);
                                    Bitmap bmp = iconFactory.makeIcon(donation.getFoodName());

                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(location)
                                            .title(donation.getFoodName())
                                            .snippet("Qty: " + donation.getQuantity() + " (Tap to view)")
                                            .icon(BitmapDescriptorFactory.fromBitmap(bmp)));
                                    
                                    if (marker != null) {
                                        markerDonationMap.put(marker.getId(), donation);
                                    }
                                    lastKnownLocation = location;
                                }
                            }
                            
                            // If markers exist and it's the first load, move camera to the last one (as a fallback if user location isn't instantly available)
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 12));
                            }
                        }
                    }
                });
    }
}

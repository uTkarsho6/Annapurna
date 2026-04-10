package com.example.foodwastemanagement.receiver;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.foodwastemanagement.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ReceiverDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.receiver_bottom_navigation);

        // Default fragment on launch
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.receiver_nav_host_fragment, new BrowseSurplusFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_browse) {
                selectedFragment = new BrowseSurplusFragment();
            } else if (itemId == R.id.nav_map) {
                selectedFragment = new ReceiverMapFragment();
            } else if (itemId == R.id.nav_claims) {
                selectedFragment = new ReceiverHistoryFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ReceiverProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.receiver_nav_host_fragment, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }
}

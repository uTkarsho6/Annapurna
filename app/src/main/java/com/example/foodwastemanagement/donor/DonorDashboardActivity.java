package com.example.foodwastemanagement.donor;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.foodwastemanagement.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DonorDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        
        // Initial Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.donor_nav_host_fragment, new DonorHomeFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new DonorHomeFragment();
            } else if (itemId == R.id.nav_history) {
                selectedFragment = new DonorHistoryFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new DonorProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.donor_nav_host_fragment, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }
}

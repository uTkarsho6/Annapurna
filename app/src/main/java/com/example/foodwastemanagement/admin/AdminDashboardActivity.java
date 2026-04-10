package com.example.foodwastemanagement.admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.foodwastemanagement.R;
import com.example.foodwastemanagement.auth.WelcomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.admin_bottom_navigation);

        // Default fragment on launch
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.admin_nav_host_fragment, new AdminUsersFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_users) {
                selectedFragment = new AdminUsersFragment();
            } else if (itemId == R.id.nav_all_donations) {
                selectedFragment = new AdminDonationsFragment();
            } else if (itemId == R.id.nav_analytics) {
                selectedFragment = new AdminAnalyticsFragment();
            } else if (itemId == R.id.nav_admin_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, WelcomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.admin_nav_host_fragment, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }
}

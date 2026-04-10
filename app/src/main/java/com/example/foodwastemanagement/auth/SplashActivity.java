package com.example.foodwastemanagement.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodwastemanagement.R;
import com.example.foodwastemanagement.donor.DonorDashboardActivity;
import com.example.foodwastemanagement.receiver.ReceiverDashboardActivity;
import com.example.foodwastemanagement.admin.AdminDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 2-second delay as per TRD T-50
        new Handler(Looper.getMainLooper()).postDelayed(this::checkAuthState, 2000);
    }

    private void checkAuthState() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        if (currentUser != null) {
            // User is logged in, check role and route (Phase 2 Logic)
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            if ("Admin".equals(role)) {
                                startDashboard(AdminDashboardActivity.class);
                            } else if ("Donor".equals(role)) {
                                startDashboard(DonorDashboardActivity.class);
                            } else {
                                startDashboard(ReceiverDashboardActivity.class);
                            }
                        } else {
                            // Profile missing, fallback to Welcome
                            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Session error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                        finish();
                    });
        } else {
            // No user logged in
            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            finish();
        }
    }

    private void startDashboard(Class<?> dashboardClass) {
        Intent intent = new Intent(SplashActivity.this, dashboardClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

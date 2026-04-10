package com.example.foodwastemanagement.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.foodwastemanagement.R;
import com.example.foodwastemanagement.donor.DonorDashboardActivity;
import com.example.foodwastemanagement.receiver.ReceiverDashboardActivity;
import com.example.foodwastemanagement.admin.AdminDashboardActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLoginSubmit;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLoginSubmit = findViewById(R.id.btnLoginSubmit);
        TextView tvLoginRegister = findViewById(R.id.tvLoginRegister);

        // Note: rgLoginRole has been removed in UI visually because login shouldn't require role selection to authenticate.
        // But if we want to extract it from DB, we just query DB.

        tvLoginRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        btnLoginSubmit.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }

        btnLoginSubmit.setEnabled(false);
        btnLoginSubmit.setText("Signing In...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                saveFcmToken(user.getUid());
                                routeUserBasedOnRole(user.getUid());
                            } else {
                                Toast.makeText(LoginActivity.this, "Please verify your email address.", Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                                btnLoginSubmit.setEnabled(true);
                                btnLoginSubmit.setText("Sign In");
                                startActivity(new Intent(LoginActivity.this, EmailVerificationActivity.class));
                            }
                        }
                    } else {
                        btnLoginSubmit.setEnabled(true);
                        btnLoginSubmit.setText("Sign In");
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void routeUserBasedOnRole(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if ("Admin".equals(role)) {
                            startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                        } else if ("Donor".equals(role)) {
                            startActivity(new Intent(LoginActivity.this, DonorDashboardActivity.class));
                        } else {
                            startActivity(new Intent(LoginActivity.this, ReceiverDashboardActivity.class));
                        }
                        finish();
                    } else {
                        btnLoginSubmit.setEnabled(true);
                        btnLoginSubmit.setText("Sign In");
                        Toast.makeText(LoginActivity.this, "User profile not found in database.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    btnLoginSubmit.setEnabled(true);
                    btnLoginSubmit.setText("Sign In");
                    Toast.makeText(LoginActivity.this, "Failed to get user role: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void saveFcmToken(String uid) {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (token != null && !token.isEmpty()) {
                db.collection("users").document(uid)
                        .update("fcmToken", token);
            }
        });
    }
}

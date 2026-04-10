package com.example.foodwastemanagement.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.foodwastemanagement.R;

public class EmailVerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        Button btnBackToLogin = findViewById(R.id.btnBackToLogin);
        TextView tvResendEmail = findViewById(R.id.tvResendEmail);

        btnBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(EmailVerificationActivity.this, LoginActivity.class));
            finish();
        });

        tvResendEmail.setOnClickListener(v -> {
            // Logic for resending verification email will be added in Phase 2
        });
    }
}

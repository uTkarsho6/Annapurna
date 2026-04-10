package com.example.foodwastemanagement.donor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.foodwastemanagement.AboutUsActivity;
import com.example.foodwastemanagement.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Intent;
import com.example.foodwastemanagement.auth.WelcomeActivity;

public class DonorProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donor_profile, container, false);

        // Show current user email
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView tvEmail = view.findViewById(R.id.tvDonorEmail);
        if (user != null && user.getEmail() != null) {
            tvEmail.setText(user.getEmail());
        }

        // About Us button
        Button btnAboutUs = view.findViewById(R.id.btnAboutUs);
        btnAboutUs.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AboutUsActivity.class)));

        // Logout button
        Button btnLogout = view.findViewById(R.id.btnDonorLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}

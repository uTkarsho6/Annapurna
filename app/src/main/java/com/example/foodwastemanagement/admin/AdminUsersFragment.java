package com.example.foodwastemanagement.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodwastemanagement.R;
import com.example.foodwastemanagement.adapters.UserAdapter;
import com.example.foodwastemanagement.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class AdminUsersFragment extends Fragment {

    private RecyclerView rvAdminUsers;
    private ProgressBar pbAdminUsers;
    private UserAdapter adapter;
    private final List<User> userList = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_users, container, false);

        rvAdminUsers = view.findViewById(R.id.rvAdminUsers);
        pbAdminUsers = view.findViewById(R.id.pbAdminUsers);
        db = FirebaseFirestore.getInstance();

        adapter = new UserAdapter(userList);
        rvAdminUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAdminUsers.setAdapter(adapter);

        fetchUsers();

        return view;
    }

    private void fetchUsers() {
        pbAdminUsers.setVisibility(View.VISIBLE);
        rvAdminUsers.setVisibility(View.GONE);

        db.collection("users")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        pbAdminUsers.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error loading users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    userList.clear();
                    if (snapshots != null) {
                        for (var doc : snapshots.getDocuments()) {
                            User user = doc.toObject(User.class);
                            if (user != null) {
                                user.setUid(doc.getId());
                                userList.add(user);
                            }
                        }
                    }

                    pbAdminUsers.setVisibility(View.GONE);
                    rvAdminUsers.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                });
    }
}

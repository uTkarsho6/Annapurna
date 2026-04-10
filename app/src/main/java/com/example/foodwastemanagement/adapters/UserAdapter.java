package com.example.foodwastemanagement.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodwastemanagement.R;
import com.example.foodwastemanagement.models.User;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvUserName.setText(user.getFullName());
        holder.tvUserEmail.setText(user.getEmail());

        String role = user.getRole();
        holder.tvUserRoleBadge.setText(role != null ? role.toUpperCase() : "UNKNOWN");

        if ("Admin".equalsIgnoreCase(role)) {
            holder.tvUserRoleBadge.setTextColor(Color.parseColor("#1565C0")); // Blue
            holder.tvUserRoleBadge.setBackgroundResource(R.drawable.bg_badge_claimed); // Reusing blue badge outline
        } else if ("Donor".equalsIgnoreCase(role)) {
            holder.tvUserRoleBadge.setTextColor(Color.parseColor("#0d631b")); // Green
            holder.tvUserRoleBadge.setBackgroundResource(R.drawable.bg_badge_available);
        } else {
            holder.tvUserRoleBadge.setTextColor(Color.parseColor("#E65100")); // Orange
            holder.tvUserRoleBadge.setBackgroundResource(R.drawable.bg_badge_available); // You can create an orange badge later if needed
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail, tvUserRoleBadge;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserRoleBadge = itemView.findViewById(R.id.tvUserRoleBadge);
        }
    }
}

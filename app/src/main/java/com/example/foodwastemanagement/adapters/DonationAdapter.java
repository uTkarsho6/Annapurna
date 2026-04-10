package com.example.foodwastemanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodwastemanagement.R;
import com.example.foodwastemanagement.models.Donation;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.DonationViewHolder> {

    public interface OnDonationClickListener {
        void onViewDetails(Donation donation);
    }

    private final List<Donation> donationList;
    private final OnDonationClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());

    public DonationAdapter(List<Donation> donationList, OnDonationClickListener listener) {
        this.donationList = donationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_donation_card, parent, false);
        return new DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationViewHolder holder, int position) {
        Donation donation = donationList.get(position);

        holder.tvFoodName.setText(donation.getFoodName());
        holder.tvQuantity.setText("Quantity: " + donation.getQuantity());
        holder.tvContact.setText("📞 " + donation.getContactNumber());

        if (donation.getPickupTime() != null) {
            holder.tvDeadline.setText("Pickup by: " + dateFormat.format(donation.getPickupTime().toDate()));
        } else {
            holder.tvDeadline.setText("Pickup time: —");
        }

        if ("claimed".equals(donation.getStatus())) {
            holder.tvStatusBadge.setText("● CLAIMED");
            holder.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#1565C0")); // Blue Dark
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_claimed);
            holder.btnViewDetails.setText("View Claim");
        } else {
            holder.tvStatusBadge.setText("● AVAILABLE");
            holder.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#0d631b")); // Green Dark
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_available);
            holder.btnViewDetails.setText("View Details");
        }

        holder.btnViewDetails.setOnClickListener(v -> listener.onViewDetails(donation));
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    public static class DonationViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvQuantity, tvContact, tvDeadline, tvStatusBadge;
        MaterialButton btnViewDetails;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvContact = itemView.findViewById(R.id.tvContact);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}

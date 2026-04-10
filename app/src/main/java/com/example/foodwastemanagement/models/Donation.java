package com.example.foodwastemanagement.models;

import com.google.firebase.Timestamp;
import java.io.Serializable;

public class Donation implements Serializable {
    private String donationId;
    private String donorUid;
    private String foodName;
    private String quantity;
    private Timestamp pickupTime;
    private Double latitude;
    private Double longitude;
    private String manualAddress;
    private String contactNumber;
    private String status; // 'available' | 'claimed' | 'expired'
    private String claimedBy;
    private Timestamp claimedAt;
    private Timestamp createdAt;

    // Required empty constructor for Firestore
    public Donation() {}

    public Donation(String donationId, String donorUid, String foodName, String quantity, 
                    Timestamp pickupTime, Double latitude, Double longitude, 
                    String manualAddress, String contactNumber, String status, 
                    Timestamp createdAt) {
        this.donationId = donationId;
        this.donorUid = donorUid;
        this.foodName = foodName;
        this.quantity = quantity;
        this.pickupTime = pickupTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.manualAddress = manualAddress;
        this.contactNumber = contactNumber;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getDonationId() { return donationId; }
    public void setDonationId(String donationId) { this.donationId = donationId; }

    public String getDonorUid() { return donorUid; }
    public void setDonorUid(String donorUid) { this.donorUid = donorUid; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public Timestamp getPickupTime() { return pickupTime; }
    public void setPickupTime(Timestamp pickupTime) { this.pickupTime = pickupTime; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getManualAddress() { return manualAddress; }
    public void setManualAddress(String manualAddress) { this.manualAddress = manualAddress; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getClaimedBy() { return claimedBy; }
    public void setClaimedBy(String claimedBy) { this.claimedBy = claimedBy; }

    public Timestamp getClaimedAt() { return claimedAt; }
    public void setClaimedAt(Timestamp claimedAt) { this.claimedAt = claimedAt; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

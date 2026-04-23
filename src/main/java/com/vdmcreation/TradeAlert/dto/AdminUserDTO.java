package com.vdmcreation.TradeAlert.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AdminUserDTO {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean verified;
    private String subscriptionId;
    private LocalDateTime createdDate;
    private List<String> roles;
    private int totalAlerts;
    private boolean hasActiveSubscription;
    private String currentPlan;

    public AdminUserDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public String getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public int getTotalAlerts() { return totalAlerts; }
    public void setTotalAlerts(int totalAlerts) { this.totalAlerts = totalAlerts; }

    public boolean isHasActiveSubscription() { return hasActiveSubscription; }
    public void setHasActiveSubscription(boolean hasActiveSubscription) { this.hasActiveSubscription = hasActiveSubscription; }

    public String getCurrentPlan() { return currentPlan; }
    public void setCurrentPlan(String currentPlan) { this.currentPlan = currentPlan; }
}
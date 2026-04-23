package com.vdmcreation.TradeAlert.dto;

public class UserAlertStatsDTO {
    
    private Long userId;
    private String subscriptionId;
    private boolean subscriptionActive;
    private int totalAlertCount;
    private int remainingAlertCount;
    private int maxAlertsAllowed;
    private String planType;

    public UserAlertStatsDTO() {}

    public UserAlertStatsDTO(Long userId, String subscriptionId, boolean subscriptionActive, 
                           int totalAlertCount, int remainingAlertCount, int maxAlertsAllowed, String planType) {
        this.userId = userId;
        this.subscriptionId = subscriptionId;
        this.subscriptionActive = subscriptionActive;
        this.totalAlertCount = totalAlertCount;
        this.remainingAlertCount = remainingAlertCount;
        this.maxAlertsAllowed = maxAlertsAllowed;
        this.planType = planType;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }

    public boolean isSubscriptionActive() { return subscriptionActive; }
    public void setSubscriptionActive(boolean subscriptionActive) { this.subscriptionActive = subscriptionActive; }

    public int getTotalAlertCount() { return totalAlertCount; }
    public void setTotalAlertCount(int totalAlertCount) { this.totalAlertCount = totalAlertCount; }

    public int getRemainingAlertCount() { return remainingAlertCount; }
    public void setRemainingAlertCount(int remainingAlertCount) { this.remainingAlertCount = remainingAlertCount; }

    public int getMaxAlertsAllowed() { return maxAlertsAllowed; }
    public void setMaxAlertsAllowed(int maxAlertsAllowed) { this.maxAlertsAllowed = maxAlertsAllowed; }

    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }
}
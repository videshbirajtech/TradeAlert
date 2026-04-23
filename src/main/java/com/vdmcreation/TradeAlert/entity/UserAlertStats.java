package com.vdmcreation.TradeAlert.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_alert_stats")
public class UserAlertStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "subscription_id")
    private String subscriptionId;

    @Column(name = "is_subscription_active", nullable = false)
    private boolean subscriptionActive = false;

    @Column(name = "total_alert_count", nullable = false)
    private int totalAlertCount = 0;

    @Column(name = "remaining_alert_count", nullable = false)
    private int remainingAlertCount = 0;

    @Column(name = "max_alerts_allowed", nullable = false)
    private int maxAlertsAllowed = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UserAlertStats() {}

    public UserAlertStats(User user) {
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Helper method to update remaining alerts
    public void updateRemainingAlerts() {
        this.remainingAlertCount = Math.max(0, this.maxAlertsAllowed - this.totalAlertCount);
    }
}
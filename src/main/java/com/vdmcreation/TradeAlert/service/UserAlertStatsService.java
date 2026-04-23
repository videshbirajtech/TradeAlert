package com.vdmcreation.TradeAlert.service;

import com.vdmcreation.TradeAlert.entity.UserAlertStats;

public interface UserAlertStatsService {
    
    UserAlertStats getUserAlertStats(Long userId);
    
    UserAlertStats getUserAlertStatsByEmail(String email);
    
    UserAlertStats createOrUpdateUserAlertStats(Long userId);
    
    boolean canCreateAlert(Long userId);
    
    void incrementAlertCount(Long userId);
    
    void decrementAlertCount(Long userId);
    
    void updateSubscriptionInfo(Long userId, String subscriptionId, boolean isActive);
}
package com.vdmcreation.TradeAlert.service.impl;

import com.vdmcreation.TradeAlert.entity.Subscription;
import com.vdmcreation.TradeAlert.entity.User;
import com.vdmcreation.TradeAlert.entity.UserAlertStats;
import com.vdmcreation.TradeAlert.enums.PlanType;
import com.vdmcreation.TradeAlert.repository.PriceAlertRepository;
import com.vdmcreation.TradeAlert.repository.SubscriptionRepository;
import com.vdmcreation.TradeAlert.repository.UserAlertStatsRepository;
import com.vdmcreation.TradeAlert.repository.UserRepository;
import com.vdmcreation.TradeAlert.service.UserAlertStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserAlertStatsServiceImpl implements UserAlertStatsService {

    @Autowired
    private UserAlertStatsRepository userAlertStatsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PriceAlertRepository priceAlertRepository;

    @Override
    public UserAlertStats getUserAlertStats(Long userId) {
        Optional<UserAlertStats> stats = userAlertStatsRepository.findByUserId(userId);
        if (stats.isPresent()) {
            return stats.get();
        }
        return createOrUpdateUserAlertStats(userId);
    }

    @Override
    public UserAlertStats getUserAlertStatsByEmail(String email) {
        Optional<UserAlertStats> stats = userAlertStatsRepository.findByUserEmail(email);
        if (stats.isPresent()) {
            return stats.get();
        }
        
        // Find user by email and create stats
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return createOrUpdateUserAlertStats(user.get().getId());
        }
        
        throw new RuntimeException("User not found with email: " + email);
    }

    @Override
    public UserAlertStats createOrUpdateUserAlertStats(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        User user = userOpt.get();
        UserAlertStats stats = userAlertStatsRepository.findByUserId(userId)
                .orElse(new UserAlertStats(user));

        // Get current alert count
        int currentAlertCount = priceAlertRepository.countByUserId(userId);
        stats.setTotalAlertCount(currentAlertCount);

        // Check for active subscription
        Optional<Subscription> activeSubscription = subscriptionRepository
                .findByUserIdAndEndDateAfter(userId, LocalDateTime.now());

        if (activeSubscription.isPresent()) {
            Subscription subscription = activeSubscription.get();
            stats.setSubscriptionId(subscription.getSubscriptionId());
            stats.setSubscriptionActive(true);
            stats.setMaxAlertsAllowed(subscription.getPlanType().getMaxAlerts());
        } else {
            stats.setSubscriptionId(null);
            stats.setSubscriptionActive(false);
            stats.setMaxAlertsAllowed(0); // No alerts allowed without active subscription
        }

        stats.updateRemainingAlerts();
        return userAlertStatsRepository.save(stats);
    }

    @Override
    public boolean canCreateAlert(Long userId) {
        UserAlertStats stats = getUserAlertStats(userId);
        return stats.isSubscriptionActive() && stats.getRemainingAlertCount() > 0;
    }

    @Override
    public void incrementAlertCount(Long userId) {
        UserAlertStats stats = getUserAlertStats(userId);
        stats.setTotalAlertCount(stats.getTotalAlertCount() + 1);
        stats.updateRemainingAlerts();
        userAlertStatsRepository.save(stats);
    }

    @Override
    public void decrementAlertCount(Long userId) {
        UserAlertStats stats = getUserAlertStats(userId);
        if (stats.getTotalAlertCount() > 0) {
            stats.setTotalAlertCount(stats.getTotalAlertCount() - 1);
            stats.updateRemainingAlerts();
            userAlertStatsRepository.save(stats);
        }
    }

    @Override
    public void updateSubscriptionInfo(Long userId, String subscriptionId, boolean isActive) {
        UserAlertStats stats = getUserAlertStats(userId);
        stats.setSubscriptionId(subscriptionId);
        stats.setSubscriptionActive(isActive);
        
        if (isActive) {
            // Get subscription details to set max alerts
            Optional<Subscription> subscription = subscriptionRepository.findBySubscriptionId(subscriptionId);
            if (subscription.isPresent()) {
                stats.setMaxAlertsAllowed(subscription.get().getPlanType().getMaxAlerts());
            }
        } else {
            stats.setMaxAlertsAllowed(0);
        }
        
        stats.updateRemainingAlerts();
        userAlertStatsRepository.save(stats);
    }
}
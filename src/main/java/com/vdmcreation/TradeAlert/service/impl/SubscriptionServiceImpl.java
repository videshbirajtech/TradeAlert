package com.vdmcreation.TradeAlert.service.impl;

import com.vdmcreation.TradeAlert.dto.PurchaseRequestDTO;
import com.vdmcreation.TradeAlert.dto.SubscriptionDTO;
import com.vdmcreation.TradeAlert.entity.Subscription;
import com.vdmcreation.TradeAlert.entity.User;
import com.vdmcreation.TradeAlert.enums.PlanType;
import com.vdmcreation.TradeAlert.repository.SubscriptionRepository;
import com.vdmcreation.TradeAlert.repository.UserRepository;
import com.vdmcreation.TradeAlert.service.SubscriptionService;
import com.vdmcreation.TradeAlert.service.UserAlertStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    
    @Autowired
    private UserAlertStatsService userAlertStatsService;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository,
                                   UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public SubscriptionDTO purchase(PurchaseRequestDTO request) {
        User user = getUser(request.getEmail());

        PlanType planType;
        try {
            planType = PlanType.valueOf(request.getPlanType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid plan type. Valid values: MONTHLY, QUARTERLY, YEARLY");
        }

        // Deactivate any existing active subscriptions by setting their end date to now
        deactivateExistingSubscriptions(user);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusMonths(planType.getMonths());

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlanType(planType);
        subscription.setPrice(planType.getPrice());
        subscription.setStartDate(now);
        subscription.setEndDate(endDate);

        Subscription saved = subscriptionRepository.save(subscription);

        // Update user's latest subscriptionId
        user.setSubscriptionId(saved.getSubscriptionId());
        userRepository.save(user);

        // Update user alert stats with new subscription info
        userAlertStatsService.updateSubscriptionInfo(user.getId(), saved.getSubscriptionId(), true);

        return toDTO(saved);
    }

    private void deactivateExistingSubscriptions(User user) {
        // Find all active subscriptions for this user
        List<Subscription> activeSubscriptions = subscriptionRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .filter(s -> s.getEndDate().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());

        // Set their end date to now (effectively deactivating them)
        LocalDateTime now = LocalDateTime.now();
        for (Subscription subscription : activeSubscriptions) {
            subscription.setEndDate(now);
            subscriptionRepository.save(subscription);
        }
    }

    @Override
    public List<SubscriptionDTO> getSubscriptionsByEmail(String email) {
        User user = getUser(email);
        return subscriptionRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionDTO getActiveSubscription(String email) {
        User user = getUser(email);
        return subscriptionRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .filter(s -> s.getEndDate().isAfter(LocalDateTime.now()))
                .findFirst()
                .map(this::toDTO)
                .orElse(null);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: " + email));
    }

    private SubscriptionDTO toDTO(Subscription s) {
        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setId(s.getId());
        dto.setSubscriptionId(s.getSubscriptionId());
        dto.setPlanType(s.getPlanType().name());
        dto.setPrice(s.getPrice());
        dto.setStartDate(s.getStartDate());
        dto.setEndDate(s.getEndDate());
        dto.setCreatedAt(s.getCreatedAt());
        dto.setActive(s.getEndDate().isAfter(LocalDateTime.now()));
        return dto;
    }
}

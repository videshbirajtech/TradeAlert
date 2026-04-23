package com.vdmcreation.TradeAlert.controller;

import com.vdmcreation.TradeAlert.dto.ApiResponseDTO;
import com.vdmcreation.TradeAlert.dto.UserAlertStatsDTO;
import com.vdmcreation.TradeAlert.entity.Subscription;
import com.vdmcreation.TradeAlert.entity.UserAlertStats;
import com.vdmcreation.TradeAlert.repository.SubscriptionRepository;
import com.vdmcreation.TradeAlert.service.UserAlertStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-alert-stats")
@CrossOrigin(origins = "*")
public class UserAlertStatsController {

    @Autowired
    private UserAlertStatsService userAlertStatsService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDTO<UserAlertStatsDTO>> getUserAlertStats(@PathVariable Long userId) {
        try {
            UserAlertStats stats = userAlertStatsService.getUserAlertStats(userId);
            UserAlertStatsDTO dto = convertToDTO(stats);
            
            return ResponseEntity.ok(new ApiResponseDTO<>(
                "User alert statistics retrieved successfully", 
                dto, 
                true
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO<>(
                    "Error retrieving user alert statistics: " + e.getMessage(), 
                    null, 
                    false
                ));
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponseDTO<UserAlertStatsDTO>> getUserAlertStatsByEmail(@PathVariable String email) {
        try {
            UserAlertStats stats = userAlertStatsService.getUserAlertStatsByEmail(email);
            UserAlertStatsDTO dto = convertToDTO(stats);
            
            return ResponseEntity.ok(new ApiResponseDTO<>(
                "User alert statistics retrieved successfully", 
                dto, 
                true
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO<>(
                    "Error retrieving user alert statistics: " + e.getMessage(), 
                    null, 
                    false
                ));
        }
    }

    @PostMapping("/refresh/{userId}")
    public ResponseEntity<ApiResponseDTO<UserAlertStatsDTO>> refreshUserAlertStats(@PathVariable Long userId) {
        try {
            UserAlertStats stats = userAlertStatsService.createOrUpdateUserAlertStats(userId);
            UserAlertStatsDTO dto = convertToDTO(stats);
            
            return ResponseEntity.ok(new ApiResponseDTO<>(
                "User alert statistics refreshed successfully", 
                dto, 
                true
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO<>(
                    "Error refreshing user alert statistics: " + e.getMessage(), 
                    null, 
                    false
                ));
        }
    }

    @GetMapping("/can-create-alert/{userId}")
    public ResponseEntity<ApiResponseDTO<Boolean>> canCreateAlert(@PathVariable Long userId) {
        try {
            boolean canCreate = userAlertStatsService.canCreateAlert(userId);
            
            return ResponseEntity.ok(new ApiResponseDTO<>(
                canCreate ? "User can create more alerts" : "User has reached alert limit", 
                canCreate, 
                true
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO<>(
                    "Error checking alert creation permission: " + e.getMessage(), 
                    false, 
                    false
                ));
        }
    }

    private UserAlertStatsDTO convertToDTO(UserAlertStats stats) {
        String planType = null;
        
        if (stats.getSubscriptionId() != null) {
            Optional<Subscription> subscription = subscriptionRepository.findBySubscriptionId(stats.getSubscriptionId());
            if (subscription.isPresent()) {
                planType = subscription.get().getPlanType().name();
            }
        }
        
        return new UserAlertStatsDTO(
            stats.getUser().getId(),
            stats.getSubscriptionId(),
            stats.isSubscriptionActive(),
            stats.getTotalAlertCount(),
            stats.getRemainingAlertCount(),
            stats.getMaxAlertsAllowed(),
            planType
        );
    }
}
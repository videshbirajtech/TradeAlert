package com.vdmcreation.TradeAlert.controller;

import com.vdmcreation.TradeAlert.dto.AlertDashboardDTO;
import com.vdmcreation.TradeAlert.dto.ApiResponseDTO;
import com.vdmcreation.TradeAlert.dto.PriceAlertDTO;
import com.vdmcreation.TradeAlert.dto.PriceAlertRequestDTO;
import com.vdmcreation.TradeAlert.dto.SubscriptionDTO;
import com.vdmcreation.TradeAlert.dto.UserAlertStatsDTO;
import com.vdmcreation.TradeAlert.entity.Subscription;
import com.vdmcreation.TradeAlert.entity.User;
import com.vdmcreation.TradeAlert.entity.UserAlertStats;
import com.vdmcreation.TradeAlert.repository.SubscriptionRepository;
import com.vdmcreation.TradeAlert.repository.UserRepository;
import com.vdmcreation.TradeAlert.service.PriceAlertService;
import com.vdmcreation.TradeAlert.service.UserAlertStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class PriceAlertController {

    private final PriceAlertService alertService;
    
    @Autowired
    private UserAlertStatsService userAlertStatsService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public PriceAlertController(PriceAlertService alertService) {
        this.alertService = alertService;
    }

    // POST /api/alerts
    @PostMapping
    public ResponseEntity<ApiResponseDTO<PriceAlertDTO>> createAlert(@RequestBody PriceAlertRequestDTO request) {
        try {
            PriceAlertDTO alert = alertService.createAlert(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Alert created successfully", alert, true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDTO<>(e.getMessage(), null, false));
        }
    }

    // GET /api/alerts?email=user@example.com
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PriceAlertDTO>>> getAlerts(@RequestParam String email) {
        try {
            List<PriceAlertDTO> alerts = alertService.getAlertsByEmail(email);
            return ResponseEntity.ok(new ApiResponseDTO<>("Alerts retrieved successfully", alerts, true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO<>(e.getMessage(), null, false));
        }
    }

    // GET /api/alerts/dashboard?email=user@example.com
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponseDTO<AlertDashboardDTO>> getAlertDashboard(@RequestParam String email) {
        try {
            System.out.println("Dashboard request for email: " + email);
            
            List<PriceAlertDTO> alerts = alertService.getAlertsByEmail(email);
            System.out.println("Found " + alerts.size() + " alerts for user: " + email);
            
            UserAlertStats stats = userAlertStatsService.getUserAlertStatsByEmail(email);
            System.out.println("User stats - Total alerts: " + stats.getTotalAlertCount() + 
                             ", Remaining: " + stats.getRemainingAlertCount() + 
                             ", Max allowed: " + stats.getMaxAlertsAllowed());
            
            UserAlertStatsDTO statsDTO = convertToStatsDTO(stats);
            
            AlertDashboardDTO dashboard = new AlertDashboardDTO(alerts, statsDTO);
            
            return ResponseEntity.ok(new ApiResponseDTO<>("Dashboard data retrieved successfully", dashboard, true));
        } catch (Exception e) {
            System.err.println("Dashboard error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO<>(e.getMessage(), null, false));
        }
    }

    // PUT /api/alerts/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PriceAlertDTO>> updateAlert(@PathVariable Long id,
                                                      @RequestBody PriceAlertRequestDTO request) {
        try {
            PriceAlertDTO alert = alertService.updateAlert(id, request);
            return ResponseEntity.ok(new ApiResponseDTO<>("Alert updated successfully", alert, true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDTO<>(e.getMessage(), null, false));
        }
    }

    // DELETE /api/alerts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> deleteAlert(@PathVariable Long id) {
        try {
            alertService.deleteAlert(id);
            return ResponseEntity.ok(new ApiResponseDTO<>("Alert deleted successfully", "Deleted", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDTO<>(e.getMessage(), null, false));
        }
    }

    @GetMapping("/debug/{email}")
    public ResponseEntity<ApiResponseDTO<String>> debugUser(@PathVariable String email) {
        try {
            StringBuilder debug = new StringBuilder();
            
            // Check if user exists
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (!userOpt.isPresent()) {
                return ResponseEntity.ok(new ApiResponseDTO<>("User not found with email: " + email, null, false));
            }
            
            User user = userOpt.get();
            debug.append("User found: ").append(user.getFirstName()).append(" ").append(user.getLastName()).append("\n");
            debug.append("User ID: ").append(user.getId()).append("\n");
            debug.append("User subscription ID: ").append(user.getSubscriptionId()).append("\n");
            
            // Check alerts
            List<PriceAlertDTO> alerts = alertService.getAlertsByEmail(email);
            debug.append("Alerts found: ").append(alerts.size()).append("\n");
            
            // Check user alert stats
            try {
                UserAlertStats stats = userAlertStatsService.getUserAlertStatsByEmail(email);
                debug.append("Alert stats found - Total: ").append(stats.getTotalAlertCount())
                     .append(", Remaining: ").append(stats.getRemainingAlertCount())
                     .append(", Max: ").append(stats.getMaxAlertsAllowed())
                     .append(", Active: ").append(stats.isSubscriptionActive()).append("\n");
            } catch (Exception e) {
                debug.append("Error getting alert stats: ").append(e.getMessage()).append("\n");
            }
            
            // Check subscriptions
            List<SubscriptionDTO> subscriptions = subscriptionRepository.findByUserOrderByCreatedAtDesc(user)
                    .stream()
                    .map(s -> {
                        SubscriptionDTO dto = new SubscriptionDTO();
                        dto.setId(s.getId());
                        dto.setSubscriptionId(s.getSubscriptionId());
                        dto.setPlanType(s.getPlanType().name());
                        dto.setActive(s.getEndDate().isAfter(LocalDateTime.now()));
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            debug.append("Subscriptions found: ").append(subscriptions.size()).append("\n");
            for (SubscriptionDTO sub : subscriptions) {
                debug.append("  - ").append(sub.getPlanType()).append(" (").append(sub.getSubscriptionId()).append(") Active: ").append(sub.isActive()).append("\n");
            }
            
            return ResponseEntity.ok(new ApiResponseDTO<>("Debug info retrieved", debug.toString(), true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO<>("Debug error: " + e.getMessage(), null, false));
        }
    }

    private UserAlertStatsDTO convertToStatsDTO(UserAlertStats stats) {
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
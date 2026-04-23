package com.vdmcreation.TradeAlert.controller;

import com.vdmcreation.TradeAlert.dto.ApiResponseDTO;
import com.vdmcreation.TradeAlert.entity.User;
import com.vdmcreation.TradeAlert.repository.UserAlertStatsRepository;
import com.vdmcreation.TradeAlert.repository.UserRepository;
import com.vdmcreation.TradeAlert.service.UserAlertStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAlertStatsRepository userAlertStatsRepository;

    @Autowired
    private UserAlertStatsService userAlertStatsService;

    @PostMapping("/initialize-alert-stats")
    public ResponseEntity<ApiResponseDTO<String>> initializeAlertStats() {
        try {
            List<User> allUsers = userRepository.findAll();
            int initialized = 0;

            for (User user : allUsers) {
                if (!userAlertStatsRepository.existsByUserId(user.getId())) {
                    userAlertStatsService.createOrUpdateUserAlertStats(user.getId());
                    initialized++;
                }
            }

            String message = "Initialized alert stats for " + initialized + " users out of " + allUsers.size() + " total users.";
            return ResponseEntity.ok(new ApiResponseDTO<>(message, message, true));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ApiResponseDTO<>("Error initializing alert stats: " + e.getMessage(), null, false));
        }
    }

    @PostMapping("/refresh-all-alert-stats")
    public ResponseEntity<ApiResponseDTO<String>> refreshAllAlertStats() {
        try {
            List<User> allUsers = userRepository.findAll();
            int refreshed = 0;

            for (User user : allUsers) {
                userAlertStatsService.createOrUpdateUserAlertStats(user.getId());
                refreshed++;
            }

            String message = "Refreshed alert stats for " + refreshed + " users.";
            return ResponseEntity.ok(new ApiResponseDTO<>(message, message, true));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ApiResponseDTO<>("Error refreshing alert stats: " + e.getMessage(), null, false));
        }
    }

    @PostMapping("/create-test-alert")
    public ResponseEntity<ApiResponseDTO<String>> createTestAlert(@RequestParam String email) {
        try {
            // This is just for testing - you would normally use the proper alert creation endpoint
            // But this helps debug if the issue is with alert creation or retrieval
            
            return ResponseEntity.ok(new ApiResponseDTO<>("Test endpoint created. Use the regular /api/alerts endpoint to create alerts.", null, true));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ApiResponseDTO<>("Error: " + e.getMessage(), null, false));
        }
    }
}
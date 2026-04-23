package com.vdmcreation.TradeAlert.controller;

import com.vdmcreation.TradeAlert.dto.ApiResponseDTO;
import com.vdmcreation.TradeAlert.dto.UserProfileDTO;
import com.vdmcreation.TradeAlert.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserProfileController {

    @Autowired
    private UserRoleService userRoleService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponseDTO<UserProfileDTO>> getUserProfile(@RequestParam String email) {
        try {
            UserProfileDTO profile = userRoleService.getUserProfile(email);
            return ResponseEntity.ok(new ApiResponseDTO<>("User profile retrieved successfully", profile, true));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ApiResponseDTO<>("Error retrieving user profile: " + e.getMessage(), null, false));
        }
    }

    @PostMapping("/initialize-roles")
    public ResponseEntity<ApiResponseDTO<String>> initializeRoles() {
        try {
            userRoleService.initializeDefaultRoles();
            return ResponseEntity.ok(new ApiResponseDTO<>("Default roles initialized successfully", "Success", true));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ApiResponseDTO<>("Error initializing roles: " + e.getMessage(), null, false));
        }
    }
}
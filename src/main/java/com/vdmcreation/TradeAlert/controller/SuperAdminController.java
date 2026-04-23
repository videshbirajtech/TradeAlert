package com.vdmcreation.TradeAlert.controller;

import com.vdmcreation.TradeAlert.dto.AdminUserDTO;
import com.vdmcreation.TradeAlert.dto.ApiResponseDTO;
import com.vdmcreation.TradeAlert.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin")
@CrossOrigin(origins = "*")
public class SuperAdminController {

    @Autowired
    private UserRoleService userRoleService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponseDTO<List<AdminUserDTO>>> getAllUsers(@RequestParam String adminEmail) {
        try {
            // Check if the requesting user is a super admin
            if (!userRoleService.isSuperUser(adminEmail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDTO<>("Access denied. Super admin role required.", null, false));
            }

            List<AdminUserDTO> users = userRoleService.getAllUsersForAdmin();
            return ResponseEntity.ok(new ApiResponseDTO<>("Users retrieved successfully", users, true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO<>("Error retrieving users: " + e.getMessage(), null, false));
        }
    }
}
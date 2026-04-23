package com.vdmcreation.TradeAlert.service.impl;

import com.vdmcreation.TradeAlert.dto.AdminUserDTO;
import com.vdmcreation.TradeAlert.dto.UserProfileDTO;
import com.vdmcreation.TradeAlert.entity.Subscription;
import com.vdmcreation.TradeAlert.entity.User;
import com.vdmcreation.TradeAlert.entity.UserAlertStats;
import com.vdmcreation.TradeAlert.entity.UserRoles;
import com.vdmcreation.TradeAlert.enums.UserRole;
import com.vdmcreation.TradeAlert.repository.SubscriptionRepository;
import com.vdmcreation.TradeAlert.repository.UserAlertStatsRepository;
import com.vdmcreation.TradeAlert.repository.UserRepository;
import com.vdmcreation.TradeAlert.repository.UserRolesRepository;
import com.vdmcreation.TradeAlert.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRolesRepository userRolesRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserAlertStatsRepository userAlertStatsRepository;

    @Override
    public UserProfileDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + email));

        List<String> roles = getUserRoles(email);
        UserRole highestRole = getHighestRole(email);
        boolean isSuperUser = highestRole == UserRole.SUPER_USER;

        return new UserProfileDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.isVerified(),
                user.getProfilePhoto(),
                user.getSubscriptionId(),
                user.getCreatedDate(),
                roles,
                highestRole.name(),
                isSuperUser
        );
    }

    @Override
    public List<String> getUserRoles(String email) {
        return userRolesRepository.findByUserEmail(email)
                .stream()
                .map(userRole -> userRole.getRole().name())
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasRole(String email, UserRole role) {
        return userRolesRepository.existsByUserEmailAndRole(email, role);
    }

    @Override
    public boolean isSuperUser(String email) {
        return hasRole(email, UserRole.SUPER_USER);
    }

    @Override
    public UserRole getHighestRole(String email) {
        List<UserRoles> userRoles = userRolesRepository.findByUserEmail(email);
        
        // Check for SUPER_USER first (highest priority)
        if (userRoles.stream().anyMatch(ur -> ur.getRole() == UserRole.SUPER_USER)) {
            return UserRole.SUPER_USER;
        }
        
        // Check for USER role
        if (userRoles.stream().anyMatch(ur -> ur.getRole() == UserRole.USER)) {
            return UserRole.USER;
        }
        
        // Default to USER if no roles found (shouldn't happen)
        return UserRole.USER;
    }

    @Override
    public void assignUserRoleOnSignup(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + email));

        // Only assign USER role if user doesn't have any roles
        if (!userRolesRepository.existsByUserIdAndRole(user.getId(), UserRole.USER)) {
            UserRoles userRole = new UserRoles(user, UserRole.USER, "SIGNUP");
            userRolesRepository.save(userRole);
        }
    }

    @Override
    public List<AdminUserDTO> getAllUsersForAdmin() {
        List<User> allUsers = userRepository.findAll();
        
        return allUsers.stream().map(user -> {
            AdminUserDTO dto = new AdminUserDTO();
            dto.setId(user.getId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            dto.setVerified(user.isVerified());
            dto.setSubscriptionId(user.getSubscriptionId());
            dto.setCreatedDate(user.getCreatedDate());
            
            // Get user roles
            List<String> roles = userRolesRepository.findByUserId(user.getId())
                    .stream()
                    .map(ur -> ur.getRole().name())
                    .collect(Collectors.toList());
            dto.setRoles(roles);
            
            // Get alert count
            Optional<UserAlertStats> alertStats = userAlertStatsRepository.findByUserId(user.getId());
            dto.setTotalAlerts(alertStats.map(UserAlertStats::getTotalAlertCount).orElse(0));
            
            // Check active subscription
            Optional<Subscription> activeSubscription = subscriptionRepository
                    .findByUserIdAndEndDateAfter(user.getId(), LocalDateTime.now());
            dto.setHasActiveSubscription(activeSubscription.isPresent());
            dto.setCurrentPlan(activeSubscription.map(s -> s.getPlanType().name()).orElse("None"));
            
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void initializeDefaultRoles() {
        // Assign USER role to all users who don't have any roles
        List<User> allUsers = userRepository.findAll();
        
        for (User user : allUsers) {
            List<UserRoles> existingRoles = userRolesRepository.findByUserId(user.getId());
            if (existingRoles.isEmpty()) {
                // Assign default USER role
                UserRoles userRole = new UserRoles(user, UserRole.USER, "SYSTEM");
                userRolesRepository.save(userRole);
            }
        }
    }
}
package com.vdmcreation.TradeAlert.service;

import com.vdmcreation.TradeAlert.dto.AdminUserDTO;
import com.vdmcreation.TradeAlert.dto.UserProfileDTO;
import com.vdmcreation.TradeAlert.enums.UserRole;

import java.util.List;

public interface UserRoleService {
    
    UserProfileDTO getUserProfile(String email);
    
    List<String> getUserRoles(String email);
    
    UserRole getHighestRole(String email);
    
    boolean hasRole(String email, UserRole role);
    
    boolean isSuperUser(String email);
    
    List<AdminUserDTO> getAllUsersForAdmin();
    
    void initializeDefaultRoles();
    
    void assignUserRoleOnSignup(String email);
}
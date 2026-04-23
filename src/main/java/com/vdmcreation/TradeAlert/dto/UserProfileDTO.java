package com.vdmcreation.TradeAlert.dto;

import java.time.LocalDateTime;
import java.util.List;

public class UserProfileDTO {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean verified;
    private String profilePhoto;
    private String subscriptionId;
    private LocalDateTime createdDate;
    private List<String> roles;
    private String highestRole;
    private boolean isSuperUser;

    public UserProfileDTO() {}

    public UserProfileDTO(Long id, String firstName, String lastName, String email, 
                         boolean verified, String profilePhoto, String subscriptionId, 
                         LocalDateTime createdDate, List<String> roles, String highestRole, boolean isSuperUser) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.verified = verified;
        this.profilePhoto = profilePhoto;
        this.subscriptionId = subscriptionId;
        this.createdDate = createdDate;
        this.roles = roles;
        this.highestRole = highestRole;
        this.isSuperUser = isSuperUser;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }

    public String getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public String getHighestRole() { return highestRole; }
    public void setHighestRole(String highestRole) { this.highestRole = highestRole; }

    public boolean isSuperUser() { return isSuperUser; }
    public void setSuperUser(boolean superUser) { isSuperUser = superUser; }
}
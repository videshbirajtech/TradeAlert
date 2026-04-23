package com.vdmcreation.TradeAlert.dto;

import java.time.LocalDateTime;

public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePhoto;
    private LocalDateTime createdDate;

    public UserDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}

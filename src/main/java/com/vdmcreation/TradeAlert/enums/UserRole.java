package com.vdmcreation.TradeAlert.enums;

public enum UserRole {
    USER("Regular User"),
    SUPER_USER("Super Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
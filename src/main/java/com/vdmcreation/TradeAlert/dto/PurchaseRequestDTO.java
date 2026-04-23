package com.vdmcreation.TradeAlert.dto;

public class PurchaseRequestDTO {

    private String email;
    private String planType; // MONTHLY, QUARTERLY, YEARLY

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }
}

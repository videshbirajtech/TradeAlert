package com.vdmcreation.TradeAlert.dto;

public class VerifyOtpRequestDTO {

    private String email;
    private Integer otp;

    public VerifyOtpRequestDTO() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getOtp() { return otp; }
    public void setOtp(Integer otp) { this.otp = otp; }
}

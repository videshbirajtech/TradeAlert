package com.vdmcreation.TradeAlert.dto;

public class VerifySignupOtpRequestDTO {

    private String email;
    private Integer otp;

    public VerifySignupOtpRequestDTO() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getOtp() { return otp; }
    public void setOtp(Integer otp) { this.otp = otp; }
}

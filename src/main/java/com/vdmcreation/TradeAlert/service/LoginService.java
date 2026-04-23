package com.vdmcreation.TradeAlert.service;

import com.vdmcreation.TradeAlert.dto.ApiResponseDTO;
import com.vdmcreation.TradeAlert.dto.LoginRequestDTO;
import com.vdmcreation.TradeAlert.dto.SignupRequestDTO;
import com.vdmcreation.TradeAlert.dto.VerifyOtpRequestDTO;
import com.vdmcreation.TradeAlert.dto.VerifySignupOtpRequestDTO;

public interface LoginService {

    ApiResponseDTO login(LoginRequestDTO request);

    ApiResponseDTO verifyOtp(VerifyOtpRequestDTO request);

    ApiResponseDTO signup(SignupRequestDTO request);

    ApiResponseDTO verifySignupOtp(VerifySignupOtpRequestDTO request);
}

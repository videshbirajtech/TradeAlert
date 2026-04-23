package com.vdmcreation.TradeAlert.service;

import com.vdmcreation.TradeAlert.dto.ApiResponseDTO;
import com.vdmcreation.TradeAlert.dto.LoginRequestDTO;
import com.vdmcreation.TradeAlert.dto.SignupRequestDTO;
import com.vdmcreation.TradeAlert.dto.VerifyOtpRequestDTO;
import com.vdmcreation.TradeAlert.dto.VerifySignupOtpRequestDTO;

public interface LoginService {

    ApiResponseDTO<String> login(LoginRequestDTO request);

    ApiResponseDTO<String> verifyOtp(VerifyOtpRequestDTO request);

    ApiResponseDTO<String> signup(SignupRequestDTO request);

    ApiResponseDTO<String> verifySignupOtp(VerifySignupOtpRequestDTO request);
}

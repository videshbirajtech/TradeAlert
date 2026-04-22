package com.vdmcreation.TradeAlert.service;

import com.vdmcreation.TradeAlert.dto.ApiResponseDTO;
import com.vdmcreation.TradeAlert.dto.LoginRequestDTO;
import com.vdmcreation.TradeAlert.dto.VerifyOtpRequestDTO;

public interface LoginService {

    ApiResponseDTO login(LoginRequestDTO request);

    ApiResponseDTO verifyOtp(VerifyOtpRequestDTO request);
}

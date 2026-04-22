package com.vdmcreation.TradeAlert.controller;

import com.vdmcreation.TradeAlert.dto.ApiResponseDTO;
import com.vdmcreation.TradeAlert.dto.LoginRequestDTO;
import com.vdmcreation.TradeAlert.dto.VerifyOtpRequestDTO;
import com.vdmcreation.TradeAlert.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(loginService.login(request));
    }

    // POST /api/auth/verify-otp
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponseDTO> verifyOtp(@RequestBody VerifyOtpRequestDTO request) {
        return ResponseEntity.ok(loginService.verifyOtp(request));
    }
}

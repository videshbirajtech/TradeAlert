package com.vdmcreation.TradeAlert.controller;

import com.vdmcreation.TradeAlert.dto.ApiResponseDTO;
import com.vdmcreation.TradeAlert.dto.LoginRequestDTO;
import com.vdmcreation.TradeAlert.dto.SignupRequestDTO;
import com.vdmcreation.TradeAlert.dto.VerifyOtpRequestDTO;
import com.vdmcreation.TradeAlert.dto.VerifySignupOtpRequestDTO;
import com.vdmcreation.TradeAlert.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<String>> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(loginService.login(request));
    }

    // POST /api/auth/verify-otp
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponseDTO<String>> verifyOtp(@RequestBody VerifyOtpRequestDTO request) {
        return ResponseEntity.ok(loginService.verifyOtp(request));
    }

    // POST /api/auth/signup
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDTO<String>> signup(@RequestBody SignupRequestDTO request) {
        return ResponseEntity.ok(loginService.signup(request));
    }

    // POST /api/auth/verify-signup-otp
    @PostMapping("/verify-signup-otp")
    public ResponseEntity<ApiResponseDTO<String>> verifySignupOtp(@RequestBody VerifySignupOtpRequestDTO request) {
        return ResponseEntity.ok(loginService.verifySignupOtp(request));
    }
}

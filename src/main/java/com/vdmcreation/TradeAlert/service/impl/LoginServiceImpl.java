package com.vdmcreation.TradeAlert.service.impl;

import com.vdmcreation.TradeAlert.dto.ApiResponseDTO;
import com.vdmcreation.TradeAlert.dto.LoginRequestDTO;
import com.vdmcreation.TradeAlert.dto.SignupRequestDTO;
import com.vdmcreation.TradeAlert.dto.VerifyOtpRequestDTO;
import com.vdmcreation.TradeAlert.dto.VerifySignupOtpRequestDTO;
import com.vdmcreation.TradeAlert.entity.User;
import com.vdmcreation.TradeAlert.entity.UserOtp;
import com.vdmcreation.TradeAlert.repository.UserOtpRepository;
import com.vdmcreation.TradeAlert.repository.UserRepository;
import com.vdmcreation.TradeAlert.service.EmailService;
import com.vdmcreation.TradeAlert.service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final UserOtpRepository userOtpRepository;
    private final EmailService emailService;

    public LoginServiceImpl(UserRepository userRepository,
                            UserOtpRepository userOtpRepository,
                            EmailService emailService) {
        this.userRepository = userRepository;
        this.userOtpRepository = userOtpRepository;
        this.emailService = emailService;
    }

    @Override
    public ApiResponseDTO<String> login(LoginRequestDTO request) {
        // Check if user exists
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No account found with email: " + request.getEmail()));

        // Generate 6-digit OTP
        int otp = 100000 + new Random().nextInt(900000);

        // Save OTP to database
        UserOtp userOtp = new UserOtp(user, otp);
        userOtpRepository.save(userOtp);

        // Send OTP via email
        emailService.sendOtpEmail(user.getEmail(), otp);

        return new ApiResponseDTO<>("OTP sent successfully to " + user.getEmail(), null, true);
    }

    @Override
    public ApiResponseDTO<String> verifyOtp(VerifyOtpRequestDTO request) {
        // Check if user exists
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No account found with email: " + request.getEmail()));

        // Fetch latest OTP record
        UserOtp latestOtp = userOtpRepository.findTopByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No OTP found for this user. Please request a new OTP."));

        // Check if OTP is already used
        if (latestOtp.isUsed()) {
            return new ApiResponseDTO<>("OTP has already been used. Please request a new OTP.", null, false);
        }

        // Check if OTP is expired (5 minutes)
        if (latestOtp.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            return new ApiResponseDTO<>("OTP has expired. Please request a new OTP.", null, false);
        }

        // Verify OTP
        if (!latestOtp.getOtp().equals(request.getOtp())) {
            return new ApiResponseDTO<>("Invalid OTP. Please try again.", null, false);
        }

        // Mark OTP as used
        latestOtp.setUsed(true);
        userOtpRepository.save(latestOtp);

        return new ApiResponseDTO<>("OTP verified successfully. Welcome, " + user.getFirstName() + "!", null, true);
    }

    @Override
    public ApiResponseDTO<String> signup(SignupRequestDTO request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "An account already exists with email: " + request.getEmail());
        }

        // Create user (unverified)
        User user = new User(request.getFirstName(), request.getLastName(), request.getEmail());
        user.setVerified(false);
        userRepository.save(user);

        // Generate and save OTP
        int otp = 100000 + new Random().nextInt(900000);
        userOtpRepository.save(new UserOtp(user, otp));

        // Send OTP email
        emailService.sendOtpEmail(user.getEmail(), otp);

        return new ApiResponseDTO<>("OTP sent to " + user.getEmail() + ". Please verify to complete signup.", null, true);
    }

    @Override
    public ApiResponseDTO<String> verifySignupOtp(VerifySignupOtpRequestDTO request) {
        // Check user exists
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No account found with email: " + request.getEmail()));

        // Fetch latest OTP
        UserOtp latestOtp = userOtpRepository.findTopByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No OTP found. Please sign up again."));

        // Check already used
        if (latestOtp.isUsed()) {
            return new ApiResponseDTO<>("OTP already used. Please sign up again.", null, false);
        }

        // Check expiry (5 minutes)
        if (latestOtp.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            return new ApiResponseDTO<>("OTP has expired. Please sign up again.", null, false);
        }

        // Verify OTP
        if (!latestOtp.getOtp().equals(request.getOtp())) {
            return new ApiResponseDTO<>("Invalid OTP. Please try again.", null, false);
        }

        // Mark OTP used and set user as verified
        latestOtp.setUsed(true);
        userOtpRepository.save(latestOtp);

        user.setVerified(true);
        userRepository.save(user);

        return new ApiResponseDTO<>("Account verified! Welcome, " + user.getFirstName() + "!", null, true);
    }
}

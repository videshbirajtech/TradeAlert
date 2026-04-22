package com.vdmcreation.TradeAlert.repository;

import com.vdmcreation.TradeAlert.entity.User;
import com.vdmcreation.TradeAlert.entity.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp, Long> {

    // Fetch the latest OTP for a user
    Optional<UserOtp> findTopByUserOrderByCreatedAtDesc(User user);
}

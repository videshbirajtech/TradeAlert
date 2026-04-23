package com.vdmcreation.TradeAlert.repository;

import com.vdmcreation.TradeAlert.entity.UserAlertStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAlertStatsRepository extends JpaRepository<UserAlertStats, Long> {

    Optional<UserAlertStats> findByUserId(Long userId);

    @Query("SELECT uas FROM UserAlertStats uas WHERE uas.user.email = :email")
    Optional<UserAlertStats> findByUserEmail(@Param("email") String email);

    boolean existsByUserId(Long userId);
}
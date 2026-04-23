package com.vdmcreation.TradeAlert.repository;

import com.vdmcreation.TradeAlert.entity.Subscription;
import com.vdmcreation.TradeAlert.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserOrderByCreatedAtDesc(User user);

    Optional<Subscription> findBySubscriptionId(String subscriptionId);
    
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.endDate > :currentDate ORDER BY s.endDate DESC")
    Optional<Subscription> findByUserIdAndEndDateAfter(@Param("userId") Long userId, @Param("currentDate") LocalDateTime currentDate);
}

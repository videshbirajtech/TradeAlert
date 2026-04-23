package com.vdmcreation.TradeAlert.repository;

import com.vdmcreation.TradeAlert.entity.PriceAlert;
import com.vdmcreation.TradeAlert.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {

    List<PriceAlert> findByUserOrderByCreatedAtDesc(User user);
    
    @Query("SELECT COUNT(pa) FROM PriceAlert pa WHERE pa.user.id = :userId")
    int countByUserId(@Param("userId") Long userId);
}

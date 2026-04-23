package com.vdmcreation.TradeAlert.repository;

import com.vdmcreation.TradeAlert.entity.PriceAlert;
import com.vdmcreation.TradeAlert.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {

    List<PriceAlert> findByUserOrderByCreatedAtDesc(User user);
}

package com.vdmcreation.TradeAlert.service;

import com.vdmcreation.TradeAlert.dto.PurchaseRequestDTO;
import com.vdmcreation.TradeAlert.dto.SubscriptionDTO;

import java.util.List;

public interface SubscriptionService {

    SubscriptionDTO purchase(PurchaseRequestDTO request);

    List<SubscriptionDTO> getSubscriptionsByEmail(String email);

    SubscriptionDTO getActiveSubscription(String email);
}

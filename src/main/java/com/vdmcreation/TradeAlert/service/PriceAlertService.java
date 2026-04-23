package com.vdmcreation.TradeAlert.service;

import com.vdmcreation.TradeAlert.dto.PriceAlertDTO;
import com.vdmcreation.TradeAlert.dto.PriceAlertRequestDTO;

import java.util.List;

public interface PriceAlertService {

    PriceAlertDTO createAlert(PriceAlertRequestDTO request);

    List<PriceAlertDTO> getAlertsByEmail(String email);

    PriceAlertDTO updateAlert(Long id, PriceAlertRequestDTO request);

    void deleteAlert(Long id);
}

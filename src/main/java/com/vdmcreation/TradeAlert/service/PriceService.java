package com.vdmcreation.TradeAlert.service;

import com.vdmcreation.TradeAlert.enums.CoinEnum;

import java.util.Map;

public interface PriceService {
    Map<String, String> getPrice(String symbol);
}

package com.vdmcreation.TradeAlert.service.impl;

import com.vdmcreation.TradeAlert.enums.CoinEnum;
import com.vdmcreation.TradeAlert.service.PriceService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PriceServiceImpl implements PriceService {

    private static final String BINANCE_URL = "https://api.binance.com/api/v3/ticker/price?symbol={symbol}";

    private final RestTemplate restTemplate;

    public PriceServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, String> getPrice(CoinEnum symbol) {
        return restTemplate.getForObject(BINANCE_URL, Map.class, symbol.getDisplayName());
    }
}

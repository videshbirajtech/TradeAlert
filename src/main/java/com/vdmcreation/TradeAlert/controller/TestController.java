package com.vdmcreation.TradeAlert.controller;

import com.vdmcreation.TradeAlert.enums.CoinEnum;
import com.vdmcreation.TradeAlert.service.PriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    private final PriceService priceService;

    public TestController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/price")
    public Map<String, String> getPrice(@RequestParam CoinEnum symbol) {
        return priceService.getPrice(symbol);
    }
}

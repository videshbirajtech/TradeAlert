package com.vdmcreation.TradeAlert.controller;

import com.vdmcreation.TradeAlert.dto.CoinDTO;
import com.vdmcreation.TradeAlert.service.CoinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coins")
public class CoinController {

    private final CoinService coinService;

    public CoinController(CoinService coinService) {
        this.coinService = coinService;
    }

    // GET /api/coins
    @GetMapping
    public ResponseEntity<List<CoinDTO>> getAllCoins() {
        return ResponseEntity.ok(coinService.getAllCoins());
    }
}

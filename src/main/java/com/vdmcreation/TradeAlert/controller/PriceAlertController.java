package com.vdmcreation.TradeAlert.controller;

import com.vdmcreation.TradeAlert.dto.PriceAlertDTO;
import com.vdmcreation.TradeAlert.dto.PriceAlertRequestDTO;
import com.vdmcreation.TradeAlert.service.PriceAlertService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class PriceAlertController {

    private final PriceAlertService alertService;

    public PriceAlertController(PriceAlertService alertService) {
        this.alertService = alertService;
    }

    // POST /api/alerts
    @PostMapping
    public ResponseEntity<PriceAlertDTO> createAlert(@RequestBody PriceAlertRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertService.createAlert(request));
    }

    // GET /api/alerts?email=user@example.com
    @GetMapping
    public ResponseEntity<List<PriceAlertDTO>> getAlerts(@RequestParam String email) {
        return ResponseEntity.ok(alertService.getAlertsByEmail(email));
    }

    // PUT /api/alerts/{id}
    @PutMapping("/{id}")
    public ResponseEntity<PriceAlertDTO> updateAlert(@PathVariable Long id,
                                                      @RequestBody PriceAlertRequestDTO request) {
        return ResponseEntity.ok(alertService.updateAlert(id, request));
    }

    // DELETE /api/alerts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }
}

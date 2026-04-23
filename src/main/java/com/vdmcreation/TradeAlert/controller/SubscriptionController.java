package com.vdmcreation.TradeAlert.controller;

import com.vdmcreation.TradeAlert.dto.PurchaseRequestDTO;
import com.vdmcreation.TradeAlert.dto.SubscriptionDTO;
import com.vdmcreation.TradeAlert.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // POST /api/subscriptions/purchase
    @PostMapping("/purchase")
    public ResponseEntity<SubscriptionDTO> purchase(@RequestBody PurchaseRequestDTO request) {
        return ResponseEntity.ok(subscriptionService.purchase(request));
    }

    // GET /api/subscriptions?email=user@example.com
    @GetMapping
    public ResponseEntity<List<SubscriptionDTO>> getAll(@RequestParam String email) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByEmail(email));
    }

    // GET /api/subscriptions/active?email=user@example.com
    @GetMapping("/active")
    public ResponseEntity<SubscriptionDTO> getActive(@RequestParam String email) {
        return ResponseEntity.ok(subscriptionService.getActiveSubscription(email));
    }
}

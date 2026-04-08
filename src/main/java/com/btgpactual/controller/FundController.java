package com.btgpactual.controller;

import com.btgpactual.domain.Fund;
import com.btgpactual.domain.Transaction;
import com.btgpactual.dto.CancelSubscriptionRequest;
import com.btgpactual.dto.SubscribeRequest;
import com.btgpactual.service.FundService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funds")
public class FundController {

    @Autowired
    private FundService fundService;

    @GetMapping
    public ResponseEntity<List<Fund>> getAllFunds() {
        return ResponseEntity.ok(fundService.getAllFunds());
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Transaction> subscribe(
            Authentication authentication,
            @Valid @RequestBody SubscribeRequest request) {
        
        String username = authentication.getName();
        Transaction transaction = fundService.subscribe(username, request);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<Transaction> unsubscribe(
            Authentication authentication,
            @Valid @RequestBody CancelSubscriptionRequest request) {
        
        String username = authentication.getName();
        Transaction transaction = fundService.unsubscribe(username, request.getFundId());
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> getHistory(Authentication authentication) {
        String username = authentication.getName();
        List<Transaction> history = fundService.getHistory(username);
        return ResponseEntity.ok(history);
    }
}

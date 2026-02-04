package org.poma.jpa.backend.controller;

import org.poma.jpa.backend.dto.SellRequest;
import org.poma.jpa.backend.dto.TransactionRequest;
import org.poma.jpa.backend.entity.Transactions;
import org.poma.jpa.backend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin("*")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    // ✅ BUY Transaction
    @PostMapping("/buy")
    public ResponseEntity<Transactions> buyAsset(
            @RequestBody TransactionRequest req
    ) {
        return ResponseEntity.ok(service.buy(req));
    }

    // ✅ SELL Transaction
    @PostMapping("/sell")
    public ResponseEntity<Transactions> sellAsset(
            @RequestBody SellRequest req
    ) {
        return ResponseEntity.ok(service.sell(req));
    }
}

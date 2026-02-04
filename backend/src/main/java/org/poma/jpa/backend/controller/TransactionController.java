package org.poma.jpa.backend.controller;

import org.poma.jpa.backend.dto.TransactionRequest;
import org.poma.jpa.backend.entity.Transactions;
import org.poma.jpa.backend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    // ✅ POST /api/transactions
    @PostMapping
    public ResponseEntity<Transactions> addTransaction(
            @RequestBody TransactionRequest req
    ) {
        return ResponseEntity.ok(service.create(req));
    }
}

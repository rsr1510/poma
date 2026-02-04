package org.poma.jpa.backend.controller;

import org.poma.jpa.backend.dto.HoldingsRequest;
import org.poma.jpa.backend.dto.SellRequest;
import org.poma.jpa.backend.entity.Holdings;
import org.poma.jpa.backend.entity.Transactions;
import org.poma.jpa.backend.service.HoldingsService;
import org.poma.jpa.backend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/holdings")
public class HoldingsController {

    private final HoldingsService svc;
    private final TransactionService transactionService;

    public HoldingsController(HoldingsService svc, TransactionService transactionService) {
        this.svc = svc;
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<Holdings>> all() {
        return ResponseEntity.ok(svc.findAll());
    }

//    @GetMapping("/summary")
//    public ResponseEntity<Map<String, BigDecimal>> summary() {
//        return ResponseEntity.ok(svc.getSummary());
//    }


//    @GetMapping("/portfolio/{portfolioId}")
//    public ResponseEntity<List<Holdings>> byPortfolio(@PathVariable Long portfolioId) {
//        return ResponseEntity.ok(svc.findByPortfolioId(portfolioId));
//    }

//    @GetMapping("/{id}")
//    public ResponseEntity<Holdings> get(@PathVariable Long id) {
//        return ResponseEntity.ok(svc.findById(id));
//    }

    @PostMapping("/portfolio/{portfolioId}/asset/{assetId}")
    public ResponseEntity<Holdings> create(@PathVariable Long portfolioId, @PathVariable Long assetId, @RequestBody HoldingsRequest req) {
        Holdings saved = svc.create(portfolioId, assetId, req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PostMapping("/sell")
    public ResponseEntity<Transactions> sell(@RequestBody SellRequest req) {
        Transactions transaction = transactionService.sell(req);
        return ResponseEntity.ok(transaction);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Holdings> update(@PathVariable Long id, @RequestBody HoldingsRequest req) {
//        Holdings updated = svc.update(id, req);
//        return ResponseEntity.ok(updated);
//    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        svc.delete(id);
//        return ResponseEntity.noContent().build();
//    }
}


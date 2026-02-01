package org.poma.jpa.backend.controller;

import org.poma.jpa.backend.dto.UserHistoryRequest;
import org.poma.jpa.backend.entity.UserHistory;
import org.poma.jpa.backend.service.UserHistoryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/history")
public class UserHistoryController {

    private final UserHistoryService svc;

    public UserHistoryController(UserHistoryService svc) {
        this.svc = svc;
    }

    @GetMapping
    public ResponseEntity<List<UserHistory>> all() {
        return ResponseEntity.ok(svc.findAll());
    }

    @GetMapping("/portfolio/{portfolioId}")
    public ResponseEntity<List<UserHistory>> byPortfolio(@PathVariable Long portfolioId) {
        return ResponseEntity.ok(svc.findByPortfolio(portfolioId));
    }

    @GetMapping("/portfolio/{portfolioId}/range")
    public ResponseEntity<List<UserHistory>> byPortfolioRange(
            @PathVariable Long portfolioId,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(svc.findByPortfolioBetween(portfolioId, from, to));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserHistory> get(@PathVariable Long id) {
        return ResponseEntity.ok(svc.findById(id));
    }

    @PostMapping("/portfolio/{portfolioId}")
    public ResponseEntity<UserHistory> create(@PathVariable Long portfolioId, @RequestBody UserHistoryRequest req) {
        if (req == null) throw new IllegalArgumentException("UserHistory must not be null");
        if (req.getDate() == null) throw new IllegalArgumentException("Date is required");
        if (req.getTotalValue() == null) throw new IllegalArgumentException("totalValue is required");
        UserHistory saved = svc.create(portfolioId, req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserHistory> update(@PathVariable Long id, @RequestBody UserHistoryRequest req) {
        if (req == null) throw new IllegalArgumentException("UserHistory must not be null");
        UserHistory updated = svc.update(id, req);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }
}

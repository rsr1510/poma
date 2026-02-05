package org.poma.jpa.backend.controller;

import org.poma.jpa.backend.dto.AlertRequest;
import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.entity.PriceAlert;
import org.poma.jpa.backend.repo.AssetRepo;
import org.poma.jpa.backend.repo.PriceAlertRepo;
import org.poma.jpa.backend.service.AlertEvaluationService;
import org.poma.jpa.backend.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin("*")
public class AlertController {

    private final AlertService alertService;
    private final PriceAlertRepo priceAlertRepo;
    private final AssetRepo assetRepo;
    private final AlertEvaluationService alertEvaluationService;

    public AlertController(AlertService alertService, PriceAlertRepo priceAlertRepo, AssetRepo assetRepo, AlertEvaluationService alertEvaluationService) {
        this.alertService = alertService;
        this.priceAlertRepo = priceAlertRepo;
        this.assetRepo = assetRepo;
        this.alertEvaluationService = alertEvaluationService;
    }

    @PostMapping
    public ResponseEntity<PriceAlert> createAlert(@RequestBody AlertRequest request) {
        try {
            PriceAlert alert = alertService.createAlert(request);
            return ResponseEntity.ok(alert);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PriceAlert>> getAllAlerts() {
        List<PriceAlert> alerts = alertService.getAllAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PriceAlert>> getActiveAlerts() {
        List<PriceAlert> alerts = alertService.getActiveAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<PriceAlert>> getAlertsForAsset(@PathVariable Long assetId) {
        try {
            List<PriceAlert> alerts = alertService.getAlertsForAsset(assetId);
            return ResponseEntity.ok(alerts);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{alertId}")
    public ResponseEntity<PriceAlert> updateAlert(@PathVariable Long alertId, @RequestBody AlertRequest request) {
        try {
            PriceAlert alert = alertService.updateAlert(alertId, request);
            return ResponseEntity.ok(alert);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{alertId}/toggle")
    public ResponseEntity<PriceAlert> toggleAlert(@PathVariable Long alertId) {
        try {
            PriceAlert alert = alertService.toggleAlert(alertId);
            return ResponseEntity.ok(alert);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long alertId) {
        try {
            alertService.deleteAlert(alertId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/asset/{assetId}")
    public ResponseEntity<Void> deleteAlertsByAsset(@PathVariable Long assetId) {
        Assets asset = assetRepo.findById(assetId)
            .orElseThrow(() -> new RuntimeException("Asset not found"));
        
        List<PriceAlert> alerts = priceAlertRepo.findByAsset(asset);
        priceAlertRepo.deleteAll(alerts);
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test-evaluation")
    public ResponseEntity<String> testAlertEvaluation() {
        try {
            alertEvaluationService.evaluateAlerts();
            return ResponseEntity.ok("Alert evaluation triggered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}

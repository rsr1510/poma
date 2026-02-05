package org.poma.jpa.backend.service;

import org.poma.jpa.backend.dto.AlertRequest;
import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.entity.PriceAlert;
import org.poma.jpa.backend.repo.AssetRepo;
import org.poma.jpa.backend.repo.PriceAlertRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AlertService {

    private final PriceAlertRepo priceAlertRepo;
    private final AssetRepo assetRepo;

    public AlertService(PriceAlertRepo priceAlertRepo, AssetRepo assetRepo) {
        this.priceAlertRepo = priceAlertRepo;
        this.assetRepo = assetRepo;
    }

    public PriceAlert createAlert(AlertRequest request) {
        Assets asset = assetRepo.findById(request.getAssetId())
                .orElseThrow(() -> new RuntimeException("Asset not found with ID: " + request.getAssetId()));

        PriceAlert.AlertCondition condition;
        try {
            condition = PriceAlert.AlertCondition.valueOf(request.getCondition().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid condition. Must be ABOVE or BELOW");
        }

        // Check if duplicate alert already exists
        Optional<PriceAlert> existingAlert = priceAlertRepo.findByAssetAndConditionAndThresholdPrice(
                asset, condition, request.getThresholdPrice());
        
        if (existingAlert.isPresent()) {
            // Reactivate existing alert if it was inactive
            PriceAlert alert = existingAlert.get();
            alert.setIsActive(true);
            return priceAlertRepo.save(alert);
        }

        PriceAlert alert = new PriceAlert(asset, request.getThresholdPrice(), condition);
        return priceAlertRepo.save(alert);
    }

    public List<PriceAlert> getActiveAlerts() {
        return priceAlertRepo.findByIsActive(true);
    }

    public List<PriceAlert> getAllAlerts() {
        return priceAlertRepo.findAll();
    }

    public PriceAlert updateAlert(Long alertId, AlertRequest request) {
        PriceAlert alert = priceAlertRepo.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found with ID: " + alertId));

        if (request.getThresholdPrice() != null) {
            alert.setThresholdPrice(request.getThresholdPrice());
        }

        if (request.getCondition() != null) {
            try {
                PriceAlert.AlertCondition condition = PriceAlert.AlertCondition.valueOf(request.getCondition().toUpperCase());
                alert.setCondition(condition);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid condition. Must be ABOVE or BELOW");
            }
        }

        return priceAlertRepo.save(alert);
    }

    public void deleteAlert(Long alertId) {
        if (!priceAlertRepo.existsById(alertId)) {
            throw new RuntimeException("Alert not found with ID: " + alertId);
        }
        priceAlertRepo.deleteById(alertId);
    }

    public PriceAlert toggleAlert(Long alertId) {
        PriceAlert alert = priceAlertRepo.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found with ID: " + alertId));
        
        alert.setIsActive(!alert.getIsActive());
        return priceAlertRepo.save(alert);
    }

    public List<PriceAlert> getAlertsForAsset(Long assetId) {
        Assets asset = assetRepo.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found with ID: " + assetId));
        
        return priceAlertRepo.findByAsset(asset);
    }
}

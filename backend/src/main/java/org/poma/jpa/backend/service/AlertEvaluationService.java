package org.poma.jpa.backend.service;

import org.poma.jpa.backend.entity.*;
import org.poma.jpa.backend.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AlertEvaluationService {

    private static final Logger logger = LoggerFactory.getLogger(AlertEvaluationService.class);

    private final PriceAlertRepo priceAlertRepo;
    private final NotificationRepo notificationRepo;
    private final PriceService priceService;
    private final AssetRepo assetRepo;

    public AlertEvaluationService(
            PriceAlertRepo priceAlertRepo,
            NotificationRepo notificationRepo,
            PriceService priceService,
            AssetRepo assetRepo) {
        this.priceAlertRepo = priceAlertRepo;
        this.notificationRepo = notificationRepo;
        this.priceService = priceService;
        this.assetRepo = assetRepo;
    }

    @Scheduled(fixedRate = 30000) // Run every 30 seconds
    public void evaluateAlerts() {
        logger.debug("Starting alert evaluation at {}", LocalDateTime.now());
        
        List<PriceAlert> activeAlerts = priceAlertRepo.findAllActiveAlerts();
        
        if (activeAlerts.isEmpty()) {
            logger.debug("No active alerts to evaluate");
            return;
        }

        logger.info("Evaluating {} active alerts", activeAlerts.size());

        for (PriceAlert alert : activeAlerts) {
            try {
                evaluateAlert(alert);
            } catch (Exception e) {
                logger.error("Error evaluating alert {} for asset {}", 
                    alert.getId(), alert.getAsset().getSymbol(), e);
            }
        }
        
        logger.debug("Completed alert evaluation at {}", LocalDateTime.now());
    }

    private void evaluateAlert(PriceAlert alert) {
        Assets asset = alert.getAsset();
        BigDecimal currentPrice = priceService.getCurrentPrice(asset.getSymbol());
        
        if (currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Invalid price {} for asset {}", currentPrice, asset.getSymbol());
            return;
        }

        boolean conditionMet = checkCondition(alert, currentPrice);
        boolean wasPreviouslyTriggered = alert.getLastTriggeredAt() != null;
        
        logger.debug("Evaluating alert {} for {} - Current: {}, Threshold: {}, Condition: {}, Met: {}", 
            alert.getId(), asset.getSymbol(), currentPrice, alert.getThresholdPrice(), alert.getCondition(), conditionMet);
        
        if (conditionMet) {
            // For BELOW alerts, always notify if condition is met (simpler logic)
            if (alert.getCondition() == PriceAlert.AlertCondition.BELOW) {
                logger.info("BELOW alert condition met for {} - Creating notification", asset.getSymbol());
                createNotification(alert, currentPrice);
                updateAlertTriggered(alert);
                return;
            }
            
            logger.info("Alert condition met for {} - Current: {}, Threshold: {}, Condition: {}", 
                asset.getSymbol(), currentPrice, alert.getThresholdPrice(), alert.getCondition());
            
            // Create notification if:
            // 1. This is the first time the alert is triggered, OR
            // 2. The price moved away from the threshold and came back
            boolean shouldNotify = false;
            
            if (!wasPreviouslyTriggered) {
                // First time triggering
                shouldNotify = true;
                logger.debug("First time triggering alert {}", alert.getId());
            } else {
                // Check if price moved away from threshold and came back
                BigDecimal lastTriggeredPrice = getLastTriggeredPrice(alert);
                if (lastTriggeredPrice != null) {
                    boolean wasAboveThreshold = lastTriggeredPrice.compareTo(alert.getThresholdPrice()) > 0;
                    boolean isAboveThreshold = currentPrice.compareTo(alert.getThresholdPrice()) > 0;
                    
                    logger.debug("Alert {} - Was above: {}, Is above: {}, Condition: {}", 
                        alert.getId(), wasAboveThreshold, isAboveThreshold, alert.getCondition());
                    
                    // If the alert condition is ABOVE and price went below threshold and came back up
                    // OR if the alert condition is BELOW and price went above threshold and came back down
                    if (alert.getCondition() == PriceAlert.AlertCondition.ABOVE) {
                        shouldNotify = !wasAboveThreshold && isAboveThreshold;
                    } else { // BELOW
                        shouldNotify = wasAboveThreshold && !isAboveThreshold;
                    }
                    
                    logger.debug("Alert {} - Should notify: {}", alert.getId(), shouldNotify);
                } else {
                    // Fallback: notify if no recent notification exists
                    shouldNotify = !notificationRepo.existsByAlertAndCreatedAtAfter(alert, 
                        LocalDateTime.now().minusMinutes(5));
                    logger.debug("Alert {} - Using fallback notification check: {}", alert.getId(), shouldNotify);
                }
            }
            
            if (shouldNotify) {
                createNotification(alert, currentPrice);
                updateAlertTriggered(alert);
            } else {
                logger.debug("Condition met but notification already recent for alert {}", alert.getId());
            }
        }
    }

    private boolean checkCondition(PriceAlert alert, BigDecimal currentPrice) {
        BigDecimal thresholdPrice = alert.getThresholdPrice();
        
        logger.debug("Checking condition for {} - Current: {}, Threshold: {}, Condition: {}", 
            alert.getAsset().getSymbol(), currentPrice, thresholdPrice, alert.getCondition());
        
        switch (alert.getCondition()) {
            case ABOVE:
                boolean aboveResult = currentPrice.compareTo(thresholdPrice) > 0;
                logger.debug("ABOVE condition result: {} > {} = {}", currentPrice, thresholdPrice, aboveResult);
                return aboveResult;
            case BELOW:
                boolean belowResult = currentPrice.compareTo(thresholdPrice) < 0;
                logger.debug("BELOW condition result: {} < {} = {}", currentPrice, thresholdPrice, belowResult);
                return belowResult;
            default:
                logger.warn("Unknown alert condition: {}", alert.getCondition());
                return false;
        }
    }

    private void createNotification(PriceAlert alert, BigDecimal triggeredPrice) {
        Assets asset = alert.getAsset();
        String message = buildNotificationMessage(asset, alert, triggeredPrice);
        
        Notification notification = new Notification(asset, alert, message, triggeredPrice);
        notificationRepo.save(notification);
        
        logger.info("Created notification: {}", message);
    }

    private String buildNotificationMessage(Assets asset, PriceAlert alert, BigDecimal triggeredPrice) {
        String conditionText = alert.getCondition() == PriceAlert.AlertCondition.ABOVE ? "rose above" : "dropped below";
        
        return String.format("%s (%s) price %s ₹%s", 
            asset.getName(), 
            asset.getSymbol(),
            conditionText,
            alert.getThresholdPrice().toString()
        );
    }

    private BigDecimal getLastTriggeredPrice(PriceAlert alert) {
        // Find the most recent notification for this alert to get the triggered price
        List<Notification> notifications = notificationRepo.findByAlertOrderByCreatedAtDesc(alert);
        return notifications.isEmpty() ? null : notifications.get(0).getTriggeredPrice();
    }

    private void updateAlertTriggered(PriceAlert alert) {
        alert.setLastTriggeredAt(LocalDateTime.now());
        
        // For one-time alerts, you might want to deactivate them here
        // alert.setIsActive(false);
        
        priceAlertRepo.save(alert);
    }

    public void evaluateAlertForAsset(Assets asset) {
        List<PriceAlert> alerts = priceAlertRepo.findActiveAlertsForAsset(asset);
        
        for (PriceAlert alert : alerts) {
            try {
                evaluateAlert(alert);
            } catch (Exception e) {
                logger.error("Error evaluating alert {} for asset {}", 
                    alert.getId(), asset.getSymbol(), e);
            }
        }
    }
}

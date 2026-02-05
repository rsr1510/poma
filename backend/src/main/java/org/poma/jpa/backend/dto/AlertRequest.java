package org.poma.jpa.backend.dto;

import java.math.BigDecimal;

public class AlertRequest {
    private Long assetId;
    private BigDecimal thresholdPrice;
    private String condition; // "ABOVE" or "BELOW"

    public AlertRequest() {}

    public AlertRequest(Long assetId, BigDecimal thresholdPrice, String condition) {
        this.assetId = assetId;
        this.thresholdPrice = thresholdPrice;
        this.condition = condition;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public BigDecimal getThresholdPrice() {
        return thresholdPrice;
    }

    public void setThresholdPrice(BigDecimal thresholdPrice) {
        this.thresholdPrice = thresholdPrice;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}

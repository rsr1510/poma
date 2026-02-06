package org.poma.jpa.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "price_alerts")
public class PriceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Assets asset;

    @Column(name = "threshold_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal thresholdPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_condition", nullable = false, length = 10)
    private AlertCondition condition;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_triggered_at")
    private LocalDateTime lastTriggeredAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public PriceAlert() {}

    public PriceAlert(Assets asset, BigDecimal thresholdPrice, AlertCondition condition) {
        this.asset = asset;
        this.thresholdPrice = thresholdPrice;
        this.condition = condition;
    }

    public Long getId() {
        return id;
    }

    public Assets getAsset() {
        return asset;
    }

    public void setAsset(Assets asset) {
        this.asset = asset;
    }

    public BigDecimal getThresholdPrice() {
        return thresholdPrice;
    }

    public void setThresholdPrice(BigDecimal thresholdPrice) {
        this.thresholdPrice = thresholdPrice;
    }

    public AlertCondition getCondition() {
        return condition;
    }

    public void setCondition(AlertCondition condition) {
        this.condition = condition;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastTriggeredAt() {
        return lastTriggeredAt;
    }

    public void setLastTriggeredAt(LocalDateTime lastTriggeredAt) {
        this.lastTriggeredAt = lastTriggeredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PriceAlert)) return false;
        PriceAlert that = (PriceAlert) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PriceAlert{" +
                "id=" + id +
                ", asset=" + (asset != null ? asset.getSymbol() : null) +
                ", thresholdPrice=" + thresholdPrice +
                ", condition=" + condition +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", lastTriggeredAt=" + lastTriggeredAt +
                '}';
    }

    public enum AlertCondition {
        ABOVE,
        BELOW
    }
}

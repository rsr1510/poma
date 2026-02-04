package org.poma.jpa.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Assets asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id", nullable = false)
    private PriceAlert alert;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "triggered_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal triggeredPrice;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Notification() {}

    public Notification(Assets asset, PriceAlert alert, String message, BigDecimal triggeredPrice) {
        this.asset = asset;
        this.alert = alert;
        this.message = message;
        this.triggeredPrice = triggeredPrice;
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

    public PriceAlert getAlert() {
        return alert;
    }

    public void setAlert(PriceAlert alert) {
        this.alert = alert;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getTriggeredPrice() {
        return triggeredPrice;
    }

    public void setTriggeredPrice(BigDecimal triggeredPrice) {
        this.triggeredPrice = triggeredPrice;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", asset=" + (asset != null ? asset.getSymbol() : null) +
                ", alert=" + (alert != null ? alert.getId() : null) +
                ", message='" + message + '\'' +
                ", triggeredPrice=" + triggeredPrice +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}

package org.poma.jpa.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "holdings")
public class Holdings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Legacy or alternate FK column present in DB; map it so inserts include user_id if DB requires it.
    @Column(name = "user_id", insertable = true, updatable = false)
    private Long userId;

    // References the user who owns the portfolio (stored in portfolio_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private User portfolioOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Assets asset;

    @Column(precision = 15, scale = 4, nullable = false)
    private BigDecimal quantity;

    @Column(name = "avg_buy_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal avgBuyPrice;

    @Column(name = "current_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal currentPrice;

    // Computed/stored column in DB: market_value AS (quantity * current_price) STORED
    @Column(name = "market_value", precision = 15, scale = 2, insertable = false, updatable = false)
    private BigDecimal marketValue;

    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() {
        this.addedAt = LocalDateTime.now();
    }

    public Holdings() {}

    public Holdings(User portfolioOwner, Assets asset, BigDecimal quantity, BigDecimal avgBuyPrice, BigDecimal currentPrice) {
        this.portfolioOwner = portfolioOwner;
        this.asset = asset;
        this.quantity = quantity;
        this.avgBuyPrice = avgBuyPrice;
        this.currentPrice = currentPrice;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getPortfolioOwner() {
        return portfolioOwner;
    }

    public void setPortfolioOwner(User portfolioOwner) {
        this.portfolioOwner = portfolioOwner;
    }

    public Assets getAsset() {
        return asset;
    }

    public void setAsset(Assets asset) {
        this.asset = asset;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAvgBuyPrice() {
        return avgBuyPrice;
    }

    public void setAvgBuyPrice(BigDecimal avgBuyPrice) {
        this.avgBuyPrice = avgBuyPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getMarketValue() {
        return marketValue;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Holdings)) return false;
        Holdings holdings = (Holdings) o;
        return Objects.equals(id, holdings.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Holdings{" +
                "id=" + id +
                ", userId=" + userId +
                ", portfolioOwner=" + (portfolioOwner != null ? portfolioOwner.getId() : null) +
                ", asset=" + (asset != null ? asset.getId() : null) +
                ", quantity=" + quantity +
                ", avgBuyPrice=" + avgBuyPrice +
                ", currentPrice=" + currentPrice +
                ", marketValue=" + marketValue +
                ", addedAt=" + addedAt +
                '}';
    }
}

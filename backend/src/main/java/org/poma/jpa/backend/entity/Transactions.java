package org.poma.jpa.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="transactions")
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Link Asset Table
    @ManyToOne
    @JoinColumn(name="asset_id", nullable=false)
    private Assets asset;

    private String platform;

    private BigDecimal quantity;

    @Column(name="price_per_unit")
    private BigDecimal pricePerUnit;

    private BigDecimal fees;

    @Column(name="total_cost")
    private BigDecimal totalCost;

    // ✅ Filled only when sold
    @Column(name="market_value")
    private BigDecimal marketValue;

    @Enumerated(EnumType.STRING)
    @Column(name="transaction_type")
    private TransactionType transactionType;

    @Column(name="transaction_date")
    private LocalDateTime transactionDate;

    @PrePersist
    public void onCreate() {
        transactionDate = LocalDateTime.now();
    }

    // ================= Getters + Setters =================

    public Long getId() {
        return id;
    }

    public Assets getAsset() {
        return asset;
    }

    public void setAsset(Assets asset) {
        this.asset = asset;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(BigDecimal marketValue) {
        this.marketValue = marketValue;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
}

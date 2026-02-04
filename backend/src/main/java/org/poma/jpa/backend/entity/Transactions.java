package org.poma.jpa.backend.entity;

import jakarta.persistence.*;
import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="transactions")
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String platform;

    private BigDecimal quantity;

    @Column(name="price_per_unit")
    private BigDecimal pricePerUnit;

    private BigDecimal fees;

    @Column(name="total_cost")
    private BigDecimal totalCost;

    @Column(name="transaction_date")
    private LocalDateTime transactionDate;

    @PrePersist
    public void onCreate() {
        transactionDate = LocalDateTime.now();
    }

    public Transactions() {}

    public Transactions(Long id, String platform, BigDecimal quantity, BigDecimal pricePerUnit, BigDecimal fees, BigDecimal totalCost, LocalDateTime transactionDate) {
        this.id = id;
        this.platform = platform;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.fees = fees;
        this.totalCost = totalCost;
        this.transactionDate = transactionDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    // getters/setters
}

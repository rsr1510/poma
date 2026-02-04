package org.poma.jpa.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "assets")
public class Assets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, unique = true, nullable = false)
    private String symbol;

    @Column(length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssetType type;

    // 🔹 NEW FIELDS
    @Column(nullable = false)
    private Integer units;

    @Column(name = "price_per_unit", nullable = false)
    private Double pricePerUnit;

    @Column(name = "platform_fee", nullable = false)
    private Double platformFee;

    @Column(name = "final_value", nullable = false)
    private Double finalValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        calculateFinalValue();
    }

    private void calculateFinalValue() {
        if (units != null && pricePerUnit != null && platformFee != null) {
            this.finalValue = (units * pricePerUnit) + platformFee;
        }
    }

    public Assets() {}

    public Assets(String symbol, String name, AssetType type,
                  Integer units, Double pricePerUnit, Double platformFee) {
        this.symbol = symbol;
        this.name = name;
        this.type = type;
        this.units = units;
        this.pricePerUnit = pricePerUnit;
        this.platformFee = platformFee;
        calculateFinalValue();
    }

    // ---------- getters & setters ----------

    public Long getId() { return id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public AssetType getType() { return type; }
    public void setType(AssetType type) { this.type = type; }

    public Integer getUnits() { return units; }
    public void setUnits(Integer units) {
        this.units = units;
        calculateFinalValue();
    }

    public Double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        calculateFinalValue();
    }

    public Double getPlatformFee() { return platformFee; }
    public void setPlatformFee(Double platformFee) {
        this.platformFee = platformFee;
        calculateFinalValue();
    }

    public Double getFinalValue() { return finalValue; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assets)) return false;
        Assets assets = (Assets) o;
        return Objects.equals(id, assets.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

enum AssetType {
    STOCK,
    BOND,
    CRYPTO,
    CASH
}

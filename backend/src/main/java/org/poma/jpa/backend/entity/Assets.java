package org.poma.jpa.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "assets")
public class Assets {
//    CREATE TABLE assets (
//            id BIGINT AUTO_INCREMENT PRIMARY KEY,
//            symbol VARCHAR(20) UNIQUE,
//    name VARCHAR(100) NOT NULL,
//    type ENUM('STOCK','BOND','CRYPTO','CASH') NOT NULL,
//    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
//);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, unique = true)
    private String symbol;

    @Column(length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private AssetType type;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Assets() {}

    public Assets(String symbol, String name, AssetType type) {
        this.symbol = symbol;
        this.name = name;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetType getType() {
        return type;
    }

    public void setType(AssetType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

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

    @Override
    public String toString() {
        return "Assets{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", createdAt=" + createdAt +
                '}';
    }
}

enum AssetType {
    STOCK,
    BOND,
    CRYPTO,
    CASH
}

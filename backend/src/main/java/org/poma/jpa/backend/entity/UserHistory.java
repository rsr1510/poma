package org.poma.jpa.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "portfolio_performance")
public class UserHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // portfolio_id references portfolios(id) - using User as the owner of a portfolio in this model
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private User portfolioOwner;

    @Column(name = "date", nullable = false, updatable = false)
    private LocalDate date;

    @Column(name = "total_value", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalValue;

    public UserHistory() {}

    public UserHistory(User portfolioOwner, LocalDate date, BigDecimal totalValue) {
        this.portfolioOwner = portfolioOwner;
        this.date = LocalDate.now();
        this.totalValue = totalValue;
    }

    public Long getId() {
        return id;
    }

    public User getPortfolioOwner() {
        return portfolioOwner;
    }

    public void setPortfolioOwner(User portfolioOwner) {
        this.portfolioOwner = portfolioOwner;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserHistory)) return false;
        UserHistory that = (UserHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "UserHistory{" +
                "id=" + id +
                ", portfolioOwner=" + (portfolioOwner != null ? portfolioOwner.getId() : null) +
                ", date=" + date +
                ", totalValue=" + totalValue +
                '}';
    }
}

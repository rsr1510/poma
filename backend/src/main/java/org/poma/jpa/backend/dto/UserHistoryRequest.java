package org.poma.jpa.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UserHistoryRequest {
    private LocalDate date;
    private BigDecimal totalValue;

    public UserHistoryRequest() {}

    public UserHistoryRequest(LocalDate date, BigDecimal totalValue) {
        this.date = date;
        this.totalValue = totalValue;
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
}

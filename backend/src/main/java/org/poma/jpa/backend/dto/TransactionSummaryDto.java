package org.poma.jpa.backend.dto;

import java.math.BigDecimal;

public class TransactionSummaryDto {

    private BigDecimal totalInvested;
    private BigDecimal totalProfit;
    private BigDecimal totalFees;
    private BigDecimal estimatedTax;
    private BigDecimal netProfit;

    public TransactionSummaryDto() {}

    public TransactionSummaryDto(
            BigDecimal totalInvested,
            BigDecimal totalProfit,
            BigDecimal totalFees,
            BigDecimal estimatedTax,
            BigDecimal netProfit
    ) {
        this.totalInvested = totalInvested;
        this.totalProfit = totalProfit;
        this.totalFees = totalFees;
        this.estimatedTax = estimatedTax;
        this.netProfit = netProfit;
    }

    public BigDecimal getTotalInvested() {
        return totalInvested;
    }

    public void setTotalInvested(BigDecimal totalInvested) {
        this.totalInvested = totalInvested;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

    public BigDecimal getTotalFees() {
        return totalFees;
    }

    public void setTotalFees(BigDecimal totalFees) {
        this.totalFees = totalFees;
    }

    public BigDecimal getEstimatedTax() {
        return estimatedTax;
    }

    public void setEstimatedTax(BigDecimal estimatedTax) {
        this.estimatedTax = estimatedTax;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(BigDecimal netProfit) {
        this.netProfit = netProfit;
    }
}

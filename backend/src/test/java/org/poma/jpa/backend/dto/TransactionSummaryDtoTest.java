package org.poma.jpa.backend.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class TransactionSummaryDtoTest {

    private TransactionSummaryDto transactionSummaryDto;

    @BeforeEach
    void setUp() {
        transactionSummaryDto = new TransactionSummaryDto(
            new BigDecimal("15000.00"),
            new BigDecimal("3000.00"),
            new BigDecimal("75.00"),
            new BigDecimal("450.00"),
            new BigDecimal("2475.00")
        );
    }

    @Test
    @DisplayName("Should create TransactionSummaryDto with parameterized constructor")
    void testParameterizedConstructor() {
        assertNotNull(transactionSummaryDto);
        assertEquals(new BigDecimal("15000.00"), transactionSummaryDto.getTotalInvested());
        assertEquals(new BigDecimal("3000.00"), transactionSummaryDto.getTotalProfit());
        assertEquals(new BigDecimal("75.00"), transactionSummaryDto.getTotalFees());
        assertEquals(new BigDecimal("450.00"), transactionSummaryDto.getEstimatedTax());
        assertEquals(new BigDecimal("2475.00"), transactionSummaryDto.getNetProfit());
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testSettersAndGetters() {
        TransactionSummaryDto dto = new TransactionSummaryDto();

        dto.setTotalInvested(new BigDecimal("20000.00"));
        dto.setTotalProfit(new BigDecimal("5000.00"));
        dto.setTotalFees(new BigDecimal("100.00"));
        dto.setEstimatedTax(new BigDecimal("750.00"));
        dto.setNetProfit(new BigDecimal("4150.00"));

        assertEquals(new BigDecimal("20000.00"), dto.getTotalInvested());
        assertEquals(new BigDecimal("5000.00"), dto.getTotalProfit());
        assertEquals(new BigDecimal("100.00"), dto.getTotalFees());
        assertEquals(new BigDecimal("750.00"), dto.getEstimatedTax());
        assertEquals(new BigDecimal("4150.00"), dto.getNetProfit());
    }

    @Test
    @DisplayName("Should handle null values in setters")
    void testNullSetters() {
        TransactionSummaryDto dto = new TransactionSummaryDto();

        dto.setTotalInvested(null);
        dto.setTotalProfit(null);
        dto.setTotalFees(null);
        dto.setEstimatedTax(null);
        dto.setNetProfit(null);

        assertNull(dto.getTotalInvested());
        assertNull(dto.getTotalProfit());
        assertNull(dto.getTotalFees());
        assertNull(dto.getEstimatedTax());
        assertNull(dto.getNetProfit());
    }

    @Test
    @DisplayName("Should handle zero values correctly")
    void testZeroValues() {
        TransactionSummaryDto zeroDto = new TransactionSummaryDto(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        );

        assertEquals(BigDecimal.ZERO, zeroDto.getTotalInvested());
        assertEquals(BigDecimal.ZERO, zeroDto.getTotalProfit());
        assertEquals(BigDecimal.ZERO, zeroDto.getTotalFees());
        assertEquals(BigDecimal.ZERO, zeroDto.getEstimatedTax());
        assertEquals(BigDecimal.ZERO, zeroDto.getNetProfit());
    }

    @Test
    @DisplayName("Should handle negative values correctly")
    void testNegativeValues() {
        TransactionSummaryDto negativeDto = new TransactionSummaryDto(
            new BigDecimal("-10000.00"),
            new BigDecimal("-2000.00"),
            new BigDecimal("-50.00"),
            BigDecimal.ZERO,
            new BigDecimal("-2050.00")
        );

        assertEquals(new BigDecimal("-10000.00"), negativeDto.getTotalInvested());
        assertEquals(new BigDecimal("-2000.00"), negativeDto.getTotalProfit());
        assertEquals(new BigDecimal("-50.00"), negativeDto.getTotalFees());
        assertEquals(BigDecimal.ZERO, negativeDto.getEstimatedTax());
        assertEquals(new BigDecimal("-2050.00"), negativeDto.getNetProfit());
    }

    @Test
    @DisplayName("Should handle decimal precision correctly")
    void testDecimalPrecision() {
        TransactionSummaryDto preciseDto = new TransactionSummaryDto(
            new BigDecimal("12345.67890"),
            new BigDecimal("1234.56789"),
            new BigDecimal("12.34567"),
            new BigDecimal("185.18518"),
            new BigDecimal("1037.03704")
        );

        assertEquals(new BigDecimal("12345.67890"), preciseDto.getTotalInvested());
        assertEquals(new BigDecimal("1234.56789"), preciseDto.getTotalProfit());
        assertEquals(new BigDecimal("12.34567"), preciseDto.getTotalFees());
        assertEquals(new BigDecimal("185.18518"), preciseDto.getEstimatedTax());
        assertEquals(new BigDecimal("1037.03704"), preciseDto.getNetProfit());
    }

    @Test
    @DisplayName("Should handle very large values")
    void testLargeValues() {
        TransactionSummaryDto largeDto = new TransactionSummaryDto(
            new BigDecimal("999999999.99"),
            new BigDecimal("99999999.99"),
            new BigDecimal("999999.99"),
            new BigDecimal("14999999.9985"),
            new BigDecimal("74999999.9915")
        );

        assertEquals(new BigDecimal("999999999.99"), largeDto.getTotalInvested());
        assertEquals(new BigDecimal("99999999.99"), largeDto.getTotalProfit());
        assertEquals(new BigDecimal("999999.99"), largeDto.getTotalFees());
        assertEquals(new BigDecimal("14999999.9985"), largeDto.getEstimatedTax());
        assertEquals(new BigDecimal("74999999.9915"), largeDto.getNetProfit());
    }

    @Test
    @DisplayName("Should handle very small values")
    void testSmallValues() {
        TransactionSummaryDto smallDto = new TransactionSummaryDto(
            new BigDecimal("0.01"),
            new BigDecimal("0.001"),
            new BigDecimal("0.0001"),
            new BigDecimal("0.00015"),
            new BigDecimal("0.00075")
        );

        assertEquals(new BigDecimal("0.01"), smallDto.getTotalInvested());
        assertEquals(new BigDecimal("0.001"), smallDto.getTotalProfit());
        assertEquals(new BigDecimal("0.0001"), smallDto.getTotalFees());
        assertEquals(new BigDecimal("0.00015"), smallDto.getEstimatedTax());
        assertEquals(new BigDecimal("0.00075"), smallDto.getNetProfit());
    }

    @Test
    @DisplayName("Should handle profit calculation scenarios")
    void testProfitScenarios() {
        // Profit scenario
        TransactionSummaryDto profitDto = new TransactionSummaryDto(
            new BigDecimal("10000.00"),
            new BigDecimal("2000.00"),
            new BigDecimal("50.00"),
            new BigDecimal("300.00"),
            new BigDecimal("1650.00")
        );

        assertTrue(profitDto.getTotalProfit().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(profitDto.getNetProfit().compareTo(BigDecimal.ZERO) > 0);

        // Loss scenario
        TransactionSummaryDto lossDto = new TransactionSummaryDto(
            new BigDecimal("10000.00"),
            new BigDecimal("-2000.00"),
            new BigDecimal("50.00"),
            BigDecimal.ZERO,
            new BigDecimal("-2050.00")
        );

        assertTrue(lossDto.getTotalProfit().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(lossDto.getNetProfit().compareTo(BigDecimal.ZERO) < 0);

        // Break-even scenario
        TransactionSummaryDto breakEvenDto = new TransactionSummaryDto(
            new BigDecimal("10000.00"),
            BigDecimal.ZERO,
            new BigDecimal("50.00"),
            BigDecimal.ZERO,
            new BigDecimal("-50.00")
        );

        assertEquals(BigDecimal.ZERO, breakEvenDto.getTotalProfit());
        assertTrue(breakEvenDto.getNetProfit().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    @DisplayName("Should handle tax calculation scenarios")
    void testTaxScenarios() {
        // No tax on loss
        TransactionSummaryDto lossTaxDto = new TransactionSummaryDto(
            new BigDecimal("10000.00"),
            new BigDecimal("-1000.00"),
            new BigDecimal("50.00"),
            BigDecimal.ZERO,
            new BigDecimal("-1050.00")
        );

        assertEquals(BigDecimal.ZERO, lossTaxDto.getEstimatedTax());

        // Tax on profit
        TransactionSummaryDto profitTaxDto = new TransactionSummaryDto(
            new BigDecimal("10000.00"),
            new BigDecimal("1000.00"),
            new BigDecimal("50.00"),
            new BigDecimal("150.00"),
            new BigDecimal("800.00")
        );

        assertTrue(profitTaxDto.getEstimatedTax().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should allow partial field updates")
    void testPartialFieldUpdates() {
        TransactionSummaryDto dto = new TransactionSummaryDto();

        dto.setTotalInvested(new BigDecimal("15000.00"));
        dto.setTotalProfit(new BigDecimal("3000.00"));

        assertEquals(new BigDecimal("15000.00"), dto.getTotalInvested());
        assertEquals(new BigDecimal("3000.00"), dto.getTotalProfit());
        assertNull(dto.getTotalFees());
        assertNull(dto.getEstimatedTax());
        assertNull(dto.getNetProfit());
    }

    @Test
    @DisplayName("Should handle field updates independently")
    void testIndependentFieldUpdates() {
        TransactionSummaryDto dto = new TransactionSummaryDto();

        dto.setTotalInvested(new BigDecimal("10000.00"));
        dto.setTotalProfit(new BigDecimal("2000.00"));
        dto.setTotalFees(new BigDecimal("50.00"));
        dto.setEstimatedTax(new BigDecimal("300.00"));
        dto.setNetProfit(new BigDecimal("1650.00"));

        // Update each field independently
        dto.setTotalInvested(new BigDecimal("20000.00"));
        assertEquals(new BigDecimal("20000.00"), dto.getTotalInvested());
        assertEquals(new BigDecimal("2000.00"), dto.getTotalProfit());

        dto.setTotalProfit(new BigDecimal("4000.00"));
        assertEquals(new BigDecimal("4000.00"), dto.getTotalProfit());
        assertEquals(new BigDecimal("50.00"), dto.getTotalFees());

        dto.setTotalFees(new BigDecimal("100.00"));
        assertEquals(new BigDecimal("100.00"), dto.getTotalFees());
        assertEquals(new BigDecimal("300.00"), dto.getEstimatedTax());

        dto.setEstimatedTax(new BigDecimal("600.00"));
        assertEquals(new BigDecimal("600.00"), dto.getEstimatedTax());
        assertEquals(new BigDecimal("1650.00"), dto.getNetProfit());

        dto.setNetProfit(new BigDecimal("3300.00"));
        assertEquals(new BigDecimal("3300.00"), dto.getNetProfit());
    }

    @Test
    @DisplayName("Should handle decimal scaling correctly")
    void testDecimalScaling() {
        BigDecimal[] values = {
            new BigDecimal("1000"),
            new BigDecimal("1000.5"),
            new BigDecimal("1000.50"),
            new BigDecimal("1000.505"),
            new BigDecimal("1000.5050"),
            new BigDecimal("1000.50500")
        };

        for (BigDecimal value : values) {
            TransactionSummaryDto dto = new TransactionSummaryDto();
            dto.setTotalInvested(value);
            assertEquals(value, dto.getTotalInvested());
        }
    }

    @Test
    @DisplayName("Should handle scientific notation")
    void testScientificNotation() {
        BigDecimal scientificValue = new BigDecimal("1.5E+6");
        TransactionSummaryDto dto = new TransactionSummaryDto();
        dto.setTotalInvested(scientificValue);
        assertEquals(scientificValue, dto.getTotalInvested());
    }

    @Test
    @DisplayName("Should maintain immutability of constructor parameters")
    void testConstructorImmutability() {
        BigDecimal totalInvested = new BigDecimal("15000.00");
        BigDecimal totalProfit = new BigDecimal("3000.00");

        TransactionSummaryDto dto = new TransactionSummaryDto(
            totalInvested,
            totalProfit,
            new BigDecimal("75.00"),
            new BigDecimal("450.00"),
            new BigDecimal("2475.00")
        );

        // Modify original values
        totalInvested = new BigDecimal("20000.00");
        totalProfit = new BigDecimal("4000.00");

        // DTO should not be affected
        assertEquals(new BigDecimal("15000.00"), dto.getTotalInvested());
        assertEquals(new BigDecimal("3000.00"), dto.getTotalProfit());
    }
}

package org.poma.jpa.backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionsTest {

    private Transactions transaction;
    private Assets testAsset;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        transaction = new Transactions();
        testAsset = new Assets();
        testAsset.setSymbol("AAPL");
        testTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
    }

    @Test
    @DisplayName("Should create transaction with default constructor")
    void testDefaultConstructor() {
        assertNotNull(transaction);
        assertNull(transaction.getId());
        assertNull(transaction.getAsset());
        assertNull(transaction.getPlatform());
        assertNull(transaction.getQuantity());
        assertNull(transaction.getPricePerUnit());
        assertNull(transaction.getFees());
        assertNull(transaction.getTotalCost());
        assertNull(transaction.getMarketValue());
        assertNull(transaction.getTransactionType());
        assertNull(transaction.getTransactionDate());
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testSettersAndGetters() {
        transaction.setAsset(testAsset);
        transaction.setPlatform("Robinhood");
        transaction.setQuantity(new BigDecimal("100"));
        transaction.setPricePerUnit(new BigDecimal("150.50"));
        transaction.setFees(new BigDecimal("5.00"));
        transaction.setTotalCost(new BigDecimal("15055.00"));
        transaction.setMarketValue(new BigDecimal("16000.00"));
        transaction.setTransactionType(TransactionType.BUY);
        transaction.setTransactionDate(testTime);

        assertEquals(testAsset, transaction.getAsset());
        assertEquals("Robinhood", transaction.getPlatform());
        assertEquals(new BigDecimal("100"), transaction.getQuantity());
        assertEquals(new BigDecimal("150.50"), transaction.getPricePerUnit());
        assertEquals(new BigDecimal("5.00"), transaction.getFees());
        assertEquals(new BigDecimal("15055.00"), transaction.getTotalCost());
        assertEquals(new BigDecimal("16000.00"), transaction.getMarketValue());
        assertEquals(TransactionType.BUY, transaction.getTransactionType());
        assertEquals(testTime, transaction.getTransactionDate());
    }

    @Test
    @DisplayName("Should handle null values in setters")
    void testNullSetters() {
        transaction.setAsset(null);
        transaction.setPlatform(null);
        transaction.setQuantity(null);
        transaction.setPricePerUnit(null);
        transaction.setFees(null);
        transaction.setTotalCost(null);
        transaction.setMarketValue(null);
        transaction.setTransactionType(null);
        transaction.setTransactionDate(null);

        assertNull(transaction.getAsset());
        assertNull(transaction.getPlatform());
        assertNull(transaction.getQuantity());
        assertNull(transaction.getPricePerUnit());
        assertNull(transaction.getFees());
        assertNull(transaction.getTotalCost());
        assertNull(transaction.getMarketValue());
        assertNull(transaction.getTransactionType());
        assertNull(transaction.getTransactionDate());
    }

    @Test
    @DisplayName("Should handle BUY and SELL transaction types")
    void testTransactionTypes() {
        transaction.setTransactionType(TransactionType.BUY);
        assertEquals(TransactionType.BUY, transaction.getTransactionType());

        transaction.setTransactionType(TransactionType.SELL);
        assertEquals(TransactionType.SELL, transaction.getTransactionType());
    }

    @Test
    @DisplayName("Should handle BigDecimal precision correctly")
    void testBigDecimalPrecision() {
        BigDecimal preciseQuantity = new BigDecimal("123.456789");
        BigDecimal precisePrice = new BigDecimal("123.456789");
        BigDecimal preciseFees = new BigDecimal("12.345678");
        BigDecimal preciseTotalCost = new BigDecimal("15234.567890");
        BigDecimal preciseMarketValue = new BigDecimal("16000.123456");

        transaction.setQuantity(preciseQuantity);
        transaction.setPricePerUnit(precisePrice);
        transaction.setFees(preciseFees);
        transaction.setTotalCost(preciseTotalCost);
        transaction.setMarketValue(preciseMarketValue);

        assertEquals(preciseQuantity, transaction.getQuantity());
        assertEquals(precisePrice, transaction.getPricePerUnit());
        assertEquals(preciseFees, transaction.getFees());
        assertEquals(preciseTotalCost, transaction.getTotalCost());
        assertEquals(preciseMarketValue, transaction.getMarketValue());
    }

    @Test
    @DisplayName("Should handle asset relationship correctly")
    void testAssetRelationship() {
        Assets asset1 = new Assets();
        asset1.setSymbol("AAPL");

        Assets asset2 = new Assets();
        asset2.setSymbol("GOOGL");

        transaction.setAsset(asset1);
        assertEquals(asset1, transaction.getAsset());
        assertEquals("AAPL", transaction.getAsset().getSymbol());

        transaction.setAsset(asset2);
        assertEquals(asset2, transaction.getAsset());
        assertEquals("GOOGL", transaction.getAsset().getSymbol());
    }

    @Test
    @DisplayName("Should handle platform names")
    void testPlatformNames() {
        String[] platforms = {"Robinhood", "E*TRADE", "Fidelity", "Charles Schwab", "TD Ameritrade"};
        
        for (String platform : platforms) {
            transaction.setPlatform(platform);
            assertEquals(platform, transaction.getPlatform());
        }
    }

    @Test
    @DisplayName("Should handle different transaction dates")
    void testTransactionDates() {
        LocalDateTime[] dates = {
            LocalDateTime.of(2024, 1, 1, 9, 30),
            LocalDateTime.of(2024, 6, 15, 14, 45),
            LocalDateTime.of(2024, 12, 31, 16, 0)
        };

        for (LocalDateTime date : dates) {
            transaction.setTransactionDate(date);
            assertEquals(date, transaction.getTransactionDate());
        }
    }

    @Test
    @DisplayName("Should handle zero values")
    void testZeroValues() {
        transaction.setQuantity(BigDecimal.ZERO);
        transaction.setPricePerUnit(BigDecimal.ZERO);
        transaction.setFees(BigDecimal.ZERO);
        transaction.setTotalCost(BigDecimal.ZERO);
        transaction.setMarketValue(BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, transaction.getQuantity());
        assertEquals(BigDecimal.ZERO, transaction.getPricePerUnit());
        assertEquals(BigDecimal.ZERO, transaction.getFees());
        assertEquals(BigDecimal.ZERO, transaction.getTotalCost());
        assertEquals(BigDecimal.ZERO, transaction.getMarketValue());
    }

    @Test
    @DisplayName("Should handle negative values (though business logic might prevent this)")
    void testNegativeValues() {
        transaction.setQuantity(new BigDecimal("-100"));
        transaction.setPricePerUnit(new BigDecimal("-50.25"));
        transaction.setFees(new BigDecimal("-5.00"));
        transaction.setTotalCost(new BigDecimal("-5025.00"));
        transaction.setMarketValue(new BigDecimal("-4800.00"));

        assertEquals(new BigDecimal("-100"), transaction.getQuantity());
        assertEquals(new BigDecimal("-50.25"), transaction.getPricePerUnit());
        assertEquals(new BigDecimal("-5.00"), transaction.getFees());
        assertEquals(new BigDecimal("-5025.00"), transaction.getTotalCost());
        assertEquals(new BigDecimal("-4800.00"), transaction.getMarketValue());
    }

    @Test
    @DisplayName("Should handle very large values")
    void testLargeValues() {
        transaction.setQuantity(new BigDecimal("1000000"));
        transaction.setPricePerUnit(new BigDecimal("999999.99"));
        transaction.setFees(new BigDecimal("9999.99"));
        transaction.setTotalCost(new BigDecimal("999999999.99"));
        transaction.setMarketValue(new BigDecimal("1000000000.00"));

        assertEquals(new BigDecimal("1000000"), transaction.getQuantity());
        assertEquals(new BigDecimal("999999.99"), transaction.getPricePerUnit());
        assertEquals(new BigDecimal("9999.99"), transaction.getFees());
        assertEquals(new BigDecimal("999999999.99"), transaction.getTotalCost());
        assertEquals(new BigDecimal("1000000000.00"), transaction.getMarketValue());
    }
}

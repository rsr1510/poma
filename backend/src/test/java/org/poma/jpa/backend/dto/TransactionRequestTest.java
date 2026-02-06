package org.poma.jpa.backend.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRequestTest {

    private TransactionRequest transactionRequest;

    @BeforeEach
    void setUp() {
        transactionRequest = new TransactionRequest();
    }

    @Test
    @DisplayName("Should create TransactionRequest with default constructor")
    void testDefaultConstructor() {
        assertNotNull(transactionRequest);
        assertNull(transactionRequest.getSymbol());
        assertNull(transactionRequest.getQuantity());
        assertNull(transactionRequest.getPricePerUnit());
        assertNull(transactionRequest.getPlatform());
        assertNull(transactionRequest.getFees());
        assertNull(transactionRequest.getTotalCost());
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testSettersAndGetters() {
        transactionRequest.setSymbol("AAPL");
        transactionRequest.setQuantity(new BigDecimal("100"));
        transactionRequest.setPricePerUnit(new BigDecimal("150.50"));
        transactionRequest.setPlatform("Robinhood");
        transactionRequest.setFees(new BigDecimal("5.00"));
        transactionRequest.setTotalCost(new BigDecimal("15055.00"));

        assertEquals("AAPL", transactionRequest.getSymbol());
        assertEquals(new BigDecimal("100"), transactionRequest.getQuantity());
        assertEquals(new BigDecimal("150.50"), transactionRequest.getPricePerUnit());
        assertEquals("Robinhood", transactionRequest.getPlatform());
        assertEquals(new BigDecimal("5.00"), transactionRequest.getFees());
        assertEquals(new BigDecimal("15055.00"), transactionRequest.getTotalCost());
    }

    @Test
    @DisplayName("Should handle null values in setters")
    void testNullSetters() {
        transactionRequest.setSymbol(null);
        transactionRequest.setQuantity(null);
        transactionRequest.setPricePerUnit(null);
        transactionRequest.setPlatform(null);
        transactionRequest.setFees(null);
        transactionRequest.setTotalCost(null);

        assertNull(transactionRequest.getSymbol());
        assertNull(transactionRequest.getQuantity());
        assertNull(transactionRequest.getPricePerUnit());
        assertNull(transactionRequest.getPlatform());
        assertNull(transactionRequest.getFees());
        assertNull(transactionRequest.getTotalCost());
    }

    @Test
    @DisplayName("Should handle empty string for symbol")
    void testEmptySymbol() {
        transactionRequest.setSymbol("");
        assertEquals("", transactionRequest.getSymbol());
    }

    @Test
    @DisplayName("Should handle different platform names")
    void testDifferentPlatforms() {
        String[] platforms = {"Robinhood", "E*TRADE", "Fidelity", "Charles Schwab", "TD Ameritrade", ""};
        
        for (String platform : platforms) {
            transactionRequest.setPlatform(platform);
            assertEquals(platform, transactionRequest.getPlatform());
        }
    }

    @Test
    @DisplayName("Should handle BigDecimal precision correctly")
    void testBigDecimalPrecision() {
        BigDecimal preciseQuantity = new BigDecimal("123.456789");
        BigDecimal precisePrice = new BigDecimal("123.456789");
        BigDecimal preciseFees = new BigDecimal("12.345678");
        BigDecimal preciseTotalCost = new BigDecimal("15241.578901");

        transactionRequest.setQuantity(preciseQuantity);
        transactionRequest.setPricePerUnit(precisePrice);
        transactionRequest.setFees(preciseFees);
        transactionRequest.setTotalCost(preciseTotalCost);

        assertEquals(preciseQuantity, transactionRequest.getQuantity());
        assertEquals(precisePrice, transactionRequest.getPricePerUnit());
        assertEquals(preciseFees, transactionRequest.getFees());
        assertEquals(preciseTotalCost, transactionRequest.getTotalCost());
    }

    @Test
    @DisplayName("Should handle zero values")
    void testZeroValues() {
        transactionRequest.setQuantity(BigDecimal.ZERO);
        transactionRequest.setPricePerUnit(BigDecimal.ZERO);
        transactionRequest.setFees(BigDecimal.ZERO);
        transactionRequest.setTotalCost(BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, transactionRequest.getQuantity());
        assertEquals(BigDecimal.ZERO, transactionRequest.getPricePerUnit());
        assertEquals(BigDecimal.ZERO, transactionRequest.getFees());
        assertEquals(BigDecimal.ZERO, transactionRequest.getTotalCost());
    }

    @Test
    @DisplayName("Should handle negative values")
    void testNegativeValues() {
        transactionRequest.setQuantity(new BigDecimal("-100"));
        transactionRequest.setPricePerUnit(new BigDecimal("-150.50"));
        transactionRequest.setFees(new BigDecimal("-5.00"));
        transactionRequest.setTotalCost(new BigDecimal("-15055.00"));

        assertEquals(new BigDecimal("-100"), transactionRequest.getQuantity());
        assertEquals(new BigDecimal("-150.50"), transactionRequest.getPricePerUnit());
        assertEquals(new BigDecimal("-5.00"), transactionRequest.getFees());
        assertEquals(new BigDecimal("-15055.00"), transactionRequest.getTotalCost());
    }

    @Test
    @DisplayName("Should handle very large values")
    void testLargeValues() {
        transactionRequest.setQuantity(new BigDecimal("1000000"));
        transactionRequest.setPricePerUnit(new BigDecimal("999999.99"));
        transactionRequest.setFees(new BigDecimal("9999.99"));
        transactionRequest.setTotalCost(new BigDecimal("999999999.99"));

        assertEquals(new BigDecimal("1000000"), transactionRequest.getQuantity());
        assertEquals(new BigDecimal("999999.99"), transactionRequest.getPricePerUnit());
        assertEquals(new BigDecimal("9999.99"), transactionRequest.getFees());
        assertEquals(new BigDecimal("999999999.99"), transactionRequest.getTotalCost());
    }

    @Test
    @DisplayName("Should handle special characters in symbol")
    void testSpecialCharactersInSymbol() {
        String[] symbols = {"AAPL", "GOOGL", "BTC-USD", "ETH/USD", "SPY", "@#$%"};
        
        for (String symbol : symbols) {
            transactionRequest.setSymbol(symbol);
            assertEquals(symbol, transactionRequest.getSymbol());
        }
    }

    @Test
    @DisplayName("Should handle unicode characters in platform")
    void testUnicodeCharactersInPlatform() {
        String unicodePlatform = "交易平台";
        transactionRequest.setPlatform(unicodePlatform);
        assertEquals(unicodePlatform, transactionRequest.getPlatform());
    }

    @Test
    @DisplayName("Should handle decimal scaling correctly")
    void testDecimalScaling() {
        BigDecimal[] quantities = {
            new BigDecimal("100"),
            new BigDecimal("100.5"),
            new BigDecimal("100.50"),
            new BigDecimal("100.505"),
            new BigDecimal("100.5050"),
            new BigDecimal("100.50500")
        };

        for (BigDecimal quantity : quantities) {
            transactionRequest.setQuantity(quantity);
            assertEquals(quantity, transactionRequest.getQuantity());
        }
    }

    @Test
    @DisplayName("Should allow partial field updates")
    void testPartialFieldUpdates() {
        transactionRequest.setSymbol("AAPL");
        transactionRequest.setQuantity(new BigDecimal("100"));

        assertEquals("AAPL", transactionRequest.getSymbol());
        assertEquals(new BigDecimal("100"), transactionRequest.getQuantity());
        assertNull(transactionRequest.getPricePerUnit());
        assertNull(transactionRequest.getPlatform());
        assertNull(transactionRequest.getFees());
        assertNull(transactionRequest.getTotalCost());
    }

    @Test
    @DisplayName("Should handle field updates independently")
    void testIndependentFieldUpdates() {
        transactionRequest.setSymbol("AAPL");
        transactionRequest.setPricePerUnit(new BigDecimal("150.50"));
        transactionRequest.setPlatform("Robinhood");

        assertEquals("AAPL", transactionRequest.getSymbol());
        assertEquals(new BigDecimal("150.50"), transactionRequest.getPricePerUnit());
        assertEquals("Robinhood", transactionRequest.getPlatform());
        assertNull(transactionRequest.getQuantity());
        assertNull(transactionRequest.getFees());
        assertNull(transactionRequest.getTotalCost());
    }

    @Test
    @DisplayName("Should handle string field updates")
    void testStringFieldUpdates() {
        transactionRequest.setSymbol("AAPL");
        transactionRequest.setPlatform("Robinhood");

        transactionRequest.setSymbol("GOOGL");
        transactionRequest.setPlatform("E*TRADE");

        assertEquals("GOOGL", transactionRequest.getSymbol());
        assertEquals("E*TRADE", transactionRequest.getPlatform());
    }

    @Test
    @DisplayName("Should handle numeric field updates")
    void testNumericFieldUpdates() {
        transactionRequest.setQuantity(new BigDecimal("100"));
        transactionRequest.setPricePerUnit(new BigDecimal("150.50"));
        transactionRequest.setFees(new BigDecimal("5.00"));
        transactionRequest.setTotalCost(new BigDecimal("15055.00"));

        transactionRequest.setQuantity(new BigDecimal("200"));
        transactionRequest.setPricePerUnit(new BigDecimal("200.00"));
        transactionRequest.setFees(new BigDecimal("10.00"));
        transactionRequest.setTotalCost(new BigDecimal("40010.00"));

        assertEquals(new BigDecimal("200"), transactionRequest.getQuantity());
        assertEquals(new BigDecimal("200.00"), transactionRequest.getPricePerUnit());
        assertEquals(new BigDecimal("10.00"), transactionRequest.getFees());
        assertEquals(new BigDecimal("40010.00"), transactionRequest.getTotalCost());
    }
}

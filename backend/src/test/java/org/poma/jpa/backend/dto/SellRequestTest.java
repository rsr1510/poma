package org.poma.jpa.backend.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SellRequestTest {

    private SellRequest sellRequest;

    @BeforeEach
    void setUp() {
        sellRequest = new SellRequest();
    }

    @Test
    @DisplayName("Should create SellRequest with default constructor")
    void testDefaultConstructor() {
        assertNotNull(sellRequest);
        assertNull(sellRequest.getSymbol());
        assertNull(sellRequest.getQuantity());
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testSettersAndGetters() {
        sellRequest.setSymbol("AAPL");
        sellRequest.setQuantity(new BigDecimal("50"));

        assertEquals("AAPL", sellRequest.getSymbol());
        assertEquals(new BigDecimal("50"), sellRequest.getQuantity());
    }

    @Test
    @DisplayName("Should handle null values in setters")
    void testNullSetters() {
        sellRequest.setSymbol(null);
        sellRequest.setQuantity(null);

        assertNull(sellRequest.getSymbol());
        assertNull(sellRequest.getQuantity());
    }

    @Test
    @DisplayName("Should handle empty string for symbol")
    void testEmptySymbol() {
        sellRequest.setSymbol("");
        assertEquals("", sellRequest.getSymbol());
    }

    @Test
    @DisplayName("Should handle BigDecimal precision correctly")
    void testBigDecimalPrecision() {
        BigDecimal preciseQuantity = new BigDecimal("123.456789");

        sellRequest.setQuantity(preciseQuantity);

        assertEquals(preciseQuantity, sellRequest.getQuantity());
    }

    @Test
    @DisplayName("Should handle zero values")
    void testZeroValues() {
        sellRequest.setQuantity(BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, sellRequest.getQuantity());
    }

    @Test
    @DisplayName("Should handle negative values")
    void testNegativeValues() {
        sellRequest.setQuantity(new BigDecimal("-50"));

        assertEquals(new BigDecimal("-50"), sellRequest.getQuantity());
    }

    @Test
    @DisplayName("Should handle very large values")
    void testLargeValues() {
        sellRequest.setQuantity(new BigDecimal("1000000"));

        assertEquals(new BigDecimal("1000000"), sellRequest.getQuantity());
    }

    @Test
    @DisplayName("Should handle special characters in symbol")
    void testSpecialCharactersInSymbol() {
        String[] symbols = {"AAPL", "GOOGL", "BTC-USD", "ETH/USD", "SPY", "@#$%"};
        
        for (String symbol : symbols) {
            sellRequest.setSymbol(symbol);
            assertEquals(symbol, sellRequest.getSymbol());
        }
    }

    @Test
    @DisplayName("Should handle unicode characters in symbol")
    void testUnicodeCharactersInSymbol() {
        String unicodeSymbol = "比特币";
        sellRequest.setSymbol(unicodeSymbol);
        assertEquals(unicodeSymbol, sellRequest.getSymbol());
    }

    @Test
    @DisplayName("Should handle decimal scaling correctly")
    void testDecimalScaling() {
        BigDecimal[] quantities = {
            new BigDecimal("50"),
            new BigDecimal("50.5"),
            new BigDecimal("50.50"),
            new BigDecimal("50.505"),
            new BigDecimal("50.5050"),
            new BigDecimal("50.50500")
        };

        for (BigDecimal quantity : quantities) {
            sellRequest.setQuantity(quantity);
            assertEquals(quantity, sellRequest.getQuantity());
        }
    }

    @Test
    @DisplayName("Should allow partial field updates")
    void testPartialFieldUpdates() {
        sellRequest.setSymbol("AAPL");

        assertEquals("AAPL", sellRequest.getSymbol());
        assertNull(sellRequest.getQuantity());
    }

    @Test
    @DisplayName("Should allow partial field updates with quantity")
    void testPartialFieldUpdatesWithQuantity() {
        sellRequest.setQuantity(new BigDecimal("50"));

        assertNull(sellRequest.getSymbol());
        assertEquals(new BigDecimal("50"), sellRequest.getQuantity());
    }

    @Test
    @DisplayName("Should handle field updates independently")
    void testIndependentFieldUpdates() {
        sellRequest.setSymbol("AAPL");
        sellRequest.setQuantity(new BigDecimal("50"));

        sellRequest.setSymbol("GOOGL");
        sellRequest.setQuantity(new BigDecimal("100"));

        assertEquals("GOOGL", sellRequest.getSymbol());
        assertEquals(new BigDecimal("100"), sellRequest.getQuantity());
    }

    @Test
    @DisplayName("Should handle string field updates")
    void testStringFieldUpdates() {
        sellRequest.setSymbol("AAPL");
        assertEquals("AAPL", sellRequest.getSymbol());

        sellRequest.setSymbol("GOOGL");
        assertEquals("GOOGL", sellRequest.getSymbol());

        sellRequest.setSymbol("");
        assertEquals("", sellRequest.getSymbol());

        sellRequest.setSymbol(null);
        assertNull(sellRequest.getSymbol());
    }

    @Test
    @DisplayName("Should handle numeric field updates")
    void testNumericFieldUpdates() {
        sellRequest.setQuantity(new BigDecimal("50"));
        assertEquals(new BigDecimal("50"), sellRequest.getQuantity());

        sellRequest.setQuantity(new BigDecimal("100"));
        assertEquals(new BigDecimal("100"), sellRequest.getQuantity());

        sellRequest.setQuantity(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, sellRequest.getQuantity());

        sellRequest.setQuantity(null);
        assertNull(sellRequest.getQuantity());
    }

    @Test
    @DisplayName("Should handle fractional quantities")
    void testFractionalQuantities() {
        BigDecimal[] fractionalQuantities = {
            new BigDecimal("0.5"),
            new BigDecimal("1.25"),
            new BigDecimal("10.75"),
            new BigDecimal("100.333333")
        };

        for (BigDecimal quantity : fractionalQuantities) {
            sellRequest.setQuantity(quantity);
            assertEquals(quantity, sellRequest.getQuantity());
        }
    }

    @Test
    @DisplayName("Should handle very small quantities")
    void testVerySmallQuantities() {
        BigDecimal[] smallQuantities = {
            new BigDecimal("0.000001"),
            new BigDecimal("0.0000001"),
            new BigDecimal("0.00000001")
        };

        for (BigDecimal quantity : smallQuantities) {
            sellRequest.setQuantity(quantity);
            assertEquals(quantity, sellRequest.getQuantity());
        }
    }

    @Test
    @DisplayName("Should handle scientific notation")
    void testScientificNotation() {
        BigDecimal scientificQuantity = new BigDecimal("1.5E+6");
        sellRequest.setQuantity(scientificQuantity);
        assertEquals(scientificQuantity, sellRequest.getQuantity());
    }
}

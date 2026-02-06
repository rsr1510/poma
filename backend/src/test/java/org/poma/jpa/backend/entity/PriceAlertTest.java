package org.poma.jpa.backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PriceAlertTest {

    private PriceAlert priceAlert;
    private Assets testAsset;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        priceAlert = new PriceAlert();
        testAsset = new Assets();
        testAsset.setSymbol("AAPL");
        testTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
    }

    @Test
    @DisplayName("Should create price alert with default constructor")
    void testDefaultConstructor() {
        assertNotNull(priceAlert);
        assertNull(priceAlert.getId());
        assertNull(priceAlert.getAsset());
        assertNull(priceAlert.getThresholdPrice());
        assertNull(priceAlert.getCondition());
        assertTrue(priceAlert.getIsActive());
        assertNull(priceAlert.getCreatedAt());
        assertNull(priceAlert.getLastTriggeredAt());
    }

    @Test
    @DisplayName("Should create price alert with parameterized constructor")
    void testParameterizedConstructor() {
        BigDecimal thresholdPrice = new BigDecimal("150.00");
        PriceAlert.AlertCondition condition = PriceAlert.AlertCondition.ABOVE;

        PriceAlert paramAlert = new PriceAlert(testAsset, thresholdPrice, condition);
        
        assertEquals(testAsset, paramAlert.getAsset());
        assertEquals(thresholdPrice, paramAlert.getThresholdPrice());
        assertEquals(condition, paramAlert.getCondition());
        assertTrue(paramAlert.getIsActive());
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testSettersAndGetters() {
        BigDecimal thresholdPrice = new BigDecimal("150.00");
        PriceAlert.AlertCondition condition = PriceAlert.AlertCondition.BELOW;

        priceAlert.setAsset(testAsset);
        priceAlert.setThresholdPrice(thresholdPrice);
        priceAlert.setCondition(condition);
        priceAlert.setIsActive(false);
        priceAlert.setCreatedAt(testTime);
        priceAlert.setLastTriggeredAt(testTime.plusHours(1));

        assertEquals(testAsset, priceAlert.getAsset());
        assertEquals(thresholdPrice, priceAlert.getThresholdPrice());
        assertEquals(condition, priceAlert.getCondition());
        assertFalse(priceAlert.getIsActive());
        assertEquals(testTime, priceAlert.getCreatedAt());
        assertEquals(testTime.plusHours(1), priceAlert.getLastTriggeredAt());
    }

    @Test
    @DisplayName("Should handle null values in setters")
    void testNullSetters() {
        priceAlert.setAsset(null);
        priceAlert.setThresholdPrice(null);
        priceAlert.setCondition(null);
        priceAlert.setIsActive(null);
        priceAlert.setCreatedAt(null);
        priceAlert.setLastTriggeredAt(null);

        assertNull(priceAlert.getAsset());
        assertNull(priceAlert.getThresholdPrice());
        assertNull(priceAlert.getCondition());
        assertNull(priceAlert.getIsActive());
        assertNull(priceAlert.getCreatedAt());
        assertNull(priceAlert.getLastTriggeredAt());
    }

    @Test
    @DisplayName("Should handle all alert conditions")
    void testAlertConditions() {
        PriceAlert.AlertCondition[] conditions = {
            PriceAlert.AlertCondition.ABOVE,
            PriceAlert.AlertCondition.BELOW
        };
        
        for (PriceAlert.AlertCondition condition : conditions) {
            priceAlert.setCondition(condition);
            assertEquals(condition, priceAlert.getCondition());
        }
    }

    @Test
    @DisplayName("Should handle active/inactive status")
    void testActiveStatus() {
        assertTrue(priceAlert.getIsActive(), "Default isActive should be true");

        priceAlert.setIsActive(false);
        assertFalse(priceAlert.getIsActive());

        priceAlert.setIsActive(true);
        assertTrue(priceAlert.getIsActive());
    }

    @Test
    @DisplayName("Should implement equals correctly based on id")
    void testEquals() {
        PriceAlert alert1 = new PriceAlert();
        PriceAlert alert2 = new PriceAlert();
        PriceAlert alert3 = new PriceAlert();

        assertEquals(alert1, alert2, "Price alerts with null ids should be equal");
        assertEquals(alert1, alert3, "Price alerts with null ids should be equal");
        assertNotEquals(alert1, null, "Price alert should not equal null");
        assertNotEquals(alert1, "string", "Price alert should not equal different type");
        assertEquals(alert1, alert1, "Price alert should equal itself");
    }

    @Test
    @DisplayName("Should implement hashCode correctly based on id")
    void testHashCode() {
        PriceAlert alert1 = new PriceAlert();
        PriceAlert alert2 = new PriceAlert();
        PriceAlert alert3 = new PriceAlert();

        assertEquals(alert1.hashCode(), alert2.hashCode(), "Price alerts with null ids should have same hashCode");
        assertEquals(alert1.hashCode(), alert3.hashCode(), "Price alerts with null ids should have same hashCode");
    }

    @Test
    @DisplayName("Should handle hashCode with null id")
    void testHashCodeWithNullId() {
        PriceAlert alertWithNullId = new PriceAlert();
        
        assertEquals(0, alertWithNullId.hashCode(), "Price alert with null id should have hashCode 0");
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void testToString() {
        BigDecimal thresholdPrice = new BigDecimal("150.00");
        PriceAlert.AlertCondition condition = PriceAlert.AlertCondition.ABOVE;

        priceAlert.setAsset(testAsset);
        priceAlert.setThresholdPrice(thresholdPrice);
        priceAlert.setCondition(condition);
        priceAlert.setIsActive(true);
        priceAlert.setCreatedAt(testTime);
        priceAlert.setLastTriggeredAt(testTime.plusHours(1));

        String toString = priceAlert.toString();
        
        assertTrue(toString.contains("PriceAlert{"));
        assertTrue(toString.contains("id=null"));
        assertTrue(toString.contains("asset=AAPL"));
        assertTrue(toString.contains("thresholdPrice=" + thresholdPrice));
        assertTrue(toString.contains("condition=" + condition));
        assertTrue(toString.contains("isActive=true"));
        assertTrue(toString.contains("createdAt=" + testTime));
        assertTrue(toString.contains("lastTriggeredAt=" + testTime.plusHours(1)));
    }

    @Test
    @DisplayName("Should handle toString with null values")
    void testToStringWithNulls() {
        PriceAlert alertWithNulls = new PriceAlert();
        alertWithNulls.setAsset(null);
        alertWithNulls.setThresholdPrice(null);
        alertWithNulls.setCondition(null);
        alertWithNulls.setIsActive(null);
        alertWithNulls.setCreatedAt(null);
        alertWithNulls.setLastTriggeredAt(null);

        String toString = alertWithNulls.toString();
        
        assertTrue(toString.contains("PriceAlert{"));
        assertTrue(toString.contains("id=null"));
        assertTrue(toString.contains("asset=null"));
        assertTrue(toString.contains("thresholdPrice=null"));
        assertTrue(toString.contains("condition=null"));
        assertTrue(toString.contains("isActive=null"));
        assertTrue(toString.contains("createdAt=null"));
        assertTrue(toString.contains("lastTriggeredAt=null"));
    }

    @Test
    @DisplayName("Should handle BigDecimal precision correctly")
    void testBigDecimalPrecision() {
        BigDecimal precisePrice = new BigDecimal("123.456789");

        priceAlert.setThresholdPrice(precisePrice);
        assertEquals(precisePrice, priceAlert.getThresholdPrice());
    }

    @Test
    @DisplayName("Should handle asset relationship correctly")
    void testAssetRelationship() {
        Assets asset1 = new Assets();
        asset1.setSymbol("AAPL");
        Assets asset2 = new Assets();
        asset2.setSymbol("GOOGL");

        priceAlert.setAsset(asset1);
        assertEquals(asset1, priceAlert.getAsset());
        assertEquals("AAPL", priceAlert.getAsset().getSymbol());

        priceAlert.setAsset(asset2);
        assertEquals(asset2, priceAlert.getAsset());
        assertEquals("GOOGL", priceAlert.getAsset().getSymbol());
    }

    @Test
    @DisplayName("Should handle different timestamps")
    void testTimestamps() {
        LocalDateTime[] timestamps = {
            LocalDateTime.of(2024, 1, 1, 9, 30),
            LocalDateTime.of(2024, 6, 15, 14, 45),
            LocalDateTime.of(2024, 12, 31, 16, 0)
        };

        for (LocalDateTime timestamp : timestamps) {
            priceAlert.setCreatedAt(timestamp);
            assertEquals(timestamp, priceAlert.getCreatedAt());

            priceAlert.setLastTriggeredAt(timestamp.plusHours(1));
            assertEquals(timestamp.plusHours(1), priceAlert.getLastTriggeredAt());
        }
    }

    @Test
    @DisplayName("Should handle zero and negative threshold prices")
    void testThresholdPriceValues() {
        priceAlert.setThresholdPrice(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, priceAlert.getThresholdPrice());

        priceAlert.setThresholdPrice(new BigDecimal("-100.50"));
        assertEquals(new BigDecimal("-100.50"), priceAlert.getThresholdPrice());
    }

    @Test
    @DisplayName("Should handle very large threshold prices")
    void testLargeThresholdPrices() {
        BigDecimal largePrice = new BigDecimal("999999999.99");
        priceAlert.setThresholdPrice(largePrice);
        assertEquals(largePrice, priceAlert.getThresholdPrice());
    }

    @Test
    @DisplayName("Should handle AlertCondition enum values")
    void testAlertConditionEnum() {
        assertEquals("ABOVE", PriceAlert.AlertCondition.ABOVE.name());
        assertEquals("BELOW", PriceAlert.AlertCondition.BELOW.name());
        
        PriceAlert.AlertCondition[] values = PriceAlert.AlertCondition.values();
        assertEquals(2, values.length);
        assertTrue(values.length > 0);
    }
}

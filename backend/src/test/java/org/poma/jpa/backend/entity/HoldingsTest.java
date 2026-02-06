package org.poma.jpa.backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.util.ReflectionTestUtils;

class HoldingsTest {

    private Holdings holding;
    private User testUser;
    private Assets testAsset;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        holding = new Holdings();
        testUser = new User("Test User");
        testAsset = new Assets();
        testAsset.setSymbol("AAPL");
        testTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
    }

    @Test
    @DisplayName("Should create holding with default constructor")
    void testDefaultConstructor() {
        assertNotNull(holding);
        assertNull(holding.getId());
        assertNull(holding.getUserId());
        assertNull(holding.getPortfolioOwner());
        assertNull(holding.getAsset());
        assertNull(holding.getQuantity());
        assertNull(holding.getAvgBuyPrice());
        assertNull(holding.getCurrentPrice());
        assertNull(holding.getMarketValue());
        assertNull(holding.getAddedAt());
    }

    @Test
    @DisplayName("Should create holding with parameterized constructor")
    void testParameterizedConstructor() {
        BigDecimal quantity = new BigDecimal("100");
        BigDecimal avgBuyPrice = new BigDecimal("150.50");
        BigDecimal currentPrice = new BigDecimal("160.00");

        Holdings paramHolding = new Holdings(testUser, testAsset, quantity, avgBuyPrice, currentPrice);
        
        assertEquals(testUser, paramHolding.getPortfolioOwner());
        assertEquals(testAsset, paramHolding.getAsset());
        assertEquals(quantity, paramHolding.getQuantity());
        assertEquals(avgBuyPrice, paramHolding.getAvgBuyPrice());
        assertEquals(currentPrice, paramHolding.getCurrentPrice());
    }

    @Test
    @DisplayName("Should create holding with legacy constructor")
    void testLegacyConstructor() {
        BigDecimal zero = BigDecimal.ZERO;
        Holdings legacyHolding = new Holdings(testAsset, zero);
        
        assertNull(legacyHolding.getAsset());
        assertNull(legacyHolding.getPortfolioOwner());
        assertNull(legacyHolding.getQuantity());
        assertNull(legacyHolding.getAvgBuyPrice());
        assertNull(legacyHolding.getCurrentPrice());
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testSettersAndGetters() {
        holding.setUserId(1L);
        holding.setPortfolioOwner(testUser);
        holding.setAsset(testAsset);
        holding.setQuantity(new BigDecimal("100"));
        holding.setAvgBuyPrice(new BigDecimal("150.50"));
        holding.setCurrentPrice(new BigDecimal("160.00"));
        ReflectionTestUtils.setField(holding, "addedAt", testTime);

        assertEquals(1L, holding.getUserId());
        assertEquals(testUser, holding.getPortfolioOwner());
        assertEquals(testAsset, holding.getAsset());
        assertEquals(new BigDecimal("100"), holding.getQuantity());
        assertEquals(new BigDecimal("150.50"), holding.getAvgBuyPrice());
        assertEquals(new BigDecimal("160.00"), holding.getCurrentPrice());
        assertEquals(testTime, holding.getAddedAt());
    }

    @Test
    @DisplayName("Should handle null values in setters")
    void testNullSetters() {
        holding.setUserId(null);
        holding.setPortfolioOwner(null);
        holding.setAsset(null);
        holding.setQuantity(null);
        holding.setAvgBuyPrice(null);
        holding.setCurrentPrice(null);
        ReflectionTestUtils.setField(holding, "addedAt", null);

        assertNull(holding.getUserId());
        assertNull(holding.getPortfolioOwner());
        assertNull(holding.getAsset());
        assertNull(holding.getQuantity());
        assertNull(holding.getAvgBuyPrice());
        assertNull(holding.getCurrentPrice());
        assertNull(holding.getAddedAt());
    }

    @Test
    @DisplayName("Should implement equals correctly based on id")
    void testEquals() {
        Holdings holding1 = new Holdings();
        Holdings holding2 = new Holdings();
        Holdings holding3 = new Holdings();

        assertEquals(holding1, holding2, "Holdings with null ids should be equal");
        assertEquals(holding1, holding3, "Holdings with null ids should be equal");
        assertNotEquals(holding1, null, "Holding should not equal null");
        assertNotEquals(holding1, "string", "Holding should not equal different type");
        assertEquals(holding1, holding1, "Holding should equal itself");
    }

    @Test
    @DisplayName("Should implement hashCode correctly based on id")
    void testHashCode() {
        Holdings holding1 = new Holdings();
        Holdings holding2 = new Holdings();
        Holdings holding3 = new Holdings();

        assertEquals(holding1.hashCode(), holding2.hashCode(), "Holdings with null ids should have same hashCode");
        assertEquals(holding1.hashCode(), holding3.hashCode(), "Holdings with null ids should have same hashCode");
    }

    @Test
    @DisplayName("Should handle hashCode with null id")
    void testHashCodeWithNullId() {
        Holdings holdingWithNullId = new Holdings();
        
        assertEquals(0, holdingWithNullId.hashCode(), "Holding with null id should have hashCode 0");
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void testToString() {
        ReflectionTestUtils.setField(testUser, "id", 1L);
        ReflectionTestUtils.setField(testAsset, "id", 1L);
        holding.setUserId(1L);
        holding.setPortfolioOwner(testUser);
        holding.setAsset(testAsset);
        holding.setQuantity(new BigDecimal("100"));
        holding.setAvgBuyPrice(new BigDecimal("150.50"));
        holding.setCurrentPrice(new BigDecimal("160.00"));
        ReflectionTestUtils.setField(holding, "marketValue", new BigDecimal("16000.00"));
        ReflectionTestUtils.setField(holding, "addedAt", testTime);

        String toString = holding.toString();
        
        assertTrue(toString.contains("Holdings{"));
        assertTrue(toString.contains("id=null"));
        assertTrue(toString.contains("userId=1"));
        assertTrue(toString.contains("portfolioOwner=1"));
        assertTrue(toString.contains("asset=1"));
        assertTrue(toString.contains("quantity=100"));
        assertTrue(toString.contains("avgBuyPrice=150.50"));
        assertTrue(toString.contains("currentPrice=160.00"));
        assertTrue(toString.contains("marketValue=16000.00"));
        assertTrue(toString.contains("addedAt=" + testTime));
    }

    @Test
    @DisplayName("Should handle toString with null values")
    void testToStringWithNulls() {
        Holdings holdingWithNulls = new Holdings();
        holdingWithNulls.setUserId(null);
        holdingWithNulls.setPortfolioOwner(null);
        holdingWithNulls.setAsset(null);
        holdingWithNulls.setQuantity(null);
        holdingWithNulls.setAvgBuyPrice(null);
        holdingWithNulls.setCurrentPrice(null);
        ReflectionTestUtils.setField(holdingWithNulls, "marketValue", null);
        ReflectionTestUtils.setField(holdingWithNulls, "addedAt", null);

        String toString = holdingWithNulls.toString();
        
        assertTrue(toString.contains("Holdings{"));
        assertTrue(toString.contains("id=null"));
        assertTrue(toString.contains("userId=null"));
        assertTrue(toString.contains("portfolioOwner=null"));
        assertTrue(toString.contains("asset=null"));
        assertTrue(toString.contains("quantity=null"));
        assertTrue(toString.contains("avgBuyPrice=null"));
        assertTrue(toString.contains("currentPrice=null"));
        assertTrue(toString.contains("marketValue=null"));
        assertTrue(toString.contains("addedAt=null"));
    }

    @Test
    @DisplayName("Should handle BigDecimal precision correctly")
    void testBigDecimalPrecision() {
        BigDecimal preciseQuantity = new BigDecimal("123.456789");
        BigDecimal preciseAvgBuyPrice = new BigDecimal("123.456789");
        BigDecimal preciseCurrentPrice = new BigDecimal("123.456789");

        holding.setQuantity(preciseQuantity);
        holding.setAvgBuyPrice(preciseAvgBuyPrice);
        holding.setCurrentPrice(preciseCurrentPrice);

        assertEquals(preciseQuantity, holding.getQuantity());
        assertEquals(preciseAvgBuyPrice, holding.getAvgBuyPrice());
        assertEquals(preciseCurrentPrice, holding.getCurrentPrice());
    }

    @Test
    @DisplayName("Should handle user and asset relationships")
    void testRelationships() {
        User user1 = new User("User 1");
        User user2 = new User("User 2");

        Assets asset1 = new Assets();
        asset1.setSymbol("AAPL");
        Assets asset2 = new Assets();
        asset2.setSymbol("GOOGL");

        holding.setPortfolioOwner(user1);
        holding.setAsset(asset1);
        assertEquals(user1, holding.getPortfolioOwner());
        assertEquals(asset1, holding.getAsset());

        holding.setPortfolioOwner(user2);
        holding.setAsset(asset2);
        assertEquals(user2, holding.getPortfolioOwner());
        assertEquals(asset2, holding.getAsset());
    }

    @Test
    @DisplayName("Should handle zero values")
    void testZeroValues() {
        holding.setQuantity(BigDecimal.ZERO);
        holding.setAvgBuyPrice(BigDecimal.ZERO);
        holding.setCurrentPrice(BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, holding.getQuantity());
        assertEquals(BigDecimal.ZERO, holding.getAvgBuyPrice());
        assertEquals(BigDecimal.ZERO, holding.getCurrentPrice());
    }

    @Test
    @DisplayName("Should handle negative values")
    void testNegativeValues() {
        holding.setQuantity(new BigDecimal("-100"));
        holding.setAvgBuyPrice(new BigDecimal("-150.50"));
        holding.setCurrentPrice(new BigDecimal("-160.00"));

        assertEquals(new BigDecimal("-100"), holding.getQuantity());
        assertEquals(new BigDecimal("-150.50"), holding.getAvgBuyPrice());
        assertEquals(new BigDecimal("-160.00"), holding.getCurrentPrice());
    }

    @Test
    @DisplayName("Should handle very large values")
    void testLargeValues() {
        holding.setQuantity(new BigDecimal("1000000"));
        holding.setAvgBuyPrice(new BigDecimal("999999.99"));
        holding.setCurrentPrice(new BigDecimal("1000000.00"));

        assertEquals(new BigDecimal("1000000"), holding.getQuantity());
        assertEquals(new BigDecimal("999999.99"), holding.getAvgBuyPrice());
        assertEquals(new BigDecimal("1000000.00"), holding.getCurrentPrice());
    }
}

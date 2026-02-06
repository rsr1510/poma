package org.poma.jpa.backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserHistoryTest {

    private UserHistory userHistory;
    private User testUser;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        userHistory = new UserHistory();
        testUser = new User("Test User");
        testDate = LocalDate.of(2024, 1, 1);
    }

    @Test
    @DisplayName("Should create user history with default constructor")
    void testDefaultConstructor() {
        assertNotNull(userHistory);
        assertNull(userHistory.getId());
        assertNull(userHistory.getPortfolioOwner());
        assertNull(userHistory.getDate());
        assertNull(userHistory.getTotalValue());
    }

    @Test
    @DisplayName("Should create user history with parameterized constructor")
    void testParameterizedConstructor() {
        BigDecimal totalValue = new BigDecimal("50000.00");

        UserHistory paramHistory = new UserHistory(testUser, testDate, totalValue);
        
        assertEquals(testUser, paramHistory.getPortfolioOwner());
        assertEquals(LocalDate.now(), paramHistory.getDate());
        assertEquals(totalValue, paramHistory.getTotalValue());
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testSettersAndGetters() {
        BigDecimal totalValue = new BigDecimal("50000.00");

        userHistory.setPortfolioOwner(testUser);
        userHistory.setDate(testDate);
        userHistory.setTotalValue(totalValue);

        assertEquals(testUser, userHistory.getPortfolioOwner());
        assertEquals(testDate, userHistory.getDate());
        assertEquals(totalValue, userHistory.getTotalValue());
    }

    @Test
    @DisplayName("Should handle null values in setters")
    void testNullSetters() {
        userHistory.setPortfolioOwner(null);
        userHistory.setDate(null);
        userHistory.setTotalValue(null);

        assertNull(userHistory.getPortfolioOwner());
        assertNull(userHistory.getDate());
        assertNull(userHistory.getTotalValue());
    }

    @Test
    @DisplayName("Should implement equals correctly based on id")
    void testEquals() {
        UserHistory history1 = new UserHistory();
        UserHistory history2 = new UserHistory();
        UserHistory history3 = new UserHistory();

        assertEquals(history1, history2, "User histories with null ids should be equal");
        assertEquals(history1, history3, "User histories with null ids should be equal");
        assertNotEquals(history1, null, "User history should not equal null");
        assertNotEquals(history1, "string", "User history should not equal different type");
        assertEquals(history1, history1, "User history should equal itself");
    }

    @Test
    @DisplayName("Should implement hashCode correctly based on id")
    void testHashCode() {
        UserHistory history1 = new UserHistory();
        UserHistory history2 = new UserHistory();
        UserHistory history3 = new UserHistory();

        assertEquals(history1.hashCode(), history2.hashCode(), "User histories with null ids should have same hashCode");
        assertEquals(history1.hashCode(), history3.hashCode(), "User histories with null ids should have same hashCode");
    }

    @Test
    @DisplayName("Should handle hashCode with null id")
    void testHashCodeWithNullId() {
        UserHistory historyWithNullId = new UserHistory();
        
        assertEquals(0, historyWithNullId.hashCode(), "User history with null id should have hashCode 0");
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void testToString() {
        BigDecimal totalValue = new BigDecimal("50000.00");

        userHistory.setPortfolioOwner(testUser);
        userHistory.setDate(testDate);
        userHistory.setTotalValue(totalValue);

        String toString = userHistory.toString();
        
        assertTrue(toString.contains("UserHistory{"));
        assertTrue(toString.contains("id=null"));
        assertTrue(toString.contains("portfolioOwner=null"));
        assertTrue(toString.contains("date=" + testDate));
        assertTrue(toString.contains("totalValue=" + totalValue));
    }

    @Test
    @DisplayName("Should handle toString with null values")
    void testToStringWithNulls() {
        UserHistory historyWithNulls = new UserHistory();
        historyWithNulls.setPortfolioOwner(null);
        historyWithNulls.setDate(null);
        historyWithNulls.setTotalValue(null);

        String toString = historyWithNulls.toString();
        
        assertTrue(toString.contains("UserHistory{"));
        assertTrue(toString.contains("id=null"));
        assertTrue(toString.contains("portfolioOwner=null"));
        assertTrue(toString.contains("date=null"));
        assertTrue(toString.contains("totalValue=null"));
    }

    @Test
    @DisplayName("Should handle BigDecimal precision correctly")
    void testBigDecimalPrecision() {
        BigDecimal preciseValue = new BigDecimal("12345.67890");

        userHistory.setTotalValue(preciseValue);
        assertEquals(preciseValue, userHistory.getTotalValue());
    }

    @Test
    @DisplayName("Should handle user relationship correctly")
    void testUserRelationship() {
        User user1 = new User("User 1");
        User user2 = new User("User 2");

        userHistory.setPortfolioOwner(user1);
        assertEquals(user1, userHistory.getPortfolioOwner());
        assertEquals("User 1", userHistory.getPortfolioOwner().getName());

        userHistory.setPortfolioOwner(user2);
        assertEquals(user2, userHistory.getPortfolioOwner());
        assertEquals("User 2", userHistory.getPortfolioOwner().getName());
    }

    @Test
    @DisplayName("Should handle different dates")
    void testDifferentDates() {
        LocalDate[] dates = {
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 6, 15),
            LocalDate.of(2024, 12, 31)
        };

        for (LocalDate date : dates) {
            userHistory.setDate(date);
            assertEquals(date, userHistory.getDate());
        }
    }

    @Test
    @DisplayName("Should handle zero and negative values")
    void testValueRanges() {
        userHistory.setTotalValue(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, userHistory.getTotalValue());

        userHistory.setTotalValue(new BigDecimal("-1000.00"));
        assertEquals(new BigDecimal("-1000.00"), userHistory.getTotalValue());
    }

    @Test
    @DisplayName("Should handle very large values")
    void testLargeValues() {
        BigDecimal largeValue = new BigDecimal("999999999.99");
        userHistory.setTotalValue(largeValue);
        assertEquals(largeValue, userHistory.getTotalValue());
    }

    @Test
    @DisplayName("Should handle date edge cases")
    void testDateEdgeCases() {
        LocalDate minDate = LocalDate.MIN;
        LocalDate maxDate = LocalDate.MAX;
        LocalDate today = LocalDate.now();

        userHistory.setDate(minDate);
        assertEquals(minDate, userHistory.getDate());

        userHistory.setDate(maxDate);
        assertEquals(maxDate, userHistory.getDate());

        userHistory.setDate(today);
        assertEquals(today, userHistory.getDate());
    }

    @Test
    @DisplayName("Should handle decimal scaling")
    void testDecimalScaling() {
        BigDecimal[] values = {
            new BigDecimal("100"),
            new BigDecimal("100.5"),
            new BigDecimal("100.50"),
            new BigDecimal("100.505"),
            new BigDecimal("100.5050"),
            new BigDecimal("100.50500")
        };

        for (BigDecimal value : values) {
            userHistory.setTotalValue(value);
            assertEquals(value, userHistory.getTotalValue());
        }
    }
}

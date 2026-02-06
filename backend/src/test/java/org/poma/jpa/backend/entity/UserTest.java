package org.poma.jpa.backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        user = new User();
        testTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
    }

    @Test
    @DisplayName("Should create user with default constructor")
    void testDefaultConstructor() {
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getName());
        assertEquals(BigDecimal.ZERO, user.getTotalValue());
        assertEquals(BigDecimal.ZERO, user.getTotalReturnPct());
        assertNull(user.getCreatedAt());
    }

    @Test
    @DisplayName("Should create user with name constructor")
    void testConstructorWithName() {
        User userWithName = new User("John Doe");
        
        assertEquals("John Doe", userWithName.getName());
        assertNull(userWithName.getId());
        assertEquals(BigDecimal.ZERO, userWithName.getTotalValue());
        assertEquals(BigDecimal.ZERO, userWithName.getTotalReturnPct());
        assertNull(userWithName.getCreatedAt());
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testSettersAndGetters() {
        user.setName("Jane Smith");
        user.setTotalValue(new BigDecimal("10000.50"));
        user.setTotalReturnPct(new BigDecimal("15.25"));

        assertEquals("Jane Smith", user.getName());
        assertEquals(new BigDecimal("10000.50"), user.getTotalValue());
        assertEquals(new BigDecimal("15.25"), user.getTotalReturnPct());
        assertNull(user.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle null values in setters")
    void testNullSetters() {
        user.setName(null);
        user.setTotalValue(null);
        user.setTotalReturnPct(null);

        assertNull(user.getName());
        assertNull(user.getTotalValue());
        assertNull(user.getTotalReturnPct());
        assertNull(user.getCreatedAt());
    }

    @Test
    @DisplayName("Should implement equals correctly based on id")
    void testEquals() {
        User user1 = new User("Alice");
        User user2 = new User("Bob");
        User user3 = new User("Charlie");

        assertEquals(user1, user2, "Users with null ids should be equal");
        assertEquals(user1, user3, "Users with null ids should be equal");
        assertNotEquals(user1, null, "User should not equal null");
        assertNotEquals(user1, "string", "User should not equal different type");
        assertEquals(user1, user1, "User should equal itself");
    }

    @Test
    @DisplayName("Should implement hashCode correctly based on id")
    void testHashCode() {
        User user1 = new User("Alice");
        User user2 = new User("Bob");
        User user3 = new User("Charlie");

        assertEquals(user1.hashCode(), user2.hashCode(), "Users with null ids should have same hashCode");
        assertEquals(user1.hashCode(), user3.hashCode(), "Users with null ids should have same hashCode");
    }

    @Test
    @DisplayName("Should handle hashCode with null id")
    void testHashCodeWithNullId() {
        User userWithNullId = new User("Alice");
        
        assertEquals(0, userWithNullId.hashCode(), "User with null id should have hashCode 0");
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void testToString() {
        user.setName("Test User");
        user.setTotalValue(new BigDecimal("5000.00"));
        user.setTotalReturnPct(new BigDecimal("10.50"));

        String toString = user.toString();
        
        assertTrue(toString.contains("User{"));
        assertTrue(toString.contains("id=null"));
        assertTrue(toString.contains("name='Test User'"));
        assertTrue(toString.contains("totalValue=5000.00"));
        assertTrue(toString.contains("totalReturnPct=10.50"));
        assertTrue(toString.contains("createdAt=null"));
    }

    @Test
    @DisplayName("Should handle toString with null values")
    void testToStringWithNulls() {
        User userWithNulls = new User();
        userWithNulls.setName(null);
        userWithNulls.setTotalValue(null);
        userWithNulls.setTotalReturnPct(null);

        String toString = userWithNulls.toString();
        
        assertTrue(toString.contains("User{"));
        assertTrue(toString.contains("id=null"));
        assertTrue(toString.contains("name='null'"));
        assertTrue(toString.contains("totalValue=null"));
        assertTrue(toString.contains("totalReturnPct=null"));
        assertTrue(toString.contains("createdAt=null"));
    }

    @Test
    @DisplayName("Should maintain BigDecimal precision")
    void testBigDecimalPrecision() {
        BigDecimal preciseValue = new BigDecimal("12345.67890");
        BigDecimal preciseReturn = new BigDecimal("12.3456");

        user.setTotalValue(preciseValue);
        user.setTotalReturnPct(preciseReturn);

        assertEquals(preciseValue, user.getTotalValue());
        assertEquals(preciseReturn, user.getTotalReturnPct());
    }
}

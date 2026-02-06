package org.poma.jpa.backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.util.ReflectionTestUtils;

class NotificationTest {

    private Notification notification;
    private Assets testAsset;
    private PriceAlert testAlert;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        testAsset = new Assets();
        testAsset.setSymbol("AAPL");
        testAlert = new PriceAlert();
        testTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
    }

    @Test
    @DisplayName("Should create notification with default constructor")
    void testDefaultConstructor() {
        assertNotNull(notification);
        assertNull(notification.getId());
        assertNull(notification.getAsset());
        assertNull(notification.getAlert());
        assertNull(notification.getMessage());
        assertNull(notification.getTriggeredPrice());
        assertFalse(notification.getIsRead());
        assertNull(notification.getCreatedAt());
    }

    @Test
    @DisplayName("Should create notification with parameterized constructor")
    void testParameterizedConstructor() {
        BigDecimal triggeredPrice = new BigDecimal("155.50");
        String message = "Price alert triggered for AAPL";

        Notification paramNotification = new Notification(testAsset, testAlert, message, triggeredPrice);
        
        assertEquals(testAsset, paramNotification.getAsset());
        assertEquals(testAlert, paramNotification.getAlert());
        assertEquals(message, paramNotification.getMessage());
        assertEquals(triggeredPrice, paramNotification.getTriggeredPrice());
        assertFalse(paramNotification.getIsRead());
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testSettersAndGetters() {
        BigDecimal triggeredPrice = new BigDecimal("155.50");
        String message = "AAPL price reached $155.50";

        notification.setAsset(testAsset);
        notification.setAlert(testAlert);
        notification.setMessage(message);
        notification.setTriggeredPrice(triggeredPrice);
        notification.setIsRead(true);
        ReflectionTestUtils.setField(notification, "createdAt", testTime);

        assertEquals(testAsset, notification.getAsset());
        assertEquals(testAlert, notification.getAlert());
        assertEquals(message, notification.getMessage());
        assertEquals(triggeredPrice, notification.getTriggeredPrice());
        assertTrue(notification.getIsRead());
        assertEquals(testTime, notification.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle null values in setters")
    void testNullSetters() {
        notification.setAsset(null);
        notification.setAlert(null);
        notification.setMessage(null);
        notification.setTriggeredPrice(null);
        notification.setIsRead(null);
        ReflectionTestUtils.setField(notification, "createdAt", null);

        assertNull(notification.getAsset());
        assertNull(notification.getAlert());
        assertNull(notification.getMessage());
        assertNull(notification.getTriggeredPrice());
        assertNull(notification.getIsRead());
        assertNull(notification.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle read/unread status")
    void testReadStatus() {
        assertFalse(notification.getIsRead(), "Default isRead should be false");

        notification.setIsRead(true);
        assertTrue(notification.getIsRead());

        notification.setIsRead(false);
        assertFalse(notification.getIsRead());
    }

    @Test
    @DisplayName("Should implement equals correctly based on id")
    void testEquals() {
        Notification notification1 = new Notification();
        Notification notification2 = new Notification();
        Notification notification3 = new Notification();

        assertEquals(notification1, notification2, "Notifications with null ids should be equal");
        assertEquals(notification1, notification3, "Notifications with null ids should be equal");
        assertNotEquals(notification1, null, "Notification should not equal null");
        assertNotEquals(notification1, "string", "Notification should not equal different type");
        assertEquals(notification1, notification1, "Notification should equal itself");
    }

    @Test
    @DisplayName("Should implement hashCode correctly based on id")
    void testHashCode() {
        Notification notification1 = new Notification();
        Notification notification2 = new Notification();
        Notification notification3 = new Notification();

        assertEquals(notification1.hashCode(), notification2.hashCode(), "Notifications with null ids should have same hashCode");
        assertEquals(notification1.hashCode(), notification3.hashCode(), "Notifications with null ids should have same hashCode");
    }

    @Test
    @DisplayName("Should handle hashCode with null id")
    void testHashCodeWithNullId() {
        Notification notificationWithNullId = new Notification();
        
        assertEquals(0, notificationWithNullId.hashCode(), "Notification with null id should have hashCode 0");
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void testToString() {
        BigDecimal triggeredPrice = new BigDecimal("155.50");
        String message = "Price alert triggered";

        ReflectionTestUtils.setField(notification, "id", 1L);
        ReflectionTestUtils.setField(testAsset, "id", 1L);
        ReflectionTestUtils.setField(testAlert, "id", 1L);

        notification.setAsset(testAsset);
        notification.setAlert(testAlert);
        notification.setMessage(message);
        notification.setTriggeredPrice(triggeredPrice);
        notification.setIsRead(false);
        ReflectionTestUtils.setField(notification, "createdAt", testTime);

        String toString = notification.toString();
        
        assertTrue(toString.contains("Notification{"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("asset=AAPL"));
        assertTrue(toString.contains("alert=1"));
        assertTrue(toString.contains("message='" + message + "'"));
        assertTrue(toString.contains("triggeredPrice=" + triggeredPrice));
        assertTrue(toString.contains("isRead=false"));
        assertTrue(toString.contains("createdAt=" + testTime));
    }

    @Test
    @DisplayName("Should handle toString with null values")
    void testToStringWithNulls() {
        Notification notificationWithNulls = new Notification();
        notificationWithNulls.setAsset(null);
        notificationWithNulls.setAlert(null);
        notificationWithNulls.setMessage(null);
        notificationWithNulls.setTriggeredPrice(null);
        notificationWithNulls.setIsRead(null);
        ReflectionTestUtils.setField(notificationWithNulls, "createdAt", null);

        String toString = notificationWithNulls.toString();
        
        assertTrue(toString.contains("Notification{"));
        assertTrue(toString.contains("id=null"));
        assertTrue(toString.contains("asset=null"));
        assertTrue(toString.contains("alert=null"));
        assertTrue(toString.contains("message='null'"));
        assertTrue(toString.contains("triggeredPrice=null"));
        assertTrue(toString.contains("isRead=null"));
        assertTrue(toString.contains("createdAt=null"));
    }

    @Test
    @DisplayName("Should handle BigDecimal precision correctly")
    void testBigDecimalPrecision() {
        BigDecimal precisePrice = new BigDecimal("123.456789");

        notification.setTriggeredPrice(precisePrice);
        assertEquals(precisePrice, notification.getTriggeredPrice());
    }

    @Test
    @DisplayName("Should handle asset and alert relationships")
    void testRelationships() {
        Assets asset1 = new Assets();
        asset1.setSymbol("AAPL");
        Assets asset2 = new Assets();
        asset2.setSymbol("GOOGL");

        PriceAlert alert1 = new PriceAlert();
        PriceAlert alert2 = new PriceAlert();

        notification.setAsset(asset1);
        notification.setAlert(alert1);
        assertEquals(asset1, notification.getAsset());
        assertEquals(alert1, notification.getAlert());

        notification.setAsset(asset2);
        notification.setAlert(alert2);
        assertEquals(asset2, notification.getAsset());
        assertEquals(alert2, notification.getAlert());
    }

    @Test
    @DisplayName("Should handle different message lengths")
    void testMessageLengths() {
        String shortMessage = "Alert";
        String longMessage = "This is a very long notification message that contains detailed information about the price alert that was triggered for the asset";
        
        notification.setMessage(shortMessage);
        assertEquals(shortMessage, notification.getMessage());
        
        notification.setMessage(longMessage);
        assertEquals(longMessage, notification.getMessage());
    }

    @Test
    @DisplayName("Should handle zero and negative prices")
    void testPriceValues() {
        notification.setTriggeredPrice(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, notification.getTriggeredPrice());

        notification.setTriggeredPrice(new BigDecimal("-100.50"));
        assertEquals(new BigDecimal("-100.50"), notification.getTriggeredPrice());
    }

    @Test
    @DisplayName("Should handle very large price values")
    void testLargePriceValues() {
        BigDecimal largePrice = new BigDecimal("999999999.99");
        notification.setTriggeredPrice(largePrice);
        assertEquals(largePrice, notification.getTriggeredPrice());
    }
}

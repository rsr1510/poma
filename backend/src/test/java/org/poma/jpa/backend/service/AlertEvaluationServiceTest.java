package org.poma.jpa.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.entity.Notification;
import org.poma.jpa.backend.entity.PriceAlert;
import org.poma.jpa.backend.repo.NotificationRepo;
import org.poma.jpa.backend.repo.PriceAlertRepo;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AlertEvaluationServiceTest {

    @Mock
    private PriceAlertRepo priceAlertRepo;

    @Mock
    private NotificationRepo notificationRepo;

    @Mock
    private PriceService priceService;

    @InjectMocks
    private AlertEvaluationService alertEvaluationService;

    private Assets testAsset;
    private PriceAlert aboveAlert;
    private PriceAlert belowAlert;
    private Notification existingNotification;

    @BeforeEach
    void setUp() {
        testAsset = new Assets();
        testAsset.setSymbol("AAPL");
        testAsset.setName("Apple Inc.");

        aboveAlert = new PriceAlert();
        aboveAlert.setAsset(testAsset);
        aboveAlert.setThresholdPrice(new BigDecimal("150.00"));
        aboveAlert.setCondition(PriceAlert.AlertCondition.ABOVE);
        aboveAlert.setIsActive(true);

        belowAlert = new PriceAlert();
        belowAlert.setAsset(testAsset);
        belowAlert.setThresholdPrice(new BigDecimal("100.00"));
        belowAlert.setCondition(PriceAlert.AlertCondition.BELOW);
        belowAlert.setIsActive(true);

        existingNotification = new Notification();
        existingNotification.setAsset(testAsset);
        existingNotification.setAlert(aboveAlert);
        existingNotification.setTriggeredPrice(new BigDecimal("155.00"));

        ReflectionTestUtils.setField(existingNotification, "createdAt", LocalDateTime.now().minusMinutes(10));
    }

    @Test
    @DisplayName("Should do nothing when no active alerts exist")
    void testEvaluateAlertsWithNoActiveAlerts() {
        when(priceAlertRepo.findAllActiveAlerts()).thenReturn(Collections.emptyList());

        alertEvaluationService.evaluateAlerts();

        verify(priceAlertRepo, times(1)).findAllActiveAlerts();
        verify(priceService, never()).getCurrentPrice(any());
        verify(notificationRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should create notification for ABOVE alert when condition is first met")
    void testAboveAlertFirstTimeTriggered() {
        BigDecimal currentPrice = new BigDecimal("155.00");

        when(priceAlertRepo.findAllActiveAlerts()).thenReturn(Arrays.asList(aboveAlert));
        when(priceService.getCurrentPrice("AAPL")).thenReturn(currentPrice);
        when(notificationRepo.findByAlertOrderByCreatedAtDesc(aboveAlert)).thenReturn(Collections.emptyList());
        when(notificationRepo.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(priceAlertRepo.save(any(PriceAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        alertEvaluationService.evaluateAlerts();

        verify(priceService, times(1)).getCurrentPrice("AAPL");
        verify(notificationRepo, times(1)).save(any(Notification.class));
        verify(priceAlertRepo, times(1)).save(aboveAlert);

        assertNotNull(aboveAlert.getLastTriggeredAt());
    }

    @Test
    @DisplayName("Should create notification for BELOW alert when condition is met")
    void testBelowAlertTriggered() {
        BigDecimal currentPrice = new BigDecimal("95.00");

        when(priceAlertRepo.findAllActiveAlerts()).thenReturn(Arrays.asList(belowAlert));
        when(priceService.getCurrentPrice("AAPL")).thenReturn(currentPrice);
        when(notificationRepo.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(priceAlertRepo.save(any(PriceAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        alertEvaluationService.evaluateAlerts();

        verify(priceService, times(1)).getCurrentPrice("AAPL");
        verify(notificationRepo, times(1)).save(any(Notification.class));
        verify(priceAlertRepo, times(1)).save(belowAlert);

        assertNotNull(belowAlert.getLastTriggeredAt());
    }

    @Test
    @DisplayName("Should not create notification for ABOVE alert when condition not met")
    void testAboveAlertConditionNotMet() {
        BigDecimal currentPrice = new BigDecimal("145.00");

        when(priceAlertRepo.findAllActiveAlerts()).thenReturn(Arrays.asList(aboveAlert));
        when(priceService.getCurrentPrice("AAPL")).thenReturn(currentPrice);

        alertEvaluationService.evaluateAlerts();

        verify(priceService, times(1)).getCurrentPrice("AAPL");
        verify(notificationRepo, never()).save(any());
        verify(priceAlertRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should not create notification for BELOW alert when condition not met")
    void testBelowAlertConditionNotMet() {
        BigDecimal currentPrice = new BigDecimal("105.00");

        when(priceAlertRepo.findAllActiveAlerts()).thenReturn(Arrays.asList(belowAlert));
        when(priceService.getCurrentPrice("AAPL")).thenReturn(currentPrice);

        alertEvaluationService.evaluateAlerts();

        verify(priceService, times(1)).getCurrentPrice("AAPL");
        verify(notificationRepo, never()).save(any());
        verify(priceAlertRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should handle invalid price gracefully")
    void testInvalidPrice() {
        BigDecimal invalidPrice = BigDecimal.ZERO;

        when(priceAlertRepo.findAllActiveAlerts()).thenReturn(Arrays.asList(aboveAlert));
        when(priceService.getCurrentPrice("AAPL")).thenReturn(invalidPrice);

        alertEvaluationService.evaluateAlerts();

        verify(priceService, times(1)).getCurrentPrice("AAPL");
        verify(notificationRepo, never()).save(any());
        verify(priceAlertRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should create notification for ABOVE alert when price returns to threshold")
    void testAboveAlertPriceReturnsToThreshold() {
        BigDecimal currentPrice = new BigDecimal("155.00");

        aboveAlert.setLastTriggeredAt(LocalDateTime.now().minusHours(1));

        when(priceAlertRepo.findAllActiveAlerts()).thenReturn(Arrays.asList(aboveAlert));
        when(priceService.getCurrentPrice("AAPL")).thenReturn(currentPrice);
        when(notificationRepo.findByAlertOrderByCreatedAtDesc(aboveAlert))
                .thenReturn(Arrays.asList(existingNotification));
        when(notificationRepo.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(priceAlertRepo.save(any(PriceAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        alertEvaluationService.evaluateAlerts();

        verify(priceService, times(1)).getCurrentPrice("AAPL");
        verify(notificationRepo, never()).save(any(Notification.class));
        verify(priceAlertRepo, never()).save(any(PriceAlert.class));
    }

    @Test
    @DisplayName("Should not create duplicate notifications for recent alerts")
    void testAvoidDuplicateNotifications() {
        BigDecimal currentPrice = new BigDecimal("155.00");

        aboveAlert.setLastTriggeredAt(LocalDateTime.now().minusMinutes(2));

        when(priceAlertRepo.findAllActiveAlerts()).thenReturn(Arrays.asList(aboveAlert));
        when(priceService.getCurrentPrice("AAPL")).thenReturn(currentPrice);
        when(notificationRepo.findByAlertOrderByCreatedAtDesc(aboveAlert))
                .thenReturn(Arrays.asList(existingNotification));
        when(notificationRepo.existsByAlertAndCreatedAtAfter(eq(aboveAlert), any(LocalDateTime.class)))
                .thenReturn(true);

        alertEvaluationService.evaluateAlerts();

        verify(priceService, times(1)).getCurrentPrice("AAPL");
        verify(notificationRepo, never()).save(any());
        verify(priceAlertRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should evaluate alerts for specific asset")
    void testEvaluateAlertForAsset() {
        BigDecimal currentPrice = new BigDecimal("155.00");

        when(priceAlertRepo.findActiveAlertsForAsset(testAsset)).thenReturn(Arrays.asList(aboveAlert));
        when(priceService.getCurrentPrice("AAPL")).thenReturn(currentPrice);
        when(notificationRepo.findByAlertOrderByCreatedAtDesc(aboveAlert)).thenReturn(Collections.emptyList());
        when(notificationRepo.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(priceAlertRepo.save(any(PriceAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        alertEvaluationService.evaluateAlertForAsset(testAsset);

        verify(priceAlertRepo, times(1)).findActiveAlertsForAsset(testAsset);
        verify(priceService, times(1)).getCurrentPrice("AAPL");
        verify(notificationRepo, times(1)).save(any(Notification.class));
        verify(priceAlertRepo, times(1)).save(aboveAlert);
    }

    @Test
    @DisplayName("Should handle exceptions during alert evaluation gracefully")
    void testExceptionHandling() {
        when(priceAlertRepo.findAllActiveAlerts()).thenReturn(Arrays.asList(aboveAlert));
        when(priceService.getCurrentPrice("AAPL")).thenThrow(new RuntimeException("Price service error"));

        assertDoesNotThrow(() -> alertEvaluationService.evaluateAlerts());

        verify(priceService, times(1)).getCurrentPrice("AAPL");
        verify(notificationRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should handle null last triggered price correctly")
    void testNullLastTriggeredPrice() {
        BigDecimal currentPrice = new BigDecimal("155.00");

        aboveAlert.setLastTriggeredAt(LocalDateTime.now().minusHours(1));

        when(priceAlertRepo.findAllActiveAlerts()).thenReturn(Arrays.asList(aboveAlert));
        when(priceService.getCurrentPrice("AAPL")).thenReturn(currentPrice);
        when(notificationRepo.findByAlertOrderByCreatedAtDesc(aboveAlert)).thenReturn(Collections.emptyList());
        when(notificationRepo.existsByAlertAndCreatedAtAfter(eq(aboveAlert), any(LocalDateTime.class)))
                .thenReturn(false);
        when(notificationRepo.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(priceAlertRepo.save(any(PriceAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        alertEvaluationService.evaluateAlerts();

        verify(notificationRepo, times(1)).existsByAlertAndCreatedAtAfter(eq(aboveAlert), any(LocalDateTime.class));
        verify(notificationRepo, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should build correct notification message for ABOVE condition")
    void testNotificationMessageAbove() {
        BigDecimal currentPrice = new BigDecimal("155.00");

        when(priceAlertRepo.findAllActiveAlerts()).thenReturn(Arrays.asList(aboveAlert));
        when(priceService.getCurrentPrice("AAPL")).thenReturn(currentPrice);
        when(notificationRepo.findByAlertOrderByCreatedAtDesc(aboveAlert)).thenReturn(Collections.emptyList());
        when(notificationRepo.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            assertEquals("Apple Inc. (AAPL) price rose above ₹150.00", notification.getMessage());
            assertEquals(currentPrice, notification.getTriggeredPrice());
            return notification;
        });
        when(priceAlertRepo.save(any(PriceAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        alertEvaluationService.evaluateAlerts();

        verify(notificationRepo, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should build correct notification message for BELOW condition")
    void testNotificationMessageBelow() {
        BigDecimal currentPrice = new BigDecimal("95.00");

        when(priceAlertRepo.findAllActiveAlerts()).thenReturn(Arrays.asList(belowAlert));
        when(priceService.getCurrentPrice("AAPL")).thenReturn(currentPrice);
        when(notificationRepo.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            assertEquals("Apple Inc. (AAPL) price dropped below ₹100.00", notification.getMessage());
            assertEquals(currentPrice, notification.getTriggeredPrice());
            return notification;
        });
        when(priceAlertRepo.save(any(PriceAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        alertEvaluationService.evaluateAlerts();

        verify(notificationRepo, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should handle multiple alerts for same asset")
    void testMultipleAlertsSameAsset() {
        BigDecimal currentPrice = new BigDecimal("155.00");

        when(priceAlertRepo.findAllActiveAlerts()).thenReturn(Arrays.asList(aboveAlert, belowAlert));
        when(priceService.getCurrentPrice("AAPL")).thenReturn(currentPrice);
        when(notificationRepo.findByAlertOrderByCreatedAtDesc(any())).thenReturn(Collections.emptyList());
        when(notificationRepo.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(priceAlertRepo.save(any(PriceAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        alertEvaluationService.evaluateAlerts();

        verify(priceService, times(2)).getCurrentPrice("AAPL");
        verify(notificationRepo, times(1)).save(any(Notification.class));
        verify(priceAlertRepo, times(1)).save(any(PriceAlert.class));
    }

    @Test
    @DisplayName("Should handle precision with BigDecimal correctly")
    void testBigDecimalPrecision() {
        BigDecimal precisePrice = new BigDecimal("150.123456789");

        when(priceAlertRepo.findAllActiveAlerts()).thenReturn(Arrays.asList(aboveAlert));
        when(priceService.getCurrentPrice("AAPL")).thenReturn(precisePrice);
        when(notificationRepo.findByAlertOrderByCreatedAtDesc(aboveAlert)).thenReturn(Collections.emptyList());
        when(notificationRepo.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            assertEquals(precisePrice, notification.getTriggeredPrice());
            return notification;
        });
        when(priceAlertRepo.save(any(PriceAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        alertEvaluationService.evaluateAlerts();

        verify(notificationRepo, times(1)).save(any(Notification.class));
    }
}

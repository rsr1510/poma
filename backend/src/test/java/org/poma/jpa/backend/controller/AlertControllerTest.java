package org.poma.jpa.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.poma.jpa.backend.dto.AlertRequest;
import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.entity.PriceAlert;
import org.poma.jpa.backend.repo.AssetRepo;
import org.poma.jpa.backend.repo.PriceAlertRepo;
import org.poma.jpa.backend.service.AlertService;
import org.poma.jpa.backend.service.AlertEvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AlertControllerTest {

    @InjectMocks
    private AlertController alertController;

    @Mock
    private AlertService alertService;

    @Mock
    private PriceAlertRepo priceAlertRepo;

    @Mock
    private AssetRepo assetRepo;

    @Mock
    private AlertEvaluationService alertEvaluationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(alertController).build();
    }

    @Test
    void testCreateAlert() throws Exception {
        AlertRequest alertRequest = new AlertRequest();
        PriceAlert priceAlert = new PriceAlert();
        ReflectionTestUtils.setField(priceAlert, "id", 1L);

        when(alertService.createAlert(any(AlertRequest.class))).thenReturn(priceAlert);

        mockMvc.perform(post("/api/alerts")
                        .contentType("application/json")
                        .content("{\"someField\":\"value\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetAllAlerts() throws Exception {
        PriceAlert priceAlert = new PriceAlert();
        List<PriceAlert> alerts = Collections.singletonList(priceAlert);

        when(alertService.getAllAlerts()).thenReturn(alerts);

        mockMvc.perform(get("/api/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").isNotEmpty());
    }

    @Test
    void testGetActiveAlerts() throws Exception {
        PriceAlert priceAlert = new PriceAlert();
        List<PriceAlert> alerts = Collections.singletonList(priceAlert);

        when(alertService.getActiveAlerts()).thenReturn(alerts);

        mockMvc.perform(get("/api/alerts/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").isNotEmpty());
    }

    @Test
    void testGetAlertsForAsset() throws Exception {
        PriceAlert priceAlert = new PriceAlert();
        List<PriceAlert> alerts = Collections.singletonList(priceAlert);

        when(alertService.getAlertsForAsset(1L)).thenReturn(alerts);

        mockMvc.perform(get("/api/alerts/asset/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").isNotEmpty());
    }

    @Test
    void testUpdateAlert() throws Exception {
        AlertRequest alertRequest = new AlertRequest();
        PriceAlert priceAlert = new PriceAlert();
        ReflectionTestUtils.setField(priceAlert, "id", 1L);

        when(alertService.updateAlert(eq(1L), any(AlertRequest.class))).thenReturn(priceAlert);

        mockMvc.perform(put("/api/alerts/1")
                        .contentType("application/json")
                        .content("{\"someField\":\"value\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testToggleAlert() throws Exception {
        PriceAlert priceAlert = new PriceAlert();
        ReflectionTestUtils.setField(priceAlert, "id", 1L);

        when(alertService.toggleAlert(1L)).thenReturn(priceAlert);

        mockMvc.perform(patch("/api/alerts/1/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testDeleteAlert() throws Exception {
        doNothing().when(alertService).deleteAlert(1L);

        mockMvc.perform(delete("/api/alerts/1"))
                .andExpect(status().isNoContent());

        verify(alertService, times(1)).deleteAlert(1L);
    }

    @Test
    void testDeleteAlertsByAsset() throws Exception {
        Assets asset = new Assets();
        ReflectionTestUtils.setField(asset, "id", 1L);
        when(assetRepo.findById(1L)).thenReturn(Optional.of(asset));
        when(priceAlertRepo.findByAsset(asset)).thenReturn(Collections.emptyList());

        mockMvc.perform(delete("/api/alerts/asset/1"))
                .andExpect(status().isNoContent());

        verify(assetRepo, times(1)).findById(1L);
        verify(priceAlertRepo, times(1)).findByAsset(asset);
        verify(priceAlertRepo, times(1)).deleteAll(anyList());
    }

    @Test
    void testTestAlertEvaluation() throws Exception {
        doNothing().when(alertEvaluationService).evaluateAlerts();

        mockMvc.perform(post("/api/alerts/test-evaluation"))
                .andExpect(status().isOk())
                .andExpect(content().string("Alert evaluation triggered successfully"));
    }
}

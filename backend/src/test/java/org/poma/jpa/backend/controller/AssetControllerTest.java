package org.poma.jpa.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.entity.AssetType;
import org.poma.jpa.backend.exceptions.ResourceNotFoundException;
import org.poma.jpa.backend.exceptions.GlobalExceptionHandler;
import org.poma.jpa.backend.service.AssetService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AssetControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private AssetController assetController;

    @Mock
    private AssetService assetService;

    private ObjectMapper objectMapper;

    private Assets testAsset;
    private Assets newAsset;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(assetController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        testAsset = new Assets();
        ReflectionTestUtils.setField(testAsset, "id", 1L);
        testAsset.setSymbol("AAPL");
        testAsset.setName("Apple Inc.");
        testAsset.setType(AssetType.STOCK);
        testAsset.setUnits(100);
        testAsset.setPricePerUnit(150.50);
        testAsset.setPlatformFee(5.0);

        newAsset = new Assets();
        newAsset.setSymbol("GOOGL");
        newAsset.setName("Alphabet Inc.");
        newAsset.setType(AssetType.STOCK);
        newAsset.setUnits(50);
        newAsset.setPricePerUnit(2500.75);
        newAsset.setPlatformFee(10.0);
    }

    @Test
    @DisplayName("Should return all assets when GET /api/assets is called")
    void testGetAllAssets() throws Exception {
        List<Assets> assets = Arrays.asList(testAsset, newAsset);
        when(assetService.findAll()).thenReturn(assets);

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].symbol").value("AAPL"))
                .andExpect(jsonPath("$[0].name").value("Apple Inc."))
                .andExpect(jsonPath("$[0].type").value("STOCK"))
                .andExpect(jsonPath("$[1].symbol").value("GOOGL"));

        verify(assetService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when no assets found")
    void testGetAllAssetsEmpty() throws Exception {
        when(assetService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("No assets found")));

        verify(assetService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return asset when GET /api/assets/{id} is called with valid id")
    void testGetAssetById() throws Exception {
        when(assetService.findById(1L)).thenReturn(testAsset);

        mockMvc.perform(get("/api/assets/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.name").value("Apple Inc."))
                .andExpect(jsonPath("$.type").value("STOCK"))
                .andExpect(jsonPath("$.units").value(100))
                .andExpect(jsonPath("$.pricePerUnit").value(150.50))
                .andExpect(jsonPath("$.platformFee").value(5.0))
                .andExpect(jsonPath("$.finalValue").value(15055.0));

        verify(assetService, times(2)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when GET /api/assets/{id} is called with non-existing id")
    void testGetAssetByIdNotFound() throws Exception {
        when(assetService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Asset not found with id: 999"));

        mockMvc.perform(get("/api/assets/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Asset not found with id: 999")));

        verify(assetService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create asset when POST /api/assets is called with valid data")
    void testCreateAsset() throws Exception {
        Assets savedAsset = new Assets();
        ReflectionTestUtils.setField(savedAsset, "id", 2L);
        savedAsset.setSymbol("GOOGL");
        savedAsset.setName("Alphabet Inc.");
        savedAsset.setType(AssetType.STOCK);
        savedAsset.setUnits(50);
        savedAsset.setPricePerUnit(2500.75);
        savedAsset.setPlatformFee(10.0);

        when(assetService.create(any(Assets.class))).thenReturn(savedAsset);

        mockMvc.perform(post("/api/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAsset)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.symbol").value("GOOGL"))
                .andExpect(jsonPath("$.name").value("Alphabet Inc."))
                .andExpect(jsonPath("$.type").value("STOCK"))
                .andExpect(jsonPath("$.units").value(50))
                .andExpect(jsonPath("$.pricePerUnit").value(2500.75))
                .andExpect(jsonPath("$.platformFee").value(10.0))
                .andExpect(jsonPath("$.finalValue").value(125047.5))
                .andExpect(header().exists("Location"));

        verify(assetService, times(1)).create(any(Assets.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when POST /api/assets is called with null name")
    void testCreateAssetWithNullName() throws Exception {
        Assets assetWithNullName = new Assets();
        assetWithNullName.setName(null);

        mockMvc.perform(post("/api/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assetWithNullName)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Asset name is required")));

        verify(assetService, never()).create(any());
    }

    @Test
    @DisplayName("Should update asset when PUT /api/assets/{id} is called with valid data")
    void testUpdateAsset() throws Exception {
        Assets updatedAsset = new Assets();
        ReflectionTestUtils.setField(updatedAsset, "id", 1L);
        updatedAsset.setSymbol("AAPL");
        updatedAsset.setName("Updated Apple");
        updatedAsset.setType(AssetType.STOCK);
        updatedAsset.setUnits(120);
        updatedAsset.setPricePerUnit(155.75);
        updatedAsset.setPlatformFee(6.0);

        when(assetService.update(eq(1L), any(Assets.class))).thenReturn(updatedAsset);

        mockMvc.perform(put("/api/assets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedAsset)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Apple"))
                .andExpect(jsonPath("$.pricePerUnit").value(155.75));

        verify(assetService, times(1)).update(eq(1L), any(Assets.class));
    }
}

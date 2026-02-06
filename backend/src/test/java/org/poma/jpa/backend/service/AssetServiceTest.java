package org.poma.jpa.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.entity.AssetType;
import org.poma.jpa.backend.repo.AssetRepo;
import org.poma.jpa.backend.exceptions.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetRepo assetRepo;

    @InjectMocks
    private AssetService assetService;

    private Assets testAsset;
    private Assets updatedAsset;

    @BeforeEach
    void setUp() {
        testAsset = new Assets();
        testAsset.setSymbol("AAPL");
        testAsset.setName("Apple Inc.");
        testAsset.setType(AssetType.STOCK);
        testAsset.setUnits(100);
        testAsset.setPricePerUnit(150.50);
        testAsset.setPlatformFee(5.0);

        updatedAsset = new Assets();
        updatedAsset.setSymbol("GOOGL");
        updatedAsset.setName("Alphabet Inc.");
        updatedAsset.setType(AssetType.STOCK);
        updatedAsset.setUnits(50);
        updatedAsset.setPricePerUnit(2500.75);
        updatedAsset.setPlatformFee(10.0);
    }

    @Test
    @DisplayName("Should return all assets when findAll is called")
    void testFindAll() {
        List<Assets> expectedAssets = Arrays.asList(testAsset, updatedAsset);
        when(assetRepo.findAll()).thenReturn(expectedAssets);

        List<Assets> actualAssets = assetService.findAll();

        assertEquals(expectedAssets, actualAssets);
        verify(assetRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return asset when findById is called with valid id")
    void testFindByIdWithValidId() {
        when(assetRepo.findById(1L)).thenReturn(Optional.of(testAsset));

        Assets actualAsset = assetService.findById(1L);

        assertEquals(testAsset, actualAsset);
        verify(assetRepo, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when findById is called with invalid id")
    void testFindByIdWithInvalidId() {
        when(assetRepo.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> assetService.findById(999L)
        );

        assertEquals("Asset not found with id: 999", exception.getMessage());
        verify(assetRepo, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create asset when valid asset is provided")
    void testCreateWithValidAsset() {
        Assets newAsset = new Assets();
        newAsset.setSymbol("MSFT");
        newAsset.setName("Microsoft Corporation");
        newAsset.setType(AssetType.STOCK);
        newAsset.setUnits(75);
        newAsset.setPricePerUnit(300.25);
        newAsset.setPlatformFee(7.5);

        Assets savedAsset = new Assets();
        savedAsset.setSymbol("MSFT");
        savedAsset.setName("Microsoft Corporation");
        savedAsset.setType(AssetType.STOCK);
        savedAsset.setUnits(75);
        savedAsset.setPricePerUnit(300.25);
        savedAsset.setPlatformFee(7.5);

        when(assetRepo.findBySymbol("MSFT")).thenReturn(Optional.empty());
        when(assetRepo.save(any(Assets.class))).thenReturn(savedAsset);

        Assets actualAsset = assetService.create(newAsset);

        assertNotNull(actualAsset);
        assertEquals("MSFT", actualAsset.getSymbol());
        assertEquals("Microsoft Corporation", actualAsset.getName());
        assertEquals(AssetType.STOCK, actualAsset.getType());
        assertEquals(75, actualAsset.getUnits());
        assertEquals(300.25, actualAsset.getPricePerUnit());
        assertEquals(7.5, actualAsset.getPlatformFee());
        verify(assetRepo, times(1)).findBySymbol("MSFT");
        verify(assetRepo, times(1)).save(newAsset);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when create is called with null asset")
    void testCreateWithNullAsset() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> assetService.create(null)
        );

        assertEquals("Asset must not be null", exception.getMessage());
        verify(assetRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when create is called with existing symbol")
    void testCreateWithExistingSymbol() {
        Assets newAsset = new Assets();
        newAsset.setSymbol("AAPL");
        newAsset.setName("Apple Inc.");
        newAsset.setType(AssetType.STOCK);
        newAsset.setUnits(100);
        newAsset.setPricePerUnit(150.50);
        newAsset.setPlatformFee(5.0);

        when(assetRepo.findBySymbol("AAPL")).thenReturn(Optional.of(testAsset));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> assetService.create(newAsset)
        );

        assertEquals("Asset with symbol already exists: AAPL", exception.getMessage());
        verify(assetRepo, times(1)).findBySymbol("AAPL");
        verify(assetRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should update asset when valid id and asset are provided")
    void testUpdateWithValidIdAndAsset() {
        Assets existingAsset = new Assets();
        existingAsset.setSymbol("AAPL");
        existingAsset.setName("Apple Inc.");
        existingAsset.setType(AssetType.STOCK);
        existingAsset.setUnits(100);
        existingAsset.setPricePerUnit(150.50);
        existingAsset.setPlatformFee(5.0);

        Assets updateData = new Assets();
        updateData.setSymbol("NEW");
        updateData.setName("New Name");
        updateData.setType(AssetType.CRYPTO);
        updateData.setUnits(200);
        updateData.setPricePerUnit(300.00);
        updateData.setPlatformFee(10.0);

        when(assetRepo.findById(1L)).thenReturn(Optional.of(existingAsset));
        when(assetRepo.save(any(Assets.class))).thenReturn(existingAsset);

        Assets actualAsset = assetService.update(1L, updateData);

        assertEquals("NEW", actualAsset.getSymbol());
        assertEquals("New Name", actualAsset.getName());
        assertEquals(AssetType.CRYPTO, actualAsset.getType());
        assertEquals(200, actualAsset.getUnits());
        assertEquals(300.00, actualAsset.getPricePerUnit());
        assertEquals(10.0, actualAsset.getPlatformFee());
        verify(assetRepo, times(1)).findById(1L);
        verify(assetRepo, times(1)).save(existingAsset);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when update is called with invalid id")
    void testUpdateWithInvalidId() {
        when(assetRepo.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> assetService.update(999L, updatedAsset)
        );

        assertEquals("Asset not found with id: 999", exception.getMessage());
        verify(assetRepo, times(1)).findById(999L);
        verify(assetRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should delete asset when valid id is provided")
    void testDeleteWithValidId() {
        when(assetRepo.findById(1L)).thenReturn(Optional.of(testAsset));
        doNothing().when(assetRepo).delete(testAsset);

        assetService.delete(1L);

        verify(assetRepo, times(1)).findById(1L);
        verify(assetRepo, times(1)).delete(testAsset);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when delete is called with invalid id")
    void testDeleteWithInvalidId() {
        when(assetRepo.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> assetService.delete(999L)
        );

        assertEquals("Asset not found with id: 999", exception.getMessage());
        verify(assetRepo, times(1)).findById(999L);
        verify(assetRepo, never()).delete(any());
    }
}

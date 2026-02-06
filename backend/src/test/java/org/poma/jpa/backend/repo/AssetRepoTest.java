package org.poma.jpa.backend.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.entity.AssetType;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AssetRepoTest {

    @Autowired
    private AssetRepo assetRepo;

    private Assets testAsset;

    @BeforeEach
    void setUp() {
        assetRepo.deleteAll();
        testAsset = new Assets();
        testAsset.setSymbol("AAPL");
        testAsset.setName("Apple Inc.");
        testAsset.setType(AssetType.STOCK);
        testAsset.setUnits(100);
        testAsset.setPricePerUnit(150.50);
        testAsset.setPlatformFee(5.0);
    }

    @Test
    @DisplayName("Should save asset successfully")
    void testSaveAsset() {
        Assets savedAsset = assetRepo.save(testAsset);

        assertNotNull(savedAsset.getId());
        assertEquals("AAPL", savedAsset.getSymbol());
        assertEquals("Apple Inc.", savedAsset.getName());
        assertEquals(AssetType.STOCK, savedAsset.getType());
        assertEquals(100, savedAsset.getUnits());
        assertEquals(150.50, savedAsset.getPricePerUnit());
        assertEquals(5.0, savedAsset.getPlatformFee());
        assertEquals(15055.0, savedAsset.getFinalValue());
        assertNotNull(savedAsset.getCreatedAt());
    }

    @Test
    @DisplayName("Should find asset by id")
    void testFindById() {
        Assets savedAsset = assetRepo.save(testAsset);

        Optional<Assets> foundAsset = assetRepo.findById(savedAsset.getId());

        assertTrue(foundAsset.isPresent());
        assertEquals(savedAsset.getId(), foundAsset.get().getId());
        assertEquals("AAPL", foundAsset.get().getSymbol());
        assertEquals("Apple Inc.", foundAsset.get().getName());
    }

    @Test
    @DisplayName("Should return empty when asset not found by id")
    void testFindByIdNotFound() {
        Optional<Assets> foundAsset = assetRepo.findById(999L);

        assertFalse(foundAsset.isPresent());
    }

    @Test
    @DisplayName("Should find asset by symbol")
    void testFindBySymbol() {
        Assets savedAsset = assetRepo.save(testAsset);

        Optional<Assets> foundAsset = assetRepo.findBySymbol("AAPL");

        assertTrue(foundAsset.isPresent());
        assertEquals(savedAsset.getId(), foundAsset.get().getId());
        assertEquals("AAPL", foundAsset.get().getSymbol());
        assertEquals("Apple Inc.", foundAsset.get().getName());
    }

    @Test
    @DisplayName("Should return empty when asset not found by symbol")
    void testFindBySymbolNotFound() {
        Optional<Assets> foundAsset = assetRepo.findBySymbol("NONEXISTENT");

        assertFalse(foundAsset.isPresent());
    }

    @Test
    @DisplayName("Should find all assets")
    void testFindAll() {
        Assets asset1 = new Assets();
        asset1.setSymbol("AAPL");
        asset1.setName("Apple Inc.");
        asset1.setType(AssetType.STOCK);
        asset1.setUnits(100);
        asset1.setPricePerUnit(150.50);
        asset1.setPlatformFee(5.0);

        Assets asset2 = new Assets();
        asset2.setSymbol("GOOGL");
        asset2.setName("Alphabet Inc.");
        asset2.setType(AssetType.STOCK);
        asset2.setUnits(50);
        asset2.setPricePerUnit(2500.75);
        asset2.setPlatformFee(10.0);

        assetRepo.save(asset1);
        assetRepo.save(asset2);

        List<Assets> assets = assetRepo.findAll();

        assertEquals(2, assets.size());
        assertTrue(assets.stream().anyMatch(a -> "AAPL".equals(a.getSymbol())));
        assertTrue(assets.stream().anyMatch(a -> "GOOGL".equals(a.getSymbol())));
    }

    @Test
    @DisplayName("Should return empty list when no assets exist")
    void testFindAllEmpty() {
        List<Assets> assets = assetRepo.findAll();

        assertTrue(assets.isEmpty());
    }

    @Test
    @DisplayName("Should delete asset by id")
    void testDeleteById() {
        Assets savedAsset = assetRepo.save(testAsset);

        assetRepo.deleteById(savedAsset.getId());

        Optional<Assets> deletedAsset = assetRepo.findById(savedAsset.getId());
        assertFalse(deletedAsset.isPresent());
    }

    @Test
    @DisplayName("Should delete asset entity")
    void testDeleteAsset() {
        Assets savedAsset = assetRepo.save(testAsset);

        assetRepo.delete(savedAsset);

        Optional<Assets> deletedAsset = assetRepo.findById(savedAsset.getId());
        assertFalse(deletedAsset.isPresent());
    }

    @Test
    @DisplayName("Should check if asset exists by id")
    void testExistsById() {
        Assets savedAsset = assetRepo.save(testAsset);

        assertTrue(assetRepo.existsById(savedAsset.getId()));
        assertFalse(assetRepo.existsById(999L));
    }

    @Test
    @DisplayName("Should count all assets")
    void testCount() {
        assertEquals(0, assetRepo.count());

        assetRepo.save(testAsset);

        assertEquals(1, assetRepo.count());

        Assets anotherAsset = new Assets();
        anotherAsset.setSymbol("MSFT");
        anotherAsset.setName("Microsoft Corporation");
        anotherAsset.setType(AssetType.STOCK);
        anotherAsset.setUnits(75);
        anotherAsset.setPricePerUnit(300.25);
        anotherAsset.setPlatformFee(7.5);

        assetRepo.save(anotherAsset);

        assertEquals(2, assetRepo.count());
    }

    @Test
    @DisplayName("Should enforce unique symbol constraint")
    void testUniqueSymbolConstraint() {
        assetRepo.save(testAsset);

        Assets duplicateAsset = new Assets();
        duplicateAsset.setSymbol("AAPL");
        duplicateAsset.setName("Apple Inc. Duplicate");
        duplicateAsset.setType(AssetType.STOCK);
        duplicateAsset.setUnits(200);
        duplicateAsset.setPricePerUnit(200.00);
        duplicateAsset.setPlatformFee(10.0);

        assertThrows(Exception.class, () -> {
            assetRepo.save(duplicateAsset);
        });
    }
}

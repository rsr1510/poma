package org.poma.jpa.backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AssetsTest {

    private Assets asset;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        asset = new Assets();
        testTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
    }

    @Test
    @DisplayName("Should create asset with default constructor")
    void testDefaultConstructor() {
        assertNotNull(asset);
        assertNull(asset.getId());
        assertNull(asset.getSymbol());
        assertNull(asset.getName());
        assertNull(asset.getType());
        assertNull(asset.getUnits());
        assertNull(asset.getPricePerUnit());
        assertNull(asset.getPlatformFee());
        assertNull(asset.getFinalValue());
        assertNull(asset.getCreatedAt());
    }

    @Test
    @DisplayName("Should create asset with parameterized constructor")
    void testParameterizedConstructor() {
        Assets paramAsset = new Assets("AAPL", "Apple Inc.", AssetType.STOCK, 100, 150.50, 5.0);
        
        assertEquals("AAPL", paramAsset.getSymbol());
        assertEquals("Apple Inc.", paramAsset.getName());
        assertEquals(AssetType.STOCK, paramAsset.getType());
        assertEquals(100, paramAsset.getUnits());
        assertEquals(150.50, paramAsset.getPricePerUnit());
        assertEquals(5.0, paramAsset.getPlatformFee());
        assertEquals(15055.0, paramAsset.getFinalValue());
    }

    @Test
    @DisplayName("Should calculate final value correctly in constructor")
    void testFinalValueCalculationInConstructor() {
        Assets asset = new Assets("GOOGL", "Google", AssetType.STOCK, 50, 2500.0, 10.0);
        assertEquals(125010.0, asset.getFinalValue());
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testSettersAndGetters() {
        asset.setSymbol("MSFT");
        asset.setName("Microsoft Corporation");
        asset.setType(AssetType.STOCK);
        asset.setUnits(200);
        asset.setPricePerUnit(300.75);
        asset.setPlatformFee(7.50);

        assertEquals("MSFT", asset.getSymbol());
        assertEquals("Microsoft Corporation", asset.getName());
        assertEquals(AssetType.STOCK, asset.getType());
        assertEquals(200, asset.getUnits());
        assertEquals(300.75, asset.getPricePerUnit());
        assertEquals(7.50, asset.getPlatformFee());
        assertEquals(60157.5, asset.getFinalValue());
        assertNull(asset.getCreatedAt());
    }

    @Test
    @DisplayName("Should recalculate final value when units change")
    void testFinalValueRecalculationOnUnitsChange() {
        asset.setPricePerUnit(100.0);
        asset.setPlatformFee(5.0);
        
        asset.setUnits(10);
        assertEquals(1005.0, asset.getFinalValue());
        
        asset.setUnits(20);
        assertEquals(2005.0, asset.getFinalValue());
    }

    @Test
    @DisplayName("Should recalculate final value when price per unit changes")
    void testFinalValueRecalculationOnPriceChange() {
        asset.setUnits(10);
        asset.setPlatformFee(5.0);
        
        asset.setPricePerUnit(50.0);
        assertEquals(505.0, asset.getFinalValue());
        
        asset.setPricePerUnit(100.0);
        assertEquals(1005.0, asset.getFinalValue());
    }

    @DisplayName("Should recalculate final value when platform fee changes")
    void testFinalValueRecalculationOnFeeChange() {
        asset.setUnits(10);
        asset.setPricePerUnit(100.0);
        
        asset.setPlatformFee(5.0);
        assertEquals(1005.0, asset.getFinalValue());
        
        asset.setPlatformFee(10.0);
        assertEquals(1010.0, asset.getFinalValue());
    }

    @Test
    @DisplayName("Should handle null values in calculation")
    void testNullValueHandling() {
        asset.setUnits(null);
        asset.setPricePerUnit(null);
        asset.setPlatformFee(null);
        
        assertNull(asset.getFinalValue());
    }

    @Test
    @DisplayName("Should handle all asset types")
    void testAssetTypes() {
        AssetType[] types = {AssetType.STOCK, AssetType.BOND, AssetType.CRYPTO, AssetType.CASH};
        
        for (AssetType type : types) {
            asset.setType(type);
            assertEquals(type, asset.getType());
        }
    }

    @Test
    @DisplayName("Should implement equals correctly based on id")
    void testEquals() {
        Assets asset1 = new Assets();
        Assets asset2 = new Assets();
        Assets asset3 = new Assets();

        assertEquals(asset1, asset2, "Assets with null ids should be equal");
        assertEquals(asset1, asset3, "Assets with null ids should be equal");
        assertNotEquals(asset1, null, "Asset should not equal null");
        assertNotEquals(asset1, "string", "Asset should not equal different type");
        assertEquals(asset1, asset1, "Asset should equal itself");
    }

    @Test
    @DisplayName("Should implement hashCode correctly based on id")
    void testHashCode() {
        Assets asset1 = new Assets();
        Assets asset2 = new Assets();
        Assets asset3 = new Assets();

        assertEquals(asset1.hashCode(), asset2.hashCode(), "Assets with null ids should have same hashCode");
        assertEquals(asset1.hashCode(), asset3.hashCode(), "Assets with null ids should have same hashCode");
    }

    @Test
    @DisplayName("Should handle hashCode with null id")
    void testHashCodeWithNullId() {
        Assets assetWithNullId = new Assets();
        
        assertEquals(0, assetWithNullId.hashCode(), "Asset with null id should have hashCode 0");
    }

    @Test
    @DisplayName("Should handle zero values")
    void testZeroValues() {
        asset.setUnits(0);
        asset.setPricePerUnit(0.0);
        asset.setPlatformFee(0.0);

        assertEquals(0, asset.getUnits());
        assertEquals(0.0, asset.getPricePerUnit());
        assertEquals(0.0, asset.getPlatformFee());
        assertEquals(0.0, asset.getFinalValue());
    }

    @Test
    @DisplayName("Should handle negative values")
    void testNegativeValues() {
        asset.setUnits(-10);
        asset.setPricePerUnit(-100.0);
        asset.setPlatformFee(-5.0);

        assertEquals(-10, asset.getUnits());
        assertEquals(-100.0, asset.getPricePerUnit());
        assertEquals(-5.0, asset.getPlatformFee());
        assertEquals(995.0, asset.getFinalValue());
    }

    @Test
    @DisplayName("Should handle very large values")
    void testLargeValues() {
        asset.setUnits(1000000);
        asset.setPricePerUnit(999999.99);
        asset.setPlatformFee(9999.99);

        assertEquals(1000000, asset.getUnits());
        assertEquals(999999.99, asset.getPricePerUnit());
        assertEquals(9999.99, asset.getPlatformFee());
        assertEquals(999999999999.99, asset.getFinalValue());
    }

    @Test
    @DisplayName("Should handle decimal precision correctly")
    void testDecimalPrecision() {
        asset.setUnits(1);
        asset.setPricePerUnit(123.456789);
        asset.setPlatformFee(1.234567);

        assertEquals(124.691356, asset.getFinalValue(), 0.000001);
    }
}

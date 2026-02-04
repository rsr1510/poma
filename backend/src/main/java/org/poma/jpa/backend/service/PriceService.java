package org.poma.jpa.backend.service;

import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.repo.AssetRepo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PriceService {

    private final AssetRepo assetRepo;

    public PriceService(AssetRepo assetRepo) {
        this.assetRepo = assetRepo;
    }

    public Map<String, BigDecimal> getCurrentPrices(List<String> symbols) {
        Map<String, BigDecimal> prices = new HashMap<>();
        
        if (symbols == null || symbols.isEmpty()) {
            return prices;
        }

        for (String symbol : symbols) {
            BigDecimal price = mockPriceForSymbol(symbol);
            prices.put(symbol, price);
        }
        
        return prices;
    }

    public BigDecimal getCurrentPrice(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return mockPriceForSymbol(symbol);
    }

    public Map<Assets, BigDecimal> getCurrentPricesForAssets(List<Assets> assets) {
        Map<Assets, BigDecimal> prices = new HashMap<>();
        
        if (assets == null || assets.isEmpty()) {
            return prices;
        }

        for (Assets asset : assets) {
            BigDecimal price = mockPriceForSymbol(asset.getSymbol());
            prices.put(asset, price);
        }
        
        return prices;
    }

    private BigDecimal mockPriceForSymbol(String symbol) {
        if (symbol == null || symbol.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Generate a more dynamic price that changes over time
        int h = Math.abs(symbol.hashCode());
        double base = 100 + (h % 4000);
        
        // Add time-based variation to make prices change
        long currentTime = System.currentTimeMillis() / 1000; // seconds
        double timeVariation = Math.sin(currentTime / 30.0) * 50; // oscillates every 30 seconds
        
        // Add some randomness
        double randomVariation = (Math.random() - 0.5) * 10;
        
        double price = Math.round((base + timeVariation + randomVariation) * 100.0) / 100.0;
        
        return BigDecimal.valueOf(Math.max(price, 1.0)); // ensure price is positive
    }
}

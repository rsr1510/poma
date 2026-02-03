package org.poma.jpa.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/prices")
public class PriceController {

    @PostMapping
    public Map<String, Map<String, Double>> getPrices(@RequestBody List<String> symbols) {
        Map<String, Map<String, Double>> result = new HashMap<>();
        if (symbols == null) return result;

        for (String symbol : symbols) {
            double price = mockPriceForSymbol(symbol);
            Map<String, Double> entry = new HashMap<>();
            entry.put("price", price);
            result.put(symbol, entry);
        }
        return result;
    }

    // Deterministic mock price generator so responses are stable across calls
    private double mockPriceForSymbol(String symbol) {
        if (symbol == null || symbol.isEmpty()) return 0.0;
        int h = Math.abs(symbol.hashCode());
        double base = 100 + (h % 4000); // between ~100 and ~4100
        double cents = (h % 100) / 100.0;
        return Math.round((base + cents) * 100.0) / 100.0; // two decimals
    }
}

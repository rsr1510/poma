package org.poma.jpa.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.poma.jpa.backend.dto.HoldingsRequest;
import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.entity.Holdings;
import org.poma.jpa.backend.entity.User;
import org.poma.jpa.backend.exceptions.ResourceNotFoundException;
import org.poma.jpa.backend.repo.AssetRepo;
import org.poma.jpa.backend.repo.HoldingsRepo;
import org.poma.jpa.backend.repo.UserRepo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HoldingsService {

    private final HoldingsRepo holdingsRepo;
    private final UserRepo userRepo;
    private final AssetRepo assetRepo;

    public HoldingsService(HoldingsRepo holdingsRepo, UserRepo userRepo, AssetRepo assetRepo) {
        this.holdingsRepo = holdingsRepo;
        this.userRepo = userRepo;
        this.assetRepo = assetRepo;
    }

    public List<Holdings> findAll() {
        return holdingsRepo.findAll();
    }

    public List<Holdings> findByPortfolioId(Long portfolioId) {
        return holdingsRepo.findByPortfolioOwnerId(portfolioId);
    }

//    public Holdings findById(Long id) {
//        return holdingsRepo.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Holding not found with id: " + id));
//    }

    // New DTO-based create method
    public Holdings create(Long portfolioId, Long assetId, HoldingsRequest req) {
        if (req == null) throw new IllegalArgumentException("Holding must not be null");
        User user = userRepo.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + portfolioId));
        Assets asset = assetRepo.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + assetId));

        // basic validations
        if (req.getQuantity() == null || req.getQuantity().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Quantity must be positive");
        if (req.getAvgBuyPrice() == null || req.getAvgBuyPrice().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("avgBuyPrice must be non-negative");
        if (req.getCurrentPrice() == null || req.getCurrentPrice().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("currentPrice must be non-negative");

        Holdings h = new Holdings(user, asset, req.getQuantity(), req.getAvgBuyPrice(), req.getCurrentPrice());
        // set legacy user_id column so DB insert includes value if DB requires it
        h.setUserId(portfolioId);
        return holdingsRepo.save(h);
    }

    // Keep original for compatibility
    public Holdings create(Long portfolioId, Long assetId, Holdings incoming) {
        if (incoming == null) throw new IllegalArgumentException("Holding must not be null");
        User user = userRepo.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + portfolioId));
        Assets asset = assetRepo.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + assetId));

        // basic validations
        if (incoming.getQuantity() == null || incoming.getQuantity().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Quantity must be positive");
        if (incoming.getAvgBuyPrice() == null || incoming.getAvgBuyPrice().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("avgBuyPrice must be non-negative");
        if (incoming.getCurrentPrice() == null || incoming.getCurrentPrice().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("currentPrice must be non-negative");

        Holdings h = new Holdings(user, asset, incoming.getQuantity(), incoming.getAvgBuyPrice(), incoming.getCurrentPrice());
        h.setUserId(portfolioId);
        return holdingsRepo.save(h);
    }

//    public Map<String, BigDecimal> getSummary() {
//
//        List<Holdings> holdings = holdingsRepo.findAll();
//
//        Map<String, BigDecimal> totals = new HashMap<>();
//
//        for (Holdings h : holdings) {
//
//            String type = String.valueOf(h.getAsset().getType()); // Stock, Crypto, Bond
//
//            BigDecimal quantity = h.getQuantity();
//            BigDecimal buyPrice = h.getAvgBuyPrice();
//            BigDecimal currentPrice = h.getCurrentPrice();
//
//            // Profit/Loss = (current - buy) * qty
//            BigDecimal pl = currentPrice.subtract(buyPrice)
//                    .multiply(quantity);
//
//            totals.put(type,
//                    totals.getOrDefault(type, BigDecimal.ZERO).add(pl));
//        }
//
//        return totals;
//    }


//    // New DTO-based update method
//    public Holdings update(Long id, HoldingsRequest req) {
//        if (req == null) throw new IllegalArgumentException("Holding must not be null");
//        Holdings existing = findById(id);
//        if (req.getQuantity() != null) existing.setQuantity(req.getQuantity());
//        if (req.getAvgBuyPrice() != null) existing.setAvgBuyPrice(req.getAvgBuyPrice());
//        if (req.getCurrentPrice() != null) existing.setCurrentPrice(req.getCurrentPrice());
//        return holdingsRepo.save(existing);
//    }
//
//    // Keep original update for compatibility
//    public Holdings update(Long id, Holdings incoming) {
//        if (incoming == null) throw new IllegalArgumentException("Holding must not be null");
//        Holdings existing = findById(id);
//        if (incoming.getQuantity() != null) existing.setQuantity(incoming.getQuantity());
//        if (incoming.getAvgBuyPrice() != null) existing.setAvgBuyPrice(incoming.getAvgBuyPrice());
//        if (incoming.getCurrentPrice() != null) existing.setCurrentPrice(incoming.getCurrentPrice());
//        return holdingsRepo.save(existing);
//    }

//    public void delete(Long id) {
//        Holdings existing = findById(id);
//        holdingsRepo.delete(existing);
//    }
}

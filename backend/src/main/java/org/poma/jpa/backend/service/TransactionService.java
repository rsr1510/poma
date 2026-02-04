package org.poma.jpa.backend.service;

import jakarta.transaction.Transactional;
import org.poma.jpa.backend.dto.SellRequest;
import org.poma.jpa.backend.dto.TransactionRequest;
import org.poma.jpa.backend.entity.*;
import org.poma.jpa.backend.repo.AssetRepo;
import org.poma.jpa.backend.repo.HoldingsRepo;
import org.poma.jpa.backend.repo.TransactionRepo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepo repo;
    private final AssetRepo assetRepo;
    private final HoldingsRepo holdingsRepo;

    public TransactionService(TransactionRepo repo,
                              AssetRepo assetRepo,
                              HoldingsRepo holdingsRepo) {
        this.repo = repo;
        this.assetRepo = assetRepo;
        this.holdingsRepo = holdingsRepo;
    }

    // ===============================
    // ✅ BUY Asset
    // ===============================
    public Transactions buy(TransactionRequest req) {

        Assets asset = assetRepo.findBySymbol(req.getSymbol())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        // Save Transaction
        Transactions tx = new Transactions();
        tx.setAsset(asset);
        tx.setTransactionType(TransactionType.BUY);

        tx.setPlatform(req.getPlatform());
        tx.setQuantity(req.getQuantity());
        tx.setPricePerUnit(req.getPricePerUnit());
        tx.setFees(req.getFees());
        tx.setTotalCost(req.getTotalCost());

        repo.save(tx);

        // Update Holdings
        Holdings holding = holdingsRepo.findByAsset(asset)
                .orElse(new Holdings(asset, BigDecimal.ZERO));

        holding.setQuantity(
                holding.getQuantity().add(req.getQuantity())
        );

        holdingsRepo.save(holding);

        return tx;
    }

    // ===============================
    // ✅ SELL Asset
    // ===============================
    public Transactions sell(SellRequest req) {

        Assets asset = assetRepo.findBySymbol(req.getSymbol())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        Holdings holding = holdingsRepo.findByAsset(asset)
                .orElseThrow(() -> new RuntimeException("Holding not found"));

        if (holding.getQuantity().compareTo(req.getQuantity()) < 0) {
            throw new RuntimeException("Not enough quantity to sell");
        }

        // Placeholder market value (live yfinance later)
        BigDecimal marketValue = BigDecimal.ZERO;

        Transactions tx = new Transactions();
        tx.setAsset(asset);
        tx.setTransactionType(TransactionType.SELL);

        tx.setQuantity(req.getQuantity());
        tx.setMarketValue(marketValue);

        repo.save(tx);

        // Reduce Holdings
        BigDecimal remaining = holding.getQuantity().subtract(req.getQuantity());

        if (remaining.compareTo(BigDecimal.ZERO) == 0) {
            holdingsRepo.delete(holding);
        } else {
            holding.setQuantity(remaining);
            holdingsRepo.save(holding);
        }

        return tx;
    }
}

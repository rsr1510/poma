/*package org.poma.jpa.backend.service;

import jakarta.transaction.Transactional;
import org.poma.jpa.backend.dto.SellRequest;
import org.poma.jpa.backend.dto.TransactionRequest;
import org.poma.jpa.backend.entity.*;
import org.poma.jpa.backend.repo.AssetRepo;
import org.poma.jpa.backend.repo.HoldingsRepo;
import org.poma.jpa.backend.repo.TransactionRepo;
import org.poma.jpa.backend.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepo repo;
    private final AssetRepo assetRepo;
    private final HoldingsRepo holdingsRepo;
    private final UserRepo userRepo;

    public TransactionService(TransactionRepo repo,
                              AssetRepo assetRepo,
                              HoldingsRepo holdingsRepo,
                              UserRepo userRepo) {
        this.repo = repo;
        this.assetRepo = assetRepo;
        this.holdingsRepo = holdingsRepo;
        this.userRepo = userRepo;
    }

    // ===============================
    // ✅ BUY Asset
    // ===============================
    public Transactions buy(TransactionRequest req) {

        Assets asset = assetRepo.findBySymbol(req.getSymbol())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        User user = userRepo.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));

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

        // Update or Create Holdings
        Holdings holding = holdingsRepo.findByAsset(asset)
                .orElse(null);

        if (holding == null) {
            // Create new holding
            holding = new Holdings(user, asset, req.getQuantity(), req.getPricePerUnit(), req.getPricePerUnit());
            holding.setUserId(1L);
        } else {
            // Update existing holding
            BigDecimal newQuantity = holding.getQuantity().add(req.getQuantity());
            BigDecimal newAvgBuyPrice = holding.getAvgBuyPrice()
                    .multiply(holding.getQuantity())
                    .add(req.getPricePerUnit().multiply(req.getQuantity()))
                    .divide(newQuantity, 2, BigDecimal.ROUND_HALF_UP);
            
            holding.setQuantity(newQuantity);
            holding.setAvgBuyPrice(newAvgBuyPrice);
            holding.setCurrentPrice(req.getPricePerUnit());
        }

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

        // Calculate market value at time of selling (current price * quantity)
        BigDecimal marketValue = holding.getCurrentPrice().multiply(req.getQuantity());

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
    public List<Transactions> getAllTransactions() {
        return repo.findAll();
    }
}*/
package org.poma.jpa.backend.service;

import jakarta.transaction.Transactional;
import org.poma.jpa.backend.dto.SellRequest;
import org.poma.jpa.backend.dto.TransactionRequest;
import org.poma.jpa.backend.dto.TransactionSummaryDto;
import org.poma.jpa.backend.entity.*;
import org.poma.jpa.backend.repo.AssetRepo;
import org.poma.jpa.backend.repo.HoldingsRepo;
import org.poma.jpa.backend.repo.TransactionRepo;
import org.poma.jpa.backend.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.List;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepo repo;
    private final AssetRepo assetRepo;
    private final HoldingsRepo holdingsRepo;
    private final UserRepo userRepo;

    public TransactionService(TransactionRepo repo,
                              AssetRepo assetRepo,
                              HoldingsRepo holdingsRepo,
                              UserRepo userRepo) {
        this.repo = repo;
        this.assetRepo = assetRepo;
        this.holdingsRepo = holdingsRepo;
        this.userRepo = userRepo;
    }

    public Transactions buy(TransactionRequest req) {

        Assets asset = assetRepo.findBySymbol(req.getSymbol())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        User user = userRepo.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));

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

        // Update or Create Holdings
        Holdings holding = holdingsRepo.findByAsset(asset)
                .orElse(null);

        if (holding == null) {
            // Create new holding
            holding = new Holdings(user, asset, req.getQuantity(), req.getPricePerUnit(), req.getPricePerUnit());
            holding.setUserId(1L);
        } else {
            // Update existing holding
            BigDecimal newQuantity = holding.getQuantity().add(req.getQuantity());
            BigDecimal newAvgBuyPrice = holding.getAvgBuyPrice()
                    .multiply(holding.getQuantity())
                    .add(req.getPricePerUnit().multiply(req.getQuantity()))
                    .divide(newQuantity, 2, RoundingMode.HALF_UP);
            ;

            holding.setQuantity(newQuantity);
            holding.setAvgBuyPrice(newAvgBuyPrice);
            holding.setCurrentPrice(req.getPricePerUnit());
        }

        holdingsRepo.save(holding);

        return tx;
    }

    public Transactions sell(SellRequest req) {

        Assets asset = assetRepo.findBySymbol(req.getSymbol())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        Holdings holding = holdingsRepo.findByAsset(asset)
                .orElseThrow(() -> new RuntimeException("Holding not found"));

        if (holding.getQuantity().compareTo(req.getQuantity()) < 0) {
            throw new RuntimeException("Not enough quantity to sell");
        }

        // Calculate market value at time of selling (current price * quantity)
        BigDecimal marketValue = holding.getCurrentPrice().multiply(req.getQuantity());

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

    public List<Transactions> getAllTransactions() {

        return repo.findAll();
    }
    public TransactionSummaryDto getSummary() {

        List<Transactions> transactions = repo.findAll();

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal totalFees = BigDecimal.ZERO;
        BigDecimal realizedMarketValue = BigDecimal.ZERO;

        for (Transactions tx : transactions) {

            if (tx.getTransactionType() == TransactionType.BUY) {
                if (tx.getTotalCost() != null) {
                    totalInvested = totalInvested.add(tx.getTotalCost());
                }
                if (tx.getFees() != null) {
                    totalFees = totalFees.add(tx.getFees());
                }
            }

            if (tx.getTransactionType() == TransactionType.SELL
                    && tx.getMarketValue() != null) {
                realizedMarketValue =
                        realizedMarketValue.add(tx.getMarketValue());
            }
        }

        BigDecimal totalProfit =
                realizedMarketValue.subtract(totalInvested);

        BigDecimal estimatedTax = BigDecimal.ZERO;
        if (totalProfit.compareTo(BigDecimal.ZERO) > 0) {
            estimatedTax = totalProfit
                    .multiply(new BigDecimal("0.15"))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal netProfit =
                totalProfit.subtract(totalFees).subtract(estimatedTax);

        return new TransactionSummaryDto(
                totalInvested.setScale(2, RoundingMode.HALF_UP),
                totalProfit.setScale(2, RoundingMode.HALF_UP),
                totalFees.setScale(2, RoundingMode.HALF_UP),
                estimatedTax,
                netProfit.setScale(2, RoundingMode.HALF_UP)
        );
    }
}

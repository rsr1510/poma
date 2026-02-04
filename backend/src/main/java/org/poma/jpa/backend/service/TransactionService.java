package org.poma.jpa.backend.service;

import jakarta.transaction.Transactional;
import org.poma.jpa.backend.dto.TransactionRequest;
import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.entity.Transactions;
import org.poma.jpa.backend.repo.AssetRepo;
import org.poma.jpa.backend.repo.TransactionRepo;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepo repo;
    private final AssetRepo assetRepo;

    public TransactionService(TransactionRepo repo, AssetRepo assetRepo) {
        this.repo = repo;
        this.assetRepo = assetRepo;
    }

    public Transactions create(TransactionRequest req) {


        Transactions tx = new Transactions();

        tx.setPlatform(req.getPlatform());
        tx.setQuantity(req.getQuantity());
        tx.setPricePerUnit(req.getPricePerUnit());
        tx.setFees(req.getFees());
        tx.setTotalCost(req.getTotalCost());

        return repo.save(tx);
    }
}

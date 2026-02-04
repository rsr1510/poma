/*package org.poma.jpa.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.repo.AssetRepo;
import org.poma.jpa.backend.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
@Transactional
public class AssetService {
    private final AssetRepo repo;

    public AssetService(AssetRepo repo) {
        this.repo = repo;
    }

    public List<Assets> findAll() {
        return repo.findAll();
    }

    public Assets findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));
    }

    public Assets create(Assets asset) {
        if (asset == null) throw new IllegalArgumentException("Asset must not be null");
        // enforce unique symbol
        if (asset.getSymbol() != null && repo.findBySymbol(asset.getSymbol()).isPresent()) {
            throw new IllegalArgumentException("Asset with symbol already exists: " + asset.getSymbol());
        }
        return repo.save(asset);
    }

    public Assets update(Long id, Assets incoming) {
        if (incoming == null) throw new IllegalArgumentException("Asset must not be null");
        Assets existing = findById(id);
        existing.setSymbol(incoming.getSymbol());
        existing.setName(incoming.getName());
        existing.setType(incoming.getType());
        return repo.save(existing);
    }

    public void delete(Long id) {
        Assets existing = findById(id);
        repo.delete(existing);
    }
}
*/

package org.poma.jpa.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.repo.AssetRepo;
import org.poma.jpa.backend.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
@Transactional
public class AssetService {

    private final AssetRepo repo;

    public AssetService(AssetRepo repo) {
        this.repo = repo;
    }

    public List<Assets> findAll() {
        return repo.findAll();
    }

    public Assets findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Asset not found with id: " + id));
    }

    public Assets create(Assets asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must not be null");
        }

        // enforce unique symbol
        if (asset.getSymbol() != null &&
            repo.findBySymbol(asset.getSymbol()).isPresent()) {
            throw new IllegalArgumentException(
                "Asset with symbol already exists: " + asset.getSymbol());
        }

        // 🔹 REQUIRED FIELDS FOR FINAL VALUE CALCULATION
        if (asset.getUnits() == null) {
            throw new IllegalArgumentException("Units are required");
        }
        if (asset.getPricePerUnit() == null) {
            throw new IllegalArgumentException("Price per unit is required");
        }
        if (asset.getPlatformFee() == null) {
            throw new IllegalArgumentException("Platform fee is required");
        }

        // ❗ DO NOT calculate finalValue here
        // Entity (@PrePersist / setters) handles it

        return repo.save(asset);
    }

    public Assets update(Long id, Assets incoming) {
        if (incoming == null) {
            throw new IllegalArgumentException("Asset must not be null");
        }

        Assets existing = findById(id);

        existing.setSymbol(incoming.getSymbol());
        existing.setName(incoming.getName());
        existing.setType(incoming.getType());

        // allow updating transactional fields too
        existing.setUnits(incoming.getUnits());
        existing.setPricePerUnit(incoming.getPricePerUnit());
        existing.setPlatformFee(incoming.getPlatformFee());

        // finalValue will auto-recalculate via setters
        return repo.save(existing);
    }

    public void delete(Long id) {
        Assets existing = findById(id);
        repo.delete(existing);
    }
}

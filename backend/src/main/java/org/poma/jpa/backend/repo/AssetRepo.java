package org.poma.jpa.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.poma.jpa.backend.entity.Assets;

import java.util.Optional;

public interface AssetRepo extends JpaRepository<Assets, Long> {
    Optional<Assets> findBySymbol(String symbol);
}

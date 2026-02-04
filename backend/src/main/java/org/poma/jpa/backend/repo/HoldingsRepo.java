package org.poma.jpa.backend.repo;

import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.entity.Holdings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HoldingsRepo extends JpaRepository<Holdings, Long> {
    Optional<Holdings> findByAsset(Assets asset);
}

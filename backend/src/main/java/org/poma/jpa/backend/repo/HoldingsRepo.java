package org.poma.jpa.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.poma.jpa.backend.entity.Holdings;

import java.util.List;

public interface HoldingsRepo extends JpaRepository<Holdings, Long> {
    List<Holdings> findByPortfolioOwnerId(Long portfolioId);
}

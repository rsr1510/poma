package org.poma.jpa.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.poma.jpa.backend.entity.UserHistory;

import java.time.LocalDate;
import java.util.List;

public interface UserHistoryRepo extends JpaRepository<UserHistory, Long> {
    List<UserHistory> findByPortfolioOwnerIdOrderByDateDesc(Long portfolioId);
    List<UserHistory> findByPortfolioOwnerIdAndDateBetweenOrderByDateDesc(Long portfolioId, LocalDate start, LocalDate end);
}

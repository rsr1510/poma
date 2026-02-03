package org.poma.jpa.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.poma.jpa.backend.dto.UserHistoryRequest;
import org.poma.jpa.backend.entity.User;
import org.poma.jpa.backend.entity.UserHistory;
import org.poma.jpa.backend.exceptions.ResourceNotFoundException;
import org.poma.jpa.backend.repo.UserHistoryRepo;
import org.poma.jpa.backend.repo.UserRepo;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class UserHistoryService {

    private final UserHistoryRepo repo;
    private final UserRepo userRepo;

    public UserHistoryService(UserHistoryRepo repo, UserRepo userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    public List<UserHistory> findAll() {
        return repo.findAll();
    }

    public List<UserHistory> findByPortfolio(Long portfolioId) {
        return repo.findByPortfolioOwnerIdOrderByDateDesc(portfolioId);
    }

    public List<UserHistory> findByPortfolioBetween(Long portfolioId, LocalDate from, LocalDate to) {
        return repo.findByPortfolioOwnerIdAndDateBetweenOrderByDateDesc(portfolioId, from, to);
    }

    public UserHistory findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserHistory not found with id: " + id));
    }

    // DTO-based create
    public UserHistory create(Long portfolioId, UserHistoryRequest req) {
        if (req == null) throw new IllegalArgumentException("UserHistory must not be null");
        User user = userRepo.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + portfolioId));
        if (req.getDate() == null) throw new IllegalArgumentException("Date is required");
        if (req.getTotalValue() == null) throw new IllegalArgumentException("totalValue is required");

        UserHistory uh = new UserHistory(user, req.getDate(), req.getTotalValue());
        return repo.save(uh);
    }

    // Keep original create for compatibility
    public UserHistory create(Long portfolioId, UserHistory incoming) {
        if (incoming == null) throw new IllegalArgumentException("UserHistory must not be null");
        User user = userRepo.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + portfolioId));
        if (incoming.getDate() == null) throw new IllegalArgumentException("Date is required");
        if (incoming.getTotalValue() == null) throw new IllegalArgumentException("totalValue is required");

        UserHistory uh = new UserHistory(user, incoming.getDate(), incoming.getTotalValue());
        return repo.save(uh);
    }

    // DTO-based update
    public UserHistory update(Long id, UserHistoryRequest req) {
        if (req == null) throw new IllegalArgumentException("UserHistory must not be null");
        UserHistory existing = findById(id);
        if (req.getDate() != null) existing.setDate(req.getDate());
        if (req.getTotalValue() != null) existing.setTotalValue(req.getTotalValue());
        return repo.save(existing);
    }

    // Keep original update for compatibility
    public UserHistory update(Long id, UserHistory incoming) {
        if (incoming == null) throw new IllegalArgumentException("UserHistory must not be null");
        UserHistory existing = findById(id);
        if (incoming.getDate() != null) existing.setDate(incoming.getDate());
        if (incoming.getTotalValue() != null) existing.setTotalValue(incoming.getTotalValue());
        return repo.save(existing);
    }

    public void delete(Long id) {
        UserHistory existing = findById(id);
        repo.delete(existing);
    }
}

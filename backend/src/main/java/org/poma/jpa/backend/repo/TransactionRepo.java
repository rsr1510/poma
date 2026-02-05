package org.poma.jpa.backend.repo;

import org.poma.jpa.backend.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepo extends JpaRepository<Transactions, Long> {

}

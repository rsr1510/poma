package org.poma.jpa.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.poma.jpa.backend.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {
}

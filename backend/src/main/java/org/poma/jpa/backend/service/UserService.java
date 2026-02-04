package org.poma.jpa.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.poma.jpa.backend.entity.User;
import org.poma.jpa.backend.repo.UserRepo;
import org.poma.jpa.backend.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepo repo;

    public UserService(UserRepo repo) {
        this.repo = repo;
    }

    public List<User> findAll() {
        return repo.findAll();
    }

    public User findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User create(User user) {
        if (user == null) throw new IllegalArgumentException("User must not be null");
        return repo.save(user);
    }

    public User update(Long id, User incoming) {
        if (incoming == null) throw new IllegalArgumentException("User must not be null");
        User existing = findById(id);
        existing.setName(incoming.getName());
        existing.setTotalValue(incoming.getTotalValue());
        existing.setTotalReturnPct(incoming.getTotalReturnPct());
        return repo.save(existing);
    }

//    public void delete(Long id) {
//        User existing = findById(id);
//        repo.delete(existing);
//    }
}

package org.poma.jpa.backend.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.poma.jpa.backend.entity.User;
import org.poma.jpa.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService svc;

    public UserController(UserService svc) {
        this.svc = svc;
    }

    @GetMapping
    public ResponseEntity<List<User>> all() {
        return ResponseEntity.ok(svc.findAll());
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<User> get(@PathVariable Long id) {
//        // svc.findById throws ResourceNotFoundException if not found (handled by GlobalExceptionHandler)
//        User user = svc.findById(id);
//        return ResponseEntity.ok(user);
//    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        // validate request
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name is required");
        }
        if (user.getTotalValue() != null && user.getTotalValue().doubleValue() < 0) {
            throw new IllegalArgumentException("totalValue must be non-negative");
        }
        if (user.getTotalReturnPct() != null && user.getTotalReturnPct().doubleValue() < 0) {
            throw new IllegalArgumentException("totalReturnPct must be non-negative");
        }

        User saved = svc.create(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@Parameter(example = "1") @PathVariable Long id, @RequestBody User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name is required");
        }
        // svc.update will throw ResourceNotFoundException if id not found
        User updated = svc.update(id, user);
        return ResponseEntity.ok(updated);
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        // svc.delete will throw ResourceNotFoundException if id not found
//        svc.delete(id);
//        return ResponseEntity.noContent().build();
//    }
}

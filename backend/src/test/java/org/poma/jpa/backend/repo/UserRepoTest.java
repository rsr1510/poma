package org.poma.jpa.backend.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.poma.jpa.backend.BackendApplication;
import org.poma.jpa.backend.entity.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = BackendApplication.class)
@ActiveProfiles("test")
@Transactional
class UserRepoTest {

    @Autowired
    private UserRepo userRepo;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();
        testUser = new User("John Doe");
        testUser.setTotalValue(new BigDecimal("10000.00"));
        testUser.setTotalReturnPct(new BigDecimal("15.50"));
    }

    @Test
    @DisplayName("Should save user successfully")
    void testSaveUser() {
        User savedUser = userRepo.save(testUser);

        assertNotNull(savedUser.getId());
        assertEquals("John Doe", savedUser.getName());
        assertEquals(new BigDecimal("10000.00"), savedUser.getTotalValue());
        assertEquals(new BigDecimal("15.50"), savedUser.getTotalReturnPct());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    @DisplayName("Should find user by id")
    void testFindById() {
        User savedUser = userRepo.save(testUser);

        Optional<User> foundUser = userRepo.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals("John Doe", foundUser.get().getName());
    }

    @Test
    @DisplayName("Should return empty when user not found by id")
    void testFindByIdNotFound() {
        Optional<User> foundUser = userRepo.findById(999L);

        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should find all users")
    void testFindAll() {
        User user1 = new User("User 1");
        User user2 = new User("User 2");
        
        userRepo.save(user1);
        userRepo.save(user2);

        List<User> users = userRepo.findAll();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> "User 1".equals(u.getName())));
        assertTrue(users.stream().anyMatch(u -> "User 2".equals(u.getName())));
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testFindAllEmpty() {
        List<User> users = userRepo.findAll();

        assertTrue(users.isEmpty());
    }

    @Test
    @DisplayName("Should delete user by id")
    void testDeleteById() {
        User savedUser = userRepo.save(testUser);

        userRepo.deleteById(savedUser.getId());

        Optional<User> deletedUser = userRepo.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("Should delete user entity")
    void testDeleteUser() {
        User savedUser = userRepo.save(testUser);

        userRepo.delete(savedUser);

        Optional<User> deletedUser = userRepo.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("Should check if user exists by id")
    void testExistsById() {
        User savedUser = userRepo.save(testUser);

        assertTrue(userRepo.existsById(savedUser.getId()));
        assertFalse(userRepo.existsById(999L));
    }

    @Test
    @DisplayName("Should count all users")
    void testCount() {
        assertEquals(0, userRepo.count());

        userRepo.save(testUser);

        assertEquals(1, userRepo.count());

        User anotherUser = new User("Another User");
        userRepo.save(anotherUser);

        assertEquals(2, userRepo.count());
    }

    @Test
    @DisplayName("Should handle null values correctly")
    void testNullValues() {
        User userWithNulls = new User();
        userWithNulls.setName("Test User");
        userWithNulls.setTotalValue(null);
        userWithNulls.setTotalReturnPct(null);

        User savedUser = userRepo.save(userWithNulls);

        assertNotNull(savedUser.getId());
        assertEquals("Test User", savedUser.getName());
        assertNull(savedUser.getTotalValue());
        assertNull(savedUser.getTotalReturnPct());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle zero values correctly")
    void testZeroValues() {
        User userWithZeros = new User("Zero User");
        userWithZeros.setTotalValue(BigDecimal.ZERO);
        userWithZeros.setTotalReturnPct(BigDecimal.ZERO);

        User savedUser = userRepo.save(userWithZeros);

        assertNotNull(savedUser.getId());
        assertEquals("Zero User", savedUser.getName());
        assertEquals(BigDecimal.ZERO, savedUser.getTotalValue());
        assertEquals(BigDecimal.ZERO, savedUser.getTotalReturnPct());
    }

    @Test
    @DisplayName("Should handle negative values correctly")
    void testNegativeValues() {
        User userWithNegatives = new User("Negative User");
        userWithNegatives.setTotalValue(new BigDecimal("-1000.00"));
        userWithNegatives.setTotalReturnPct(new BigDecimal("-10.00"));

        User savedUser = userRepo.save(userWithNegatives);

        assertNotNull(savedUser.getId());
        assertEquals("Negative User", savedUser.getName());
        assertEquals(new BigDecimal("-1000.00"), savedUser.getTotalValue());
        assertEquals(new BigDecimal("-10.00"), savedUser.getTotalReturnPct());
    }

    @Test
    @DisplayName("Should handle decimal precision correctly")
    void testDecimalPrecision() {
        User preciseUser = new User("Precise User");
        preciseUser.setTotalValue(new BigDecimal("12345.67890"));
        preciseUser.setTotalReturnPct(new BigDecimal("12.345678"));

        User savedUser = userRepo.save(preciseUser);

        assertNotNull(savedUser.getId());
        assertEquals(new BigDecimal("12345.67890"), savedUser.getTotalValue());
        assertEquals(new BigDecimal("12.345678"), savedUser.getTotalReturnPct());
    }

    @Test
    @DisplayName("Should handle very large values")
    void testLargeValues() {
        User largeUser = new User("Large User");
        largeUser.setTotalValue(new BigDecimal("999999999.99"));
        largeUser.setTotalReturnPct(new BigDecimal("999.99"));

        User savedUser = userRepo.save(largeUser);

        assertNotNull(savedUser.getId());
        assertEquals(new BigDecimal("999999999.99"), savedUser.getTotalValue());
        assertEquals(new BigDecimal("999.99"), savedUser.getTotalReturnPct());
    }

    @Test
    @DisplayName("Should handle long names correctly")
    void testLongName() {
        String longName = "A".repeat(200);
        User userWithLongName = new User(longName);

        assertThrows(Exception.class, () -> userRepo.saveAndFlush(userWithLongName));
    }

    @Test
    @DisplayName("Should handle special characters in name")
    void testSpecialCharactersInName() {
        String specialName = "John Doe Jr. @#$%^&*()";
        User userWithSpecialName = new User(specialName);

        User savedUser = userRepo.save(userWithSpecialName);

        assertNotNull(savedUser.getId());
        assertEquals(specialName, savedUser.getName());
    }

    @Test
    @DisplayName("Should handle unicode characters in name")
    void testUnicodeCharactersInName() {
        String unicodeName = "Jöhn Döe 测试";
        User userWithUnicodeName = new User(unicodeName);

        User savedUser = userRepo.save(userWithUnicodeName);

        assertNotNull(savedUser.getId());
        assertEquals(unicodeName, savedUser.getName());
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser() {
        User savedUser = userRepo.saveAndFlush(testUser);

        savedUser.setName("Updated Name");
        savedUser.setTotalValue(new BigDecimal("20000.00"));
        savedUser.setTotalReturnPct(new BigDecimal("25.00"));

        User updatedUser = userRepo.save(savedUser);

        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals(new BigDecimal("20000.00"), updatedUser.getTotalValue());
        assertEquals(new BigDecimal("25.00"), updatedUser.getTotalReturnPct());
        assertEquals(savedUser.getCreatedAt(), updatedUser.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle multiple users with same name")
    void testMultipleUsersSameName() {
        User user1 = new User("Same Name");
        User user2 = new User("Same Name");
        
        userRepo.save(user1);
        userRepo.save(user2);

        List<User> users = userRepo.findAll();

        assertEquals(2, users.size());
        assertEquals(2, users.stream().filter(u -> "Same Name".equals(u.getName())).count());
    }

    @Test
    @DisplayName("Should maintain entity relationships correctly")
    void testEntityRelationships() {
        User savedUser = userRepo.save(testUser);

        User foundUser = userRepo.findById(savedUser.getId()).orElse(null);

        assertNotNull(foundUser);
        assertEquals(savedUser.getId(), foundUser.getId());
        assertEquals(savedUser.getName(), foundUser.getName());
        assertNotNull(foundUser.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle cascade operations correctly")
    void testCascadeOperations() {
        User savedUser = userRepo.save(testUser);

        User managedUser = userRepo.findById(savedUser.getId()).orElse(null);
        assertNotNull(managedUser);

        managedUser.setName("Cascade Test");
        userRepo.save(managedUser);

        User finalUser = userRepo.findById(savedUser.getId()).orElse(null);
        assertNotNull(finalUser);
        assertEquals("Cascade Test", finalUser.getName());
    }
}

package org.poma.jpa.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.poma.jpa.backend.entity.User;
import org.poma.jpa.backend.exceptions.ResourceNotFoundException;
import org.poma.jpa.backend.repo.UserRepo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User updatedUser;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe");
        testUser.setTotalValue(new BigDecimal("10000.00"));
        testUser.setTotalReturnPct(new BigDecimal("15.50"));

        updatedUser = new User("Jane Smith");
        updatedUser.setTotalValue(new BigDecimal("15000.00"));
        updatedUser.setTotalReturnPct(new BigDecimal("20.25"));
    }

    @Test
    @DisplayName("Should return all users when findAll is called")
    void testFindAll() {
        List<User> expectedUsers = Arrays.asList(testUser, updatedUser);
        when(userRepo.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.findAll();

        assertEquals(expectedUsers, actualUsers);
        verify(userRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return user when findById is called with valid id")
    void testFindByIdWithValidId() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));

        User actualUser = userService.findById(1L);

        assertEquals(testUser, actualUser);
        verify(userRepo, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when findById is called with invalid id")
    void testFindByIdWithInvalidId() {
        when(userRepo.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> userService.findById(999L)
        );

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepo, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create user when valid user is provided")
    void testCreateWithValidUser() {
        User newUser = new User("New User");
        User savedUser = new User("New User");
        
        when(userRepo.save(any(User.class))).thenReturn(savedUser);

        User actualUser = userService.create(newUser);

        assertEquals(savedUser, actualUser);
        verify(userRepo, times(1)).save(newUser);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when create is called with null user")
    void testCreateWithNullUser() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.create(null)
        );

        assertEquals("User must not be null", exception.getMessage());
        verify(userRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should update user when valid id and user are provided")
    void testUpdateWithValidIdAndUser() {
        User existingUser = new User("John Doe");
                existingUser.setTotalValue(new BigDecimal("10000.00"));
        existingUser.setTotalReturnPct(new BigDecimal("15.50"));

        User updatedUserData = new User("Jane Smith");
        updatedUserData.setTotalValue(new BigDecimal("15000.00"));
        updatedUserData.setTotalReturnPct(new BigDecimal("20.25"));

        User savedUser = new User("Jane Smith");
        savedUser.setTotalValue(new BigDecimal("15000.00"));
        savedUser.setTotalReturnPct(new BigDecimal("20.25"));

        when(userRepo.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepo.save(any(User.class))).thenReturn(savedUser);

        User actualUser = userService.update(1L, updatedUserData);

        assertEquals("Jane Smith", actualUser.getName());
        assertEquals(new BigDecimal("15000.00"), actualUser.getTotalValue());
        assertEquals(new BigDecimal("20.25"), actualUser.getTotalReturnPct());
        verify(userRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when update is called with invalid id")
    void testUpdateWithInvalidId() {
        when(userRepo.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> userService.update(999L, updatedUser)
        );

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepo, times(1)).findById(999L);
        verify(userRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when update is called with null user")
    void testUpdateWithNullUser() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.update(1L, null)
        );

        assertEquals("User must not be null", exception.getMessage());
        verify(userRepo, never()).findById(any());
        verify(userRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should update only mutable fields when updating user")
    void testUpdateOnlyMutableFields() {
        User existingUser = new User("John Doe");
                existingUser.setTotalValue(new BigDecimal("10000.00"));
        existingUser.setTotalReturnPct(new BigDecimal("15.50"));

        User updateData = new User("New Name");
        updateData.setTotalValue(new BigDecimal("20000.00"));
        updateData.setTotalReturnPct(new BigDecimal("25.00"));

        when(userRepo.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepo.save(any(User.class))).thenReturn(existingUser);

        userService.update(1L, updateData);

        assertEquals("New Name", existingUser.getName());
        assertEquals(new BigDecimal("20000.00"), existingUser.getTotalValue());
        assertEquals(new BigDecimal("25.00"), existingUser.getTotalReturnPct());
        verify(userRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("Should handle partial updates correctly")
    void testPartialUpdate() {
        User existingUser = new User("John Doe");
                existingUser.setTotalValue(new BigDecimal("10000.00"));
        existingUser.setTotalReturnPct(new BigDecimal("15.50"));

        User partialUpdate = new User();
        partialUpdate.setName("Updated Name Only");

        when(userRepo.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepo.save(any(User.class))).thenReturn(existingUser);

        userService.update(1L, partialUpdate);

        assertEquals("Updated Name Only", existingUser.getName());
        assertEquals(BigDecimal.ZERO, existingUser.getTotalValue());
        assertEquals(BigDecimal.ZERO, existingUser.getTotalReturnPct());
        verify(userRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("Should handle null values in update correctly")
    void testUpdateWithNullValues() {
        User existingUser = new User("John Doe");
                existingUser.setTotalValue(new BigDecimal("10000.00"));
        existingUser.setTotalReturnPct(new BigDecimal("15.50"));

        User updateWithNulls = new User();
        updateWithNulls.setName(null);
        updateWithNulls.setTotalValue(null);
        updateWithNulls.setTotalReturnPct(null);

        when(userRepo.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepo.save(any(User.class))).thenReturn(existingUser);

        userService.update(1L, updateWithNulls);

        assertNull(existingUser.getName());
        assertNull(existingUser.getTotalValue());
        assertNull(existingUser.getTotalReturnPct());
        verify(userRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("Should handle BigDecimal precision correctly in updates")
    void testBigDecimalPrecisionInUpdate() {
        User existingUser = new User("John Doe");
                existingUser.setTotalValue(new BigDecimal("10000.00"));
        existingUser.setTotalReturnPct(new BigDecimal("15.50"));

        User preciseUpdate = new User("Precise User");
        preciseUpdate.setTotalValue(new BigDecimal("12345.67890"));
        preciseUpdate.setTotalReturnPct(new BigDecimal("12.3456"));

        when(userRepo.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepo.save(any(User.class))).thenReturn(existingUser);

        userService.update(1L, preciseUpdate);

        assertEquals(new BigDecimal("12345.67890"), existingUser.getTotalValue());
        assertEquals(new BigDecimal("12.3456"), existingUser.getTotalReturnPct());
        verify(userRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).save(existingUser);
    }
}

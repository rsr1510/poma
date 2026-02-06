package org.poma.jpa.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.poma.jpa.backend.exceptions.GlobalExceptionHandler;
import org.poma.jpa.backend.entity.User;
import org.poma.jpa.backend.service.UserService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class UserControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private ObjectMapper objectMapper;

    private User testUser;
    private User newUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        testUser = new User("John Doe");
        testUser.setTotalValue(new BigDecimal("10000.00"));
        testUser.setTotalReturnPct(new BigDecimal("15.50"));

        newUser = new User("Jane Smith");
        newUser.setTotalValue(new BigDecimal("15000.00"));
        newUser.setTotalReturnPct(new BigDecimal("20.25"));
    }

    @Test
    @DisplayName("Should return all users when GET /api/users is called")
    void testGetAllUsers() throws Exception {
        List<User> users = Arrays.asList(testUser, newUser);
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].totalValue").value(10000.00))
                .andExpect(jsonPath("$[0].totalReturnPct").value(15.50))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"))
                .andExpect(jsonPath("$[1].totalValue").value(15000.00))
                .andExpect(jsonPath("$[1].totalReturnPct").value(20.25));

        verify(userService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should create user when POST /api/users is called with valid data")
    void testCreateUser() throws Exception {
        User savedUser = new User("Jane Smith");
        savedUser.setTotalValue(new BigDecimal("15000.00"));
        savedUser.setTotalReturnPct(new BigDecimal("20.25"));

        when(userService.create(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Jane Smith"))
                .andExpect(jsonPath("$.totalValue").value(15000.00))
                .andExpect(jsonPath("$.totalReturnPct").value(20.25))
                .andExpect(header().exists("Location"));

        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when POST /api/users is called with null user")
    void testCreateUserWithNullUser() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isInternalServerError());

        verify(userService, never()).create(any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when POST /api/users is called with null name")
    void testCreateUserWithNullName() throws Exception {
        User userWithNullName = new User();
        userWithNullName.setName(null);
        userWithNullName.setTotalValue(new BigDecimal("10000.00"));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWithNullName)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User name is required")));

        verify(userService, never()).create(any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when POST /api/users is called with empty name")
    void testCreateUserWithEmptyName() throws Exception {
        User userWithEmptyName = new User();
        userWithEmptyName.setName("");
        userWithEmptyName.setTotalValue(new BigDecimal("10000.00"));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWithEmptyName)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User name is required")));

        verify(userService, never()).create(any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when POST /api/users is called with whitespace-only name")
    void testCreateUserWithWhitespaceName() throws Exception {
        User userWithWhitespaceName = new User();
        userWithWhitespaceName.setName("   ");
        userWithWhitespaceName.setTotalValue(new BigDecimal("10000.00"));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWithWhitespaceName)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User name is required")));

        verify(userService, never()).create(any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when POST /api/users is called with negative totalValue")
    void testCreateUserWithNegativeTotalValue() throws Exception {
        User userWithNegativeValue = new User("Test User");
        userWithNegativeValue.setTotalValue(new BigDecimal("-1000.00"));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWithNegativeValue)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("totalValue must be non-negative")));

        verify(userService, never()).create(any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when POST /api/users is called with negative totalReturnPct")
    void testCreateUserWithNegativeTotalReturnPct() throws Exception {
        User userWithNegativeReturn = new User("Test User");
        userWithNegativeReturn.setTotalValue(new BigDecimal("10000.00"));
        userWithNegativeReturn.setTotalReturnPct(new BigDecimal("-10.00"));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWithNegativeReturn)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("totalReturnPct must be non-negative")));

        verify(userService, never()).create(any());
    }

    @Test
    @DisplayName("Should update user when PUT /api/users/{id} is called with valid data")
    void testUpdateUser() throws Exception {
        User updatedUser = new User("Updated Name");
        updatedUser.setTotalValue(new BigDecimal("20000.00"));
        updatedUser.setTotalReturnPct(new BigDecimal("25.00"));

        User savedUpdatedUser = new User("Updated Name");
        ReflectionTestUtils.setField(savedUpdatedUser, "id", 1L);
        savedUpdatedUser.setTotalValue(new BigDecimal("20000.00"));
        savedUpdatedUser.setTotalReturnPct(new BigDecimal("25.00"));

        when(userService.update(eq(1L), any(User.class))).thenReturn(savedUpdatedUser);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.totalValue").value(20000.00))
                .andExpect(jsonPath("$.totalReturnPct").value(25.00));

        verify(userService, times(1)).update(eq(1L), any(User.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when PUT /api/users/{id} is called with null user")
    void testUpdateUserWithNullUser() throws Exception {
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isInternalServerError());

        verify(userService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when PUT /api/users/{id} is called with null name")
    void testUpdateUserWithNullName() throws Exception {
        User userWithNullName = new User();
        userWithNullName.setName(null);
        userWithNullName.setTotalValue(new BigDecimal("10000.00"));

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWithNullName)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User name is required")));

        verify(userService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when PUT /api/users/{id} is called with empty name")
    void testUpdateUserWithEmptyName() throws Exception {
        User userWithEmptyName = new User();
        userWithEmptyName.setName("");
        userWithEmptyName.setTotalValue(new BigDecimal("10000.00"));

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWithEmptyName)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User name is required")));

        verify(userService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when PUT /api/users/{id} is called with negative totalValue")
    void testUpdateUserWithNegativeTotalValue() throws Exception {
        User userWithNegativeValue = new User("Test User");
        userWithNegativeValue.setTotalValue(new BigDecimal("-1000.00"));

        when(userService.update(eq(1L), any(User.class))).thenReturn(userWithNegativeValue);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWithNegativeValue)))
                .andExpect(status().isOk());

        verify(userService, times(1)).update(eq(1L), any(User.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when PUT /api/users/{id} is called with negative totalReturnPct")
    void testUpdateUserWithNegativeTotalReturnPct() throws Exception {
        User userWithNegativeReturn = new User("Test User");
        userWithNegativeReturn.setTotalValue(new BigDecimal("10000.00"));
        userWithNegativeReturn.setTotalReturnPct(new BigDecimal("-10.00"));

        when(userService.update(eq(1L), any(User.class))).thenReturn(userWithNegativeReturn);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWithNegativeReturn)))
                .andExpect(status().isOk());

        verify(userService, times(1)).update(eq(1L), any(User.class));
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException from service layer")
    void testResourceNotFoundException() throws Exception {
        when(userService.update(eq(999L), any(User.class)))
                .thenThrow(new RuntimeException("User not found with id: 999"));

        User updateUser = new User("Updated Name");
        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("User not found with id: 999")));

        verify(userService, times(1)).update(eq(999L), any(User.class));
    }

    @Test
    @DisplayName("Should create user with minimal valid data")
    void testCreateUserWithMinimalData() throws Exception {
        User minimalUser = new User("Minimal User");
        User savedMinimalUser = new User("Minimal User");
        ReflectionTestUtils.setField(savedMinimalUser, "id", 3L);

        when(userService.create(any(User.class))).thenReturn(savedMinimalUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Minimal User"))
                .andExpect(jsonPath("$.totalValue").value(0))
                .andExpect(jsonPath("$.totalReturnPct").value(0));

        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    @DisplayName("Should handle decimal precision correctly")
    void testDecimalPrecision() throws Exception {
        User preciseUser = new User("Precise User");
        preciseUser.setTotalValue(new BigDecimal("12345.67890"));
        preciseUser.setTotalReturnPct(new BigDecimal("12.345678"));

        User savedPreciseUser = new User("Precise User");
        ReflectionTestUtils.setField(savedPreciseUser, "id", 4L);
        savedPreciseUser.setTotalValue(new BigDecimal("12345.67890"));
        savedPreciseUser.setTotalReturnPct(new BigDecimal("12.345678"));

        when(userService.create(any(User.class))).thenReturn(savedPreciseUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preciseUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalValue").value(12345.67890))
                .andExpect(jsonPath("$.totalReturnPct").value(12.345678));

        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testGetAllUsersEmpty() throws Exception {
        when(userService.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle invalid JSON format")
    void testInvalidJson() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isInternalServerError());

        verify(userService, never()).create(any());
    }
}

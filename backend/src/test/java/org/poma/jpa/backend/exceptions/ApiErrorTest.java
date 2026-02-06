package org.poma.jpa.backend.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorTest {

    private ApiError apiError;

    @BeforeEach
    void setUp() {
        apiError = new ApiError();
    }

    @Test
    @DisplayName("Should create ApiError with default constructor")
    void testDefaultConstructor() {
        assertNotNull(apiError);
        assertNotNull(apiError.getTimestamp());
        assertEquals(0, apiError.getStatus());
        assertNull(apiError.getError());
        assertNull(apiError.getMessage());
        assertNull(apiError.getPath());
    }

    @Test
    @DisplayName("Should create ApiError with parameterized constructor")
    void testParameterizedConstructor() {
        int status = 404;
        String error = "Not Found";
        String message = "User not found with id: 999";
        String path = "/api/users/999";

        ApiError paramApiError = new ApiError(status, error, message, path);

        assertEquals(status, paramApiError.getStatus());
        assertEquals(error, paramApiError.getError());
        assertEquals(message, paramApiError.getMessage());
        assertEquals(path, paramApiError.getPath());
        assertNotNull(paramApiError.getTimestamp());
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testSettersAndGetters() {
        apiError.setStatus(400);
        apiError.setError("Bad Request");
        apiError.setMessage("Invalid input data");
        apiError.setPath("/api/users");

        assertEquals(400, apiError.getStatus());
        assertEquals("Bad Request", apiError.getError());
        assertEquals("Invalid input data", apiError.getMessage());
        assertEquals("/api/users", apiError.getPath());
        assertNotNull(apiError.getTimestamp());
    }

    @Test
    @DisplayName("Should handle null values in setters")
    void testNullSetters() {
        apiError.setStatus(500);
        apiError.setError("Internal Server Error");
        apiError.setMessage("Database error");
        apiError.setPath("/api/test");

        apiError.setError(null);
        apiError.setMessage(null);
        apiError.setPath(null);

        assertEquals(500, apiError.getStatus());
        assertNull(apiError.getError());
        assertNull(apiError.getMessage());
        assertNull(apiError.getPath());
        assertNotNull(apiError.getTimestamp());
    }

    @Test
    @DisplayName("Should handle empty strings in setters")
    void testEmptyStringSetters() {
        apiError.setError("");
        apiError.setMessage("");
        apiError.setPath("");

        assertEquals("", apiError.getError());
        assertEquals("", apiError.getMessage());
        assertEquals("", apiError.getPath());
    }

    @Test
    @DisplayName("Should handle whitespace-only strings in setters")
    void testWhitespaceStringSetters() {
        apiError.setError("   ");
        apiError.setMessage("   ");
        apiError.setPath("   ");

        assertEquals("   ", apiError.getError());
        assertEquals("   ", apiError.getMessage());
        assertEquals("   ", apiError.getPath());
    }

    @Test
    @DisplayName("Should handle different HTTP status codes")
    void testDifferentStatusCodes() {
        int[] statusCodes = {200, 201, 400, 401, 403, 404, 500, 502, 503};

        for (int status : statusCodes) {
            apiError.setStatus(status);
            assertEquals(status, apiError.getStatus());
        }
    }

    @Test
    @DisplayName("Should handle different error types")
    void testDifferentErrorTypes() {
        String[] errorTypes = {
            "Bad Request",
            "Unauthorized",
            "Forbidden",
            "Not Found",
            "Internal Server Error",
            "Service Unavailable"
        };

        for (String errorType : errorTypes) {
            apiError.setError(errorType);
            assertEquals(errorType, apiError.getError());
        }
    }

    @Test
    @DisplayName("Should handle long error messages")
    void testLongErrorMessages() {
        String longMessage = "A".repeat(1000);
        apiError.setMessage(longMessage);
        assertEquals(longMessage, apiError.getMessage());
    }

    @Test
    @DisplayName("Should handle special characters in message")
    void testSpecialCharactersInMessage() {
        String specialMessage = "Error: Invalid symbol @#$%^&*()";
        apiError.setMessage(specialMessage);
        assertEquals(specialMessage, apiError.getMessage());
    }

    @Test
    @DisplayName("Should handle unicode characters in message")
    void testUnicodeCharactersInMessage() {
        String unicodeMessage = "错误：用户不存在";
        apiError.setMessage(unicodeMessage);
        assertEquals(unicodeMessage, apiError.getMessage());
    }

    @Test
    @DisplayName("Should handle different API paths")
    void testDifferentApiPaths() {
        String[] paths = {
            "/api/users",
            "/api/users/1",
            "/api/assets",
            "/api/assets/AAPL",
            "/api/transactions",
            "/api/transactions/123",
            "/api/alerts",
            "/api/alerts/456",
            "/api/holdings",
            "/api/notifications"
        };

        for (String path : paths) {
            apiError.setPath(path);
            assertEquals(path, apiError.getPath());
        }
    }

    @Test
    @DisplayName("Should handle paths with query parameters")
    void testPathsWithQueryParameters() {
        String[] pathsWithQueries = {
            "/api/users?page=1&size=10",
            "/api/assets?sort=name&order=asc",
            "/api/transactions?date=2024-01-01",
            "/api/alerts?active=true",
            "/api/search?q=test&filter=all"
        };

        for (String path : pathsWithQueries) {
            apiError.setPath(path);
            assertEquals(path, apiError.getPath());
        }
    }

    @Test
    @DisplayName("Should handle timestamp correctly")
    void testTimestamp() {
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);
        ApiError newApiError = new ApiError();
        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);

        LocalDateTime timestamp = newApiError.getTimestamp();
        assertTrue(timestamp.isAfter(beforeCreation) || timestamp.equals(beforeCreation));
        assertTrue(timestamp.isBefore(afterCreation) || timestamp.equals(afterCreation));
    }

    @Test
    @DisplayName("Should allow updating timestamp")
    void testUpdateTimestamp() {
        // Note: The current implementation doesn't have a setter for timestamp,
        // but we test that the timestamp is set during creation
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);
        
        ApiError newApiError = new ApiError();
        LocalDateTime timestamp = newApiError.getTimestamp();
        
        assertNotNull(timestamp);
        assertTrue(timestamp.isAfter(beforeCreation) || timestamp.equals(beforeCreation));
    }

    @Test
    @DisplayName("Should handle partial field updates")
    void testPartialFieldUpdates() {
        // Update only status
        apiError.setStatus(404);
        assertEquals(404, apiError.getStatus());
        assertNull(apiError.getError());
        assertNull(apiError.getMessage());
        assertNull(apiError.getPath());

        // Update only error
        apiError.setError("Not Found");
        assertEquals(404, apiError.getStatus());
        assertEquals("Not Found", apiError.getError());
        assertNull(apiError.getMessage());
        assertNull(apiError.getPath());

        // Update only message
        apiError.setMessage("Resource not found");
        assertEquals(404, apiError.getStatus());
        assertEquals("Not Found", apiError.getError());
        assertEquals("Resource not found", apiError.getMessage());
        assertNull(apiError.getPath());

        // Update only path
        apiError.setPath("/api/users/999");
        assertEquals(404, apiError.getStatus());
        assertEquals("Not Found", apiError.getError());
        assertEquals("Resource not found", apiError.getMessage());
        assertEquals("/api/users/999", apiError.getPath());
    }

    @Test
    @DisplayName("Should handle field updates independently")
    void testIndependentFieldUpdates() {
        apiError.setStatus(400);
        apiError.setError("Bad Request");
        apiError.setMessage("Invalid input");
        apiError.setPath("/api/users");

        // Update status independently
        apiError.setStatus(500);
        assertEquals(500, apiError.getStatus());
        assertEquals("Bad Request", apiError.getError());
        assertEquals("Invalid input", apiError.getMessage());
        assertEquals("/api/users", apiError.getPath());

        // Update error independently
        apiError.setError("Internal Server Error");
        assertEquals(500, apiError.getStatus());
        assertEquals("Internal Server Error", apiError.getError());
        assertEquals("Invalid input", apiError.getMessage());
        assertEquals("/api/users", apiError.getPath());

        // Update message independently
        apiError.setMessage("Database error");
        assertEquals(500, apiError.getStatus());
        assertEquals("Internal Server Error", apiError.getError());
        assertEquals("Database error", apiError.getMessage());
        assertEquals("/api/users", apiError.getPath());

        // Update path independently
        apiError.setPath("/api/database");
        assertEquals(500, apiError.getStatus());
        assertEquals("Internal Server Error", apiError.getError());
        assertEquals("Database error", apiError.getMessage());
        assertEquals("/api/database", apiError.getPath());
    }

    @Test
    @DisplayName("Should handle negative status codes")
    void testNegativeStatusCodes() {
        apiError.setStatus(-1);
        assertEquals(-1, apiError.getStatus());
    }

    @Test
    @DisplayName("Should handle very large status codes")
    void testLargeStatusCodes() {
        apiError.setStatus(999999);
        assertEquals(999999, apiError.getStatus());
    }

    @Test
    @DisplayName("Should handle zero status code")
    void testZeroStatusCode() {
        apiError.setStatus(0);
        assertEquals(0, apiError.getStatus());
    }

    @Test
    @DisplayName("Should maintain immutability of constructor parameters")
    void testConstructorImmutability() {
        int status = 404;
        String error = "Not Found";
        String message = "Resource not found";
        String path = "/api/test";

        ApiError paramApiError = new ApiError(status, error, message, path);

        // Modify original values
        status = 500;
        error = "Internal Server Error";
        message = "Database error";
        path = "/api/database";

        // ApiError should not be affected
        assertEquals(404, paramApiError.getStatus());
        assertEquals("Not Found", paramApiError.getError());
        assertEquals("Resource not found", paramApiError.getMessage());
        assertEquals("/api/test", paramApiError.getPath());
    }

    @Test
    @DisplayName("Should handle multiple instances independently")
    void testMultipleInstances() {
        ApiError error1 = new ApiError(404, "Not Found", "User not found", "/api/users/999");
        ApiError error2 = new ApiError(400, "Bad Request", "Invalid input", "/api/users");

        assertEquals(404, error1.getStatus());
        assertEquals("Not Found", error1.getError());
        assertEquals("User not found", error1.getMessage());
        assertEquals("/api/users/999", error1.getPath());

        assertEquals(400, error2.getStatus());
        assertEquals("Bad Request", error2.getError());
        assertEquals("Invalid input", error2.getMessage());
        assertEquals("/api/users", error2.getPath());

        // Update one should not affect the other
        error1.setStatus(500);
        assertEquals(500, error1.getStatus());
        assertEquals(400, error2.getStatus());
    }
}

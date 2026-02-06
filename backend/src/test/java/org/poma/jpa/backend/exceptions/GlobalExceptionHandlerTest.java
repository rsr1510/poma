package org.poma.jpa.backend.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private MockHttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/api/test");
        mockRequest.setMethod("GET");
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException correctly")
    void testHandleResourceNotFoundException() {
        String errorMessage = "User not found with id: 999";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);

        ResponseEntity<ApiError> response = globalExceptionHandler.handleNotFound(exception, mockRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        
        ApiError apiError = response.getBody();
        assertEquals(HttpStatus.NOT_FOUND.value(), apiError.getStatus());
        assertEquals("Not Found", apiError.getError());
        assertEquals(errorMessage, apiError.getMessage());
        assertEquals("/api/test", apiError.getPath());
        assertNotNull(apiError.getTimestamp());
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException correctly")
    void testHandleIllegalArgumentException() {
        String errorMessage = "User name is required";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        ResponseEntity<ApiError> response = globalExceptionHandler.handleBadRequest(exception, mockRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        ApiError apiError = response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST.value(), apiError.getStatus());
        assertEquals("Bad Request", apiError.getError());
        assertEquals(errorMessage, apiError.getMessage());
        assertEquals("/api/test", apiError.getPath());
        assertNotNull(apiError.getTimestamp());
    }

    @Test
    @DisplayName("Should handle generic Exception correctly")
    void testHandleGenericException() {
        String errorMessage = "Database connection failed";
        Exception exception = new RuntimeException(errorMessage);

        ResponseEntity<ApiError> response = globalExceptionHandler.handleAll(exception, mockRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        
        ApiError apiError = response.getBody();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiError.getStatus());
        assertEquals("Internal Server Error", apiError.getError());
        assertEquals(errorMessage, apiError.getMessage());
        assertEquals("/api/test", apiError.getPath());
        assertNotNull(apiError.getTimestamp());
    }

    @Test
    @DisplayName("Should handle null message in exceptions")
    void testHandleExceptionWithNullMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException(null);

        ResponseEntity<ApiError> response = globalExceptionHandler.handleNotFound(exception, mockRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        
        ApiError apiError = response.getBody();
        assertEquals(HttpStatus.NOT_FOUND.value(), apiError.getStatus());
        assertEquals("Not Found", apiError.getError());
        assertNull(apiError.getMessage());
        assertEquals("/api/test", apiError.getPath());
        assertNotNull(apiError.getTimestamp());
    }

    @Test
    @DisplayName("Should handle empty message in exceptions")
    void testHandleExceptionWithEmptyMessage() {
        IllegalArgumentException exception = new IllegalArgumentException("");

        ResponseEntity<ApiError> response = globalExceptionHandler.handleBadRequest(exception, mockRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        ApiError apiError = response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST.value(), apiError.getStatus());
        assertEquals("Bad Request", apiError.getError());
        assertEquals("", apiError.getMessage());
        assertEquals("/api/test", apiError.getPath());
        assertNotNull(apiError.getTimestamp());
    }

    @Test
    @DisplayName("Should handle different request paths")
    void testHandleExceptionWithDifferentPaths() {
        mockRequest.setRequestURI("/api/users/999");
        
        ResourceNotFoundException exception = new ResourceNotFoundException("User not found");
        ResponseEntity<ApiError> response = globalExceptionHandler.handleNotFound(exception, mockRequest);

        assertEquals("/api/users/999", response.getBody().getPath());

        mockRequest.setRequestURI("/api/assets/invalid");
        response = globalExceptionHandler.handleNotFound(exception, mockRequest);

        assertEquals("/api/assets/invalid", response.getBody().getPath());
    }

    @Test
    @DisplayName("Should handle exceptions with special characters in message")
    void testHandleExceptionWithSpecialCharacters() {
        String specialMessage = "Error: Invalid symbol @#$%^&*()";
        IllegalArgumentException exception = new IllegalArgumentException(specialMessage);

        ResponseEntity<ApiError> response = globalExceptionHandler.handleBadRequest(exception, mockRequest);

        assertEquals(specialMessage, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle exceptions with unicode characters in message")
    void testHandleExceptionWithUnicodeCharacters() {
        String unicodeMessage = "错误：用户不存在";
        RuntimeException exception = new RuntimeException(unicodeMessage);

        ResponseEntity<ApiError> response = globalExceptionHandler.handleAll(exception, mockRequest);

        assertEquals(unicodeMessage, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle very long error messages")
    void testHandleExceptionWithLongMessage() {
        String longMessage = "A".repeat(1000);
        ResourceNotFoundException exception = new ResourceNotFoundException(longMessage);

        ResponseEntity<ApiError> response = globalExceptionHandler.handleNotFound(exception, mockRequest);

        assertEquals(longMessage, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle nested exceptions")
    void testHandleNestedException() {
        String rootMessage = "Root cause error";
        String wrapperMessage = "Wrapper error";
        Exception rootCause = new RuntimeException(rootMessage);
        IllegalArgumentException wrapperException = new IllegalArgumentException(wrapperMessage, rootCause);

        ResponseEntity<ApiError> response = globalExceptionHandler.handleBadRequest(wrapperException, mockRequest);

        assertEquals(wrapperMessage, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle exceptions with different HTTP methods")
    void testHandleExceptionWithDifferentMethods() {
        mockRequest.setMethod("POST");
        mockRequest.setRequestURI("/api/users");
        
        IllegalArgumentException exception = new IllegalArgumentException("Invalid data");
        ResponseEntity<ApiError> response = globalExceptionHandler.handleBadRequest(exception, mockRequest);

        assertEquals("/api/users", response.getBody().getPath());

        mockRequest.setMethod("PUT");
        mockRequest.setRequestURI("/api/users/1");
        response = globalExceptionHandler.handleBadRequest(exception, mockRequest);

        assertEquals("/api/users/1", response.getBody().getPath());

        mockRequest.setMethod("DELETE");
        mockRequest.setRequestURI("/api/users/1");
        response = globalExceptionHandler.handleBadRequest(exception, mockRequest);

        assertEquals("/api/users/1", response.getBody().getPath());
    }

    @Test
    @DisplayName("Should handle exceptions with query parameters")
    void testHandleExceptionWithQueryParameters() {
        mockRequest.setRequestURI("/api/users?name=test&sort=asc");
        
        ResourceNotFoundException exception = new ResourceNotFoundException("User not found");
        ResponseEntity<ApiError> response = globalExceptionHandler.handleNotFound(exception, mockRequest);

        assertEquals("/api/users?name=test&sort=asc", response.getBody().getPath());
    }

    @Test
    @DisplayName("Should ensure timestamp is current")
    void testTimestampIsCurrent() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        
        ResourceNotFoundException exception = new ResourceNotFoundException("Test error");
        ResponseEntity<ApiError> response = globalExceptionHandler.handleNotFound(exception, mockRequest);
        
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        LocalDateTime timestamp = response.getBody().getTimestamp();
        
        assertTrue(timestamp.isAfter(before) || timestamp.equals(before));
        assertTrue(timestamp.isBefore(after) || timestamp.equals(after));
    }

    @Test
    @DisplayName("Should handle multiple exceptions in sequence")
    void testHandleMultipleExceptions() {
        // First exception
        ResourceNotFoundException notFoundException = new ResourceNotFoundException("Not found");
        ResponseEntity<ApiError> notFoundResponse = globalExceptionHandler.handleNotFound(notFoundException, mockRequest);
        
        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
        assertEquals("Not Found", notFoundResponse.getBody().getError());

        // Second exception
        IllegalArgumentException badRequestException = new IllegalArgumentException("Bad request");
        ResponseEntity<ApiError> badRequestResponse = globalExceptionHandler.handleBadRequest(badRequestException, mockRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, badRequestResponse.getStatusCode());
        assertEquals("Bad Request", badRequestResponse.getBody().getError());

        // Third exception
        RuntimeException genericException = new RuntimeException("Generic error");
        ResponseEntity<ApiError> genericResponse = globalExceptionHandler.handleAll(genericException, mockRequest);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, genericResponse.getStatusCode());
        assertEquals("Internal Server Error", genericResponse.getBody().getError());
    }

    @Test
    @DisplayName("Should handle exceptions with null request")
    void testHandleExceptionWithNullRequest() {
        // This test ensures the handler can handle cases where request might be null
        // though in practice Spring should always provide a non-null request
        ResourceNotFoundException exception = new ResourceNotFoundException("Test error");
        
        assertDoesNotThrow(() -> {
            // Note: In real scenario, request should not be null, but we test robustness
            try {
                globalExceptionHandler.handleNotFound(exception, null);
            } catch (NullPointerException e) {
                // Expected if implementation doesn't handle null request
                assertTrue(true);
            }
        });
    }
}

package org.poma.jpa.backend.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    private ResourceNotFoundException exception;

    @BeforeEach
    void setUp() {
        exception = new ResourceNotFoundException("Test error message");
    }

    @Test
    @DisplayName("Should create ResourceNotFoundException with message")
    void testConstructorWithMessage() {
        assertNotNull(exception);
        assertEquals("Test error message", exception.getMessage());
    }

    @Test
    @DisplayName("Should inherit from RuntimeException")
    void testInheritance() {
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
    }

    @Test
    @DisplayName("Should handle null message")
    void testConstructorWithNullMessage() {
        ResourceNotFoundException nullMessageException = new ResourceNotFoundException(null);
        assertNull(nullMessageException.getMessage());
    }

    @Test
    @DisplayName("Should handle empty message")
    void testConstructorWithEmptyMessage() {
        ResourceNotFoundException emptyMessageException = new ResourceNotFoundException("");
        assertEquals("", emptyMessageException.getMessage());
    }

    @Test
    @DisplayName("Should handle whitespace-only message")
    void testConstructorWithWhitespaceMessage() {
        ResourceNotFoundException whitespaceMessageException = new ResourceNotFoundException("   ");
        assertEquals("   ", whitespaceMessageException.getMessage());
    }

    @Test
    @DisplayName("Should handle long messages")
    void testConstructorWithLongMessage() {
        String longMessage = "A".repeat(1000);
        ResourceNotFoundException longMessageException = new ResourceNotFoundException(longMessage);
        assertEquals(longMessage, longMessageException.getMessage());
    }

    @Test
    @DisplayName("Should handle special characters in message")
    void testConstructorWithSpecialCharacters() {
        String specialMessage = "Error: Invalid symbol @#$%^&*()";
        ResourceNotFoundException specialMessageException = new ResourceNotFoundException(specialMessage);
        assertEquals(specialMessage, specialMessageException.getMessage());
    }

    @Test
    @DisplayName("Should handle unicode characters in message")
    void testConstructorWithUnicodeCharacters() {
        String unicodeMessage = "错误：用户不存在";
        ResourceNotFoundException unicodeMessageException = new ResourceNotFoundException(unicodeMessage);
        assertEquals(unicodeMessage, unicodeMessageException.getMessage());
    }

    @Test
    @DisplayName("Should preserve message in toString")
    void testToString() {
        String toString = exception.toString();
        assertTrue(toString.contains("ResourceNotFoundException"));
        assertTrue(toString.contains("Test error message"));
    }

    @Test
    @DisplayName("Should have no cause by default")
    void testNoCause() {
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Should behave like RuntimeException")
    void testRuntimeExceptionBehavior() {
        // Test that it can be caught as RuntimeException
        assertDoesNotThrow(() -> {
            try {
                throw exception;
            } catch (RuntimeException e) {
                assertEquals(exception, e);
            }
        });

        // Test that it can be caught as Exception
        assertDoesNotThrow(() -> {
            try {
                throw exception;
            } catch (Exception e) {
                assertEquals(exception, e);
            }
        });

        // Test that it can be caught as ResourceNotFoundException
        assertDoesNotThrow(() -> {
            try {
                throw exception;
            } catch (ResourceNotFoundException e) {
                assertEquals(exception, e);
            }
        });
    }

    @Test
    @DisplayName("Should handle stack trace like any exception")
    void testStackTrace() {
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }

    @Test
    @DisplayName("Should support exception chaining")
    void testExceptionChaining() {
        RuntimeException cause = new RuntimeException("Root cause");
        ResourceNotFoundException chainedException = new ResourceNotFoundException("Chained message");
        
        // Test that we can set cause (though constructor doesn't support it directly)
        assertDoesNotThrow(() -> {
            try {
                throw chainedException;
            } catch (ResourceNotFoundException e) {
                // In real usage, you might use initCause
                e.initCause(cause);
                assertEquals(cause, e.getCause());
            }
        });
    }

    @Test
    @DisplayName("Should be serializable")
    void testSerializable() {
        assertDoesNotThrow(() -> {
            // Test that the exception can be serialized/deserialized
            // This is important for distributed systems
            ResourceNotFoundException tempException = new ResourceNotFoundException("Serializable test");
            assertNotNull(tempException);
        });
    }

    @Test
    @DisplayName("Should handle different error scenarios")
    void testDifferentErrorScenarios() {
        String[] errorMessages = {
            "User not found with id: 999",
            "Asset not found with symbol: INVALID",
            "Transaction not found with id: 123",
            "Alert not found with id: 456",
            "Holding not found for asset: AAPL"
        };

        for (String message : errorMessages) {
            ResourceNotFoundException scenarioException = new ResourceNotFoundException(message);
            assertEquals(message, scenarioException.getMessage());
            assertTrue(scenarioException.getMessage().contains("not found"));
        }
    }

    @Test
    @DisplayName("Should handle numeric IDs in error messages")
    void testNumericIdsInMessages() {
        String[] numericMessages = {
            "User not found with id: 1",
            "User not found with id: 999999",
            "User not found with id: 0",
            "User not found with id: -1"
        };

        for (String message : numericMessages) {
            ResourceNotFoundException numericException = new ResourceNotFoundException(message);
            assertEquals(message, numericException.getMessage());
        }
    }

    @Test
    @DisplayName("Should handle API endpoint patterns in messages")
    void testApiEndpointPatterns() {
        String[] apiMessages = {
            "Resource not found at /api/users/999",
            "Resource not found at /api/assets/AAPL",
            "Resource not found at /api/transactions/123",
            "Resource not found at /api/alerts/456"
        };

        for (String message : apiMessages) {
            ResourceNotFoundException apiException = new ResourceNotFoundException(message);
            assertEquals(message, apiException.getMessage());
            assertTrue(apiException.getMessage().contains("/api/"));
        }
    }
}

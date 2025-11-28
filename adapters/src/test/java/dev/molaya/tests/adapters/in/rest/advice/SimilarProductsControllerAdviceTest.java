package dev.molaya.tests.adapters.in.rest.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.molaya.tests.domain.exceptions.IntegrationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SimilarProductsControllerAdviceTest {
    @InjectMocks
    private SimilarProductsControllerAdvice advice;

    @Test
    void handleException() {
        final var exception = new Exception("Test Exception");
        final var response = advice.handleException(exception);
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        final var apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals("MOLAR-500", apiError.code());
        assertEquals("Unknown error occurred", apiError.message());
        assertEquals("Test Exception", apiError.detail());
    }

    @Test
    void handleIntegrationException() {
        final var exception = new IntegrationException("Integration failure", null);
        final var response = advice.handleIntegrationException(exception);
        assertNotNull(response);
        assertEquals(409, response.getStatusCode().value());
        final var apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals("MOLAR-409", apiError.code());
        assertEquals("Integration with external service failed", apiError.message());
        assertEquals("Integration failure", apiError.detail());
    }
}

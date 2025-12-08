package com.capitole.similarproducts.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.capitole.similarproducts.adapter.in.rest.dto.ErrorResponse;
import com.capitole.similarproducts.domain.exception.ExternalServiceException;
import com.capitole.similarproducts.domain.exception.ProductNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @Test
    void handleException_ShouldReturn404_WhenProductNotFoundException() {
        ProductNotFoundException ex = new ProductNotFoundException("1", new RuntimeException());
        Mockito.when(request.getRequestURI()).thenReturn("/test");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product not found with ID: 1", response.getBody().message());
    }

    @Test
    void handleException_ShouldReturn503_WhenExternalServiceException() {
        ExternalServiceException ex = new ExternalServiceException("service", "error");
        Mockito.when(request.getRequestURI()).thenReturn("/test");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(ex, request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("External service 'service' error: error", response.getBody().message());
    }

    @Test
    void handleException_ShouldReturn400_WhenIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid arg");
        Mockito.when(request.getRequestURI()).thenReturn("/test");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid arg", response.getBody().message());
    }

    @Test
    void handleException_ShouldReturn500_WhenUnexpectedException() {
        RuntimeException ex = new RuntimeException("Unexpected");
        Mockito.when(request.getRequestURI()).thenReturn("/test");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().message());
    }
}

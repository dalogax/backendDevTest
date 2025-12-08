package com.capitole.similarproducts.adapter.in.rest;

import com.capitole.similarproducts.adapter.in.rest.dto.ErrorResponse;
import com.capitole.similarproducts.domain.exception.ExternalServiceException;
import com.capitole.similarproducts.domain.exception.ProductNotFoundException;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler using @ControllerAdvice with pattern matching.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Unified exception handler.
     *
     * @param ex the exception
     * @param request the HTTP servlet request
     * @return ResponseEntity with appropriate error response
     */
    @ExceptionHandler(Exception.class)
    @Observed(name = "exception.handling", contextualName = "handle-exception")
    public ResponseEntity<@NonNull ErrorResponse> handleException(
            Exception ex, HttpServletRequest request) {

        String path = request.getRequestURI();

        return switch (ex) {
            case ProductNotFoundException pnf -> {
                LOGGER.error("Product not found: {}", pnf.getMessage());
                yield buildErrorResponse(HttpStatus.NOT_FOUND, pnf.getMessage(), path);
            }
            case ExternalServiceException ese -> {
                LOGGER.error("External service error: {}", ese.getMessage());
                yield buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ese.getMessage(), path);
            }
            case IllegalArgumentException iae -> {
                LOGGER.warn("Invalid argument: {}", iae.getMessage());
                yield buildErrorResponse(HttpStatus.BAD_REQUEST, iae.getMessage(), path);
            }
            default -> {
                LOGGER.error("Unexpected error occurred", ex);
                yield buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred. Please try again later.",
                        path
                );
            }
        };
    }

    /**
     * Builds standardized error response.
     * Helper method to reduce code duplication.
     *
     * @param status HTTP status code
     * @param message error message
     * @param path request path
     * @return ResponseEntity with ErrorResponse
     */
    private ResponseEntity<@NonNull ErrorResponse> buildErrorResponse(
            HttpStatus status, String message, String path) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );

        return ResponseEntity.status(status).body(errorResponse);
    }
}

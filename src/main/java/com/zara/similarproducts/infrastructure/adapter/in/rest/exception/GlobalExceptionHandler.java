package com.zara.similarproducts.infrastructure.adapter.in.rest.exception;

import com.zara.similarproducts.domain.model.DomainException;
import com.zara.similarproducts.domain.model.ExternalServiceException;
import com.zara.similarproducts.domain.model.InvalidProductIdException;
import com.zara.similarproducts.domain.model.ProductNotFoundException;
import com.zara.similarproducts.infrastructure.adapter.in.rest.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ProductNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleProductNotFound(
            ProductNotFoundException ex, ServerWebExchange exchange) {
        logger.warn("Product not found: {}", ex.getProductId().value());
        
        ErrorResponse error = ErrorResponse.of(
                ex.getErrorCode(),
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }
    
    @ExceptionHandler(InvalidProductIdException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidProductId(
            InvalidProductIdException ex, ServerWebExchange exchange) {
        logger.warn("Invalid product ID: {}", ex.getProductId());
        
        ErrorResponse error = ErrorResponse.of(
                ex.getErrorCode(),
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(error));
    }
    
    @ExceptionHandler(ExternalServiceException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleExternalService(
            ExternalServiceException ex, ServerWebExchange exchange) {
        logger.error("External service error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.of(
                ex.getErrorCode(),
                "Service temporarily unavailable",
                exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error));
    }
    
    @ExceptionHandler(TimeoutException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleTimeout(
            TimeoutException ex, ServerWebExchange exchange) {
        logger.error("Request timeout", ex);
        
        ErrorResponse error = ErrorResponse.of(
                "REQUEST_TIMEOUT",
                "Request timeout",
                exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(error));
    }
    
    @ExceptionHandler(DomainException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDomainException(
            DomainException ex, ServerWebExchange exchange) {
        logger.warn("Domain error: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.of(
                ex.getErrorCode(),
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(error));
    }
    
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(
            Exception ex, ServerWebExchange exchange) {
        logger.error("Unexpected error", ex);
        
        ErrorResponse error = ErrorResponse.of(
                "INTERNAL_SERVER_ERROR",
                "Internal server error",
                exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.internalServerError().body(error));
    }
}
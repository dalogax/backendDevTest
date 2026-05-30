package com.inditex.similarproducts.exception;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ProductNotFoundException ex) {
        log.warn("Product not found: {}", ex.getMessage());
        return new ErrorResponse(ErrorCode.PRODUCT_NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ExternalServiceException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleExternalService(ExternalServiceException ex) {
        log.error("External service error: {}", ex.getMessage());
        return new ErrorResponse(ErrorCode.EXTERNAL_SERVICE_ERROR, ex.getMessage());
    }

    @ExceptionHandler(TimeoutException.class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public ErrorResponse handleTimeout(TimeoutException ex) {
        log.error("Upstream timeout: {}", ex.getMessage());
        return new ErrorResponse(ErrorCode.UPSTREAM_TIMEOUT, "External service timed out");
    }

    @ExceptionHandler(CallNotPermittedException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleCircuitBreakerOpen(CallNotPermittedException ex) {
        log.warn("Circuit breaker open, rejecting request: {}", ex.getMessage());
        return new ErrorResponse(ErrorCode.SERVICE_UNAVAILABLE, "Service is temporarily unavailable");
    }

    @ExceptionHandler(WebClientRequestException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleWebClientRequest(WebClientRequestException ex) {
        log.error("External service unreachable: {}", ex.getMessage());
        return new ErrorResponse(ErrorCode.EXTERNAL_SERVICE_UNAVAILABLE, "External service is unreachable");
    }

    @ExceptionHandler(WebClientResponseException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleWebClientResponse(WebClientResponseException ex) {
        log.error("WebClient response error: {} {}", ex.getStatusCode(), ex.getMessage());
        return new ErrorResponse(ErrorCode.EXTERNAL_SERVICE_ERROR, "External service returned an error");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoResource(NoResourceFoundException ex) {
        return new ErrorResponse(ErrorCode.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return new ErrorResponse(ErrorCode.INTERNAL_ERROR, "An unexpected error occurred");
    }
}

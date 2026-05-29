package com.inditex.similarproducts.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ProductNotFoundException ex) {
        log.warn("Product not found: {}", ex.getMessage());
        return new ErrorResponse("PRODUCT_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(ExternalServiceException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleExternalService(ExternalServiceException ex) {
        log.error("External service error: {}", ex.getMessage());
        return new ErrorResponse("EXTERNAL_SERVICE_ERROR", ex.getMessage());
    }

    @ExceptionHandler(WebClientRequestException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleWebClientRequest(WebClientRequestException ex) {
        log.error("External service unreachable: {}", ex.getMessage());
        return new ErrorResponse("EXTERNAL_SERVICE_UNAVAILABLE", "External service is unreachable");
    }

    @ExceptionHandler(WebClientResponseException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleWebClientResponse(WebClientResponseException ex) {
        log.error("WebClient response error: {} {}", ex.getStatusCode(), ex.getMessage());
        return new ErrorResponse("EXTERNAL_SERVICE_ERROR", "External service returned an error");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoResource(NoResourceFoundException ex) {
        return new ErrorResponse("NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
    }
}

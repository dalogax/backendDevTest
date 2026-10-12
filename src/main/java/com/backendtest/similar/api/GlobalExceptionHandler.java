package com.backendtest.similar.api;

import com.backendtest.similar.exception.ProductNotFoundException;
import com.backendtest.similar.exception.UpstreamException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(ProductNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Product not found"));
    }

    @ExceptionHandler(UpstreamException.class)
    public ResponseEntity<Map<String, String>> badGateway(UpstreamException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("message", "Upstream error"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }
}

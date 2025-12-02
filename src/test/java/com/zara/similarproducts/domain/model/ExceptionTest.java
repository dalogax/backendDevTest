package com.zara.similarproducts.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ExceptionTest {

    @Test
    void productNotFoundExceptionShouldHaveCorrectProperties() {
        ProductId productId = ProductId.of("123");
        ProductNotFoundException exception = new ProductNotFoundException(productId);
        
        assertThat(exception.getProductId()).isEqualTo(productId);
        assertThat(exception.getMessage()).isEqualTo("Product not found: 123");
        assertThat(exception.getErrorCode()).isEqualTo("PRODUCT_NOT_FOUND");
    }

    @Test
    void invalidProductIdExceptionShouldHaveCorrectProperties() {
        String invalidId = "abc";
        InvalidProductIdException exception = new InvalidProductIdException(invalidId);
        
        assertThat(exception.getProductId()).isEqualTo(invalidId);
        assertThat(exception.getMessage()).isEqualTo("Invalid product ID format: abc");
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_PRODUCT_ID");
    }

    @Test
    void externalServiceExceptionShouldHaveCorrectProperties() {
        String message = "Service unavailable";
        ExternalServiceException exception = new ExternalServiceException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("EXTERNAL_SERVICE_ERROR");
    }

    @Test
    void externalServiceExceptionWithCauseShouldHaveCorrectProperties() {
        String message = "Service unavailable";
        RuntimeException cause = new RuntimeException("Connection timeout");
        ExternalServiceException exception = new ExternalServiceException(message, cause);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("EXTERNAL_SERVICE_ERROR");
    }
}
package com.globant.interview.david.msdavidmobilephone.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductIdTest {

    @Test
    void shouldCreateValidProductId() {
        ProductId productId = new ProductId("123");
        assertEquals("123", productId.value());
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ProductId(null)
        );
        assertEquals("ProductId cannot be null or blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIdIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ProductId("   ")
        );
        assertEquals("ProductId cannot be null or blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIdIsEmpty() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ProductId("")
        );
        assertEquals("ProductId cannot be null or blank", exception.getMessage());
    }
}

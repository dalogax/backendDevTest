package com.globant.interview.david.msdavidmobilephone.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateValidProduct() {
        Product product = new Product("1", "iPhone 15", new BigDecimal("999.99"), true);

        assertEquals("1", product.id());
        assertEquals("iPhone 15", product.name());
        assertEquals(new BigDecimal("999.99"), product.price());
        assertTrue(product.availability());
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Product(null, "iPhone", new BigDecimal("999"), true)
        );
    }

    @Test
    void shouldThrowExceptionWhenIdIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Product("  ", "iPhone", new BigDecimal("999"), true)
        );
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Product("1", null, new BigDecimal("999"), true)
        );
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Product("1", "", new BigDecimal("999"), true)
        );
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Product("1", "iPhone", null, true)
        );
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Product("1", "iPhone", new BigDecimal("-10"), true)
        );
    }

    @Test
    void shouldAcceptZeroPrice() {
        Product product = new Product("1", "iPhone", BigDecimal.ZERO, true);
        assertEquals(BigDecimal.ZERO, product.price());
    }
}

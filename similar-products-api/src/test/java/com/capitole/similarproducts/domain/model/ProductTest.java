package com.capitole.similarproducts.domain.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ProductTest {

    @Test
    void isValid_withAllValidFields_returnsTrue() {
        Product product = new Product("1", "Test Product", java.math.BigDecimal.valueOf(10.0),
            true);
        assertTrue(product.isValid());
    }

    @ParameterizedTest
    @MethodSource("parameterProvideForString")
    void isValid_idIsNullOrBlank_returnsFalse(String id) {
        final Product product = new Product(id, "Test Product", java.math.BigDecimal.valueOf(10.0),
            true);
        assertFalse(product.isValid());
    }

    @ParameterizedTest
    @MethodSource("parameterProvideForString")
    void isValid_nameIsNullOrBlank_returnsFalse(String name) {
        final Product product = new Product("1", name, java.math.BigDecimal.valueOf(10.0), true);
        assertFalse(product.isValid());
    }

    @ParameterizedTest
    @MethodSource("parameterProvideForBigDecimal")
    void isValid_priceIsNullOrZero_returnsFalse(BigDecimal price) {
        final Product product = new Product("1", "Test Product Name", price, true);
        assertFalse(product.isValid());
    }

    @Test
    void isValid_priceIsZero_returnsTrue() {
        final Product product = new Product("1", "Test Product Name", java.math.BigDecimal.ZERO,
            true);
        assertTrue(product.isValid());
    }

    private static Stream<Arguments> parameterProvideForString() {
        return Stream.of(
            Arguments.of((String) null),
            Arguments.of(""),
            Arguments.of("     "),
            Arguments.of("\t"),
            Arguments.of("\n")
        );
    }

    private static Stream<Arguments> parameterProvideForBigDecimal() {
        return Stream.of(
            Arguments.of((BigDecimal) null),
            Arguments.of(BigDecimal.valueOf(-1.0))
        );
    }
}

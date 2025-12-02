package com.zara.similarproducts.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateValidProduct() {
        ProductId id = ProductId.of("123");
        Product product = Product.of(id, "Test Product", BigDecimal.valueOf(99.99), true);
        
        assertThat(product.id()).isEqualTo(id);
        assertThat(product.name()).isEqualTo("Test Product");
        assertThat(product.price()).isEqualTo(BigDecimal.valueOf(99.99));
        assertThat(product.availability()).isTrue();
    }

    @Test
    void shouldThrowExceptionForNullId() {
        assertThatThrownBy(() -> Product.of(null, "Test", BigDecimal.TEN, true))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Product ID cannot be null");
    }

    @Test
    void shouldThrowExceptionForNullName() {
        ProductId id = ProductId.of("123");
        assertThatThrownBy(() -> Product.of(id, null, BigDecimal.TEN, true))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Product name cannot be null");
    }

    @Test
    void shouldThrowExceptionForBlankName() {
        ProductId id = ProductId.of("123");
        assertThatThrownBy(() -> Product.of(id, "", BigDecimal.TEN, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product name cannot be blank");
    }

    @Test
    void shouldThrowExceptionForNullPrice() {
        ProductId id = ProductId.of("123");
        assertThatThrownBy(() -> Product.of(id, "Test", null, true))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Product price cannot be null");
    }

    @Test
    void shouldThrowExceptionForNegativePrice() {
        ProductId id = ProductId.of("123");
        assertThatThrownBy(() -> Product.of(id, "Test", BigDecimal.valueOf(-1), true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product price cannot be negative");
    }

    @Test
    void shouldAcceptZeroPrice() {
        ProductId id = ProductId.of("123");
        assertThatCode(() -> Product.of(id, "Test", BigDecimal.ZERO, true))
                .doesNotThrowAnyException();
    }
}
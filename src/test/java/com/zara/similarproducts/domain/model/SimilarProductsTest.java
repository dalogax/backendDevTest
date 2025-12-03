package com.zara.similarproducts.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class SimilarProductsTest {

    @Test
    void shouldCreateSimilarProductsWithList() {
        Product product1 = createProduct("1", "Product 1");
        Product product2 = createProduct("2", "Product 2");
        List<Product> products = List.of(product1, product2);
        
        SimilarProducts similarProducts = SimilarProducts.of(products);
        
        assertThat(similarProducts.products()).hasSize(2);
        assertThat(similarProducts.size()).isEqualTo(2);
        assertThat(similarProducts.isEmpty()).isFalse();
    }

    @Test
    void shouldCreateEmptySimilarProducts() {
        SimilarProducts similarProducts = SimilarProducts.empty();
        
        assertThat(similarProducts.products()).isEmpty();
        assertThat(similarProducts.size()).isZero();
        assertThat(similarProducts.isEmpty()).isTrue();
    }

    @Test
    void shouldThrowExceptionForNullProductsList() {
        assertThatThrownBy(() -> SimilarProducts.of(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Products list cannot be null");
    }

    @Test
    void shouldCreateImmutableCopy() {
        Product product = createProduct("1", "Product 1");
        List<Product> originalList = List.of(product);
        
        SimilarProducts similarProducts = SimilarProducts.of(originalList);
        
        assertThat(similarProducts.products()).containsExactlyElementsOf(originalList);
        assertThat(similarProducts.products().getClass().getName()).contains("Immutable");
    }

    private Product createProduct(String id, String name) {
        return Product.of(ProductId.of(id), name, BigDecimal.valueOf(10.0), true);
    }
}
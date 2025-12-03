package com.zara.similarproducts.infrastructure.adapter.in.rest.mapper;

import com.zara.similarproducts.domain.model.Product;
import com.zara.similarproducts.domain.model.ProductId;
import com.zara.similarproducts.domain.model.SimilarProducts;
import com.zara.similarproducts.infrastructure.adapter.in.rest.dto.ProductDetailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimilarProductsMapperTest {

    private SimilarProductsMapper mapper;
    
    @BeforeEach
    void setUp() {
        mapper = new SimilarProductsMapper();
    }

    @Test
    void shouldMapSimilarProductsToResponse() {
        Product product1 = createProduct("1", "Product 1", BigDecimal.valueOf(99.99), true);
        Product product2 = createProduct("2", "Product 2", BigDecimal.valueOf(149.99), false);
        SimilarProducts similarProducts = SimilarProducts.of(List.of(product1, product2));
        
        List<ProductDetailResponse> response = mapper.toResponse(similarProducts);
        
        assertThat(response).hasSize(2);
        
        ProductDetailResponse response1 = response.get(0);
        assertThat(response1.id()).isEqualTo("1");
        assertThat(response1.name()).isEqualTo("Product 1");
        assertThat(response1.price()).isEqualTo(BigDecimal.valueOf(99.99));
        assertThat(response1.availability()).isTrue();
        
        ProductDetailResponse response2 = response.get(1);
        assertThat(response2.id()).isEqualTo("2");
        assertThat(response2.name()).isEqualTo("Product 2");
        assertThat(response2.price()).isEqualTo(BigDecimal.valueOf(149.99));
        assertThat(response2.availability()).isFalse();
    }

    @Test
    void shouldMapEmptySimilarProducts() {
        SimilarProducts similarProducts = SimilarProducts.empty();
        
        List<ProductDetailResponse> response = mapper.toResponse(similarProducts);
        
        assertThat(response).isEmpty();
    }

    private Product createProduct(String id, String name, BigDecimal price, boolean availability) {
        return Product.of(ProductId.of(id), name, price, availability);
    }
}
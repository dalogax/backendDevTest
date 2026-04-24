package org.challenge.products.application.service;

import org.challenge.products.application.port.out.ProductPort;
import org.challenge.products.domain.exception.ProductNotFoundException;
import org.challenge.products.domain.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductPort productPort;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Should return similar products for valid product id")
    void shouldReturnSimilarProducts() {

        String productId1 = "1";
        String productId2 = "2";
        String productId3 = "3";

        Product product2 = new Product(productId2, "Product 2", 19.99, true);
        Product product3 = new Product(productId3, "Product 3", 29.99, true);

        when(productPort.getSimilarProductIds(productId1)).thenReturn(List.of(productId2, productId3));
        when(productPort.getProduct(productId2)).thenReturn(product2);
        when(productPort.getProduct(productId3)).thenReturn(product3);

        List<Product> result = productService.getSimilarProducts(productId1);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(product2, product3);
    }

    @Test
    @DisplayName("Should return empty list when no similar ids exist")
    void shouldReturnEmptyListWhenNoSimilarIds() {
        String productId = "1";

        when(productPort.getSimilarProductIds(productId)).thenReturn(List.of());

        List<Product> result = productService.getSimilarProducts(productId);

        assertThat(result).isEmpty();
        verify(productPort, never()).getProduct(any());
    }

    @Test
    @DisplayName("Should skip product when getProduct returns null")
    void shouldSkipNullProducts() {
        String productId1 = "1";
        String productId2 = "2";
        String productIdNull = "99";

        Product product2 = new Product(productId2, "Product 2", 19.99, true);

        when(productPort.getSimilarProductIds(productId1)).thenReturn(List.of(productId2, productIdNull));
        when(productPort.getProduct(productId2)).thenReturn(product2);
        when(productPort.getProduct(productIdNull)).thenReturn(null);

        List<Product> result = productService.getSimilarProducts(productId1);

        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(product2);
    }

    @Test
    @DisplayName("Should propagate ProductNotFoundException from getSimilarProductIds")
    void shouldPropagateNotFoundException() {
        String productId = "4";
        String errorMessage = "not found";

        when(productPort.getSimilarProductIds(productId))
                .thenThrow(new ProductNotFoundException(productId, errorMessage));

        assertThatThrownBy(() -> productService.getSimilarProducts(productId))
                .isInstanceOf(ProductNotFoundException.class);
    }
}

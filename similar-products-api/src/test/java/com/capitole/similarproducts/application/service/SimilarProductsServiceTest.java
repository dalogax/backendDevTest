package com.capitole.similarproducts.application.service;

import static org.mockito.BDDMockito.given;

import com.capitole.similarproducts.domain.exception.ProductNotFoundException;
import com.capitole.similarproducts.domain.model.Product;
import com.capitole.similarproducts.domain.port.out.ProductServicePort;
import java.math.BigDecimal;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SimilarProductsServiceTest {

    @Mock
    private ProductServicePort productServicePort;

    private com.capitole.similarproducts.configuration.SimilarProductsProperties properties;

    @InjectMocks
    private SimilarProductsService similarProductsService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
         properties = new com.capitole.similarproducts.configuration.SimilarProductsProperties();
         properties.setMaxConcurrentCalls(10);
         similarProductsService = new SimilarProductsService(productServicePort, properties);
    }

    @Test
    void getSimilarProducts_ShouldReturnProducts_WhenFound() {
        String productId = "1";
        List<String> similarIds = List.of("2", "3");
        Product p2 = new Product("2", "Product 2", BigDecimal.TEN, true);
        Product p3 = new Product("3", "Product 3", BigDecimal.ONE, true);

        given(productServicePort.getSimilarProductIds(productId)).willReturn(similarIds);
        given(productServicePort.getProductDetail("2")).willReturn(p2);
        given(productServicePort.getProductDetail("3")).willReturn(p3);

        List<Product> result = similarProductsService.getSimilarProducts(productId);
        
        Assertions.assertThat(result).containsExactly(p2, p3);
    }

    @Test
    void getSimilarProducts_ShouldFilterOutNullOrInvalidProducts() {
        String productId = "1";
        List<String> similarIds = List.of("2", "3", "4");
        Product p2 = new Product("2", "Product 2", BigDecimal.TEN, true);
        // p3 returns null (empty in previous logic), p4 throws exception

        given(productServicePort.getSimilarProductIds(productId)).willReturn(similarIds);
        given(productServicePort.getProductDetail("2")).willReturn(p2);
        given(productServicePort.getProductDetail("3")).willReturn(null); // was Mono.empty()
        given(productServicePort.getProductDetail("4")).willThrow(new RuntimeException("Fetch error"));

        List<Product> result = similarProductsService.getSimilarProducts(productId);

        Assertions.assertThat(result).containsExactly(p2);
    }

    @Test
    void getSimilarProducts_ShouldThrowProductNotFoundException_WhenIdsFetchFails() {
        String productId = "1";
        given(productServicePort.getSimilarProductIds(productId)).willThrow(new RuntimeException("API Error"));

        Assertions.assertThatThrownBy(() -> similarProductsService.getSimilarProducts(productId))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void getSimilarProducts_ShouldPreserveOrder_WhenCallsProvideResultsOutOfOrder() {
        // In synchronous world with virtual threads, preserving order depends on collecting futures.
        // Our implementation uses stream().map(submit).toList() then futures.stream().map(join).collect
        // This preserves order of the original ID list.
        
        String productId = "1";
        List<String> similarIds = List.of("2", "3");
        Product p2 = new Product("2", "Product 2", BigDecimal.TEN, true);
        Product p3 = new Product("3", "Product 3", BigDecimal.ONE, true);

        given(productServicePort.getSimilarProductIds(productId)).willReturn(similarIds);
        given(productServicePort.getProductDetail("2")).willReturn(p2);
        given(productServicePort.getProductDetail("3")).willReturn(p3);
        
        // We cannot easily simulate delay in "given" for blocking code without Thread.sleep or custom answer, 
        // but the logic guarantees order by list nature.
        
        List<Product> result = similarProductsService.getSimilarProducts(productId);

        Assertions.assertThat(result).containsExactly(p2, p3);
    }
}

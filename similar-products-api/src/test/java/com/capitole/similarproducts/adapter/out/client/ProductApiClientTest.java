package com.capitole.similarproducts.adapter.out.client;

import com.capitole.similarproducts.generated.api.ProductApi;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.capitole.similarproducts.adapter.out.client.mapper.ProductClientMapper;
import com.capitole.similarproducts.domain.model.Product;
import com.capitole.similarproducts.generated.model.ProductDetail;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ProductApiClientTest {

    @Mock
    private ProductApi productApi;
    @Mock
    private ProductClientMapper productClientMapper;

    private ProductApiClient productApiClient;

    @BeforeEach
    void setUp() {
        productApiClient = new ProductApiClient(productApi, productClientMapper);
    }

    @Test
    void getSimilarProductIds_ShouldReturnListOfIds_WhenApiCallIsSuccessful() {
        // Given
        String productId = "1";
        List<String> expectedIds = List.of("2", "3", "4");
        when(productApi.getProductSimilarids(productId)).thenReturn(Mono.just(java.util.Set.of("2", "3", "4")));

        // When
        Mono<List<String>> result = productApiClient.getSimilarProductIds(productId);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(list -> list.size() == expectedIds.size() && list.containsAll(expectedIds))
                .verifyComplete();
        verify(productApi).getProductSimilarids(productId);
    }

    @Test
    void getProductDetail_ShouldReturnProduct_WhenApiCallIsSuccessful() {
        // Given
        String productId = "1";
        ProductDetail detail = new ProductDetail();
        detail.setId("1");
        detail.setName("Product 1");
        detail.setPrice(new BigDecimal("10.00"));
        detail.setAvailability(true);
        
        Product product = new Product("1", "Product 1", new BigDecimal("10.00"), true);
        
        when(productApi.getProductProductId(productId)).thenReturn(Mono.just(detail));
        when(productClientMapper.toDomain(detail)).thenReturn(product);

        // When
        Mono<Product> result = productApiClient.getProductDetail(productId);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(resultingProduct -> 
                    resultingProduct.id().equals("1") && 
                    resultingProduct.name().equals("Product 1") && 
                    resultingProduct.price().equals(new BigDecimal("10.00")) && 
                    resultingProduct.availability())
                .verifyComplete();
        verify(productApi).getProductProductId(productId);
        verify(productClientMapper).toDomain(detail);
    }
}

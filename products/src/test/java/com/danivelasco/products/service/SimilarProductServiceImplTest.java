package com.danivelasco.products.service;

import com.danivelasco.products.client.ProductApiClient;
import com.danivelasco.products.dto.ProductDetailsResponse;
import com.danivelasco.products.dto.ProductResponse;
import com.danivelasco.products.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SimilarProductServiceImplTest {

    private ProductApiClient productApiClient;
    private Cache cache;
    private SimilarProductServiceImpl similarProductService;

    @BeforeEach
    void setUp() {
        productApiClient = mock(ProductApiClient.class);
        CacheManager cacheManager = mock(CacheManager.class);
        cache = mock(Cache.class);

        when(cacheManager.getCache("similar_products_full")).thenReturn(cache);

        similarProductService = new SimilarProductServiceImpl(productApiClient, cacheManager);
    }

    @Test
    void getSimilarProducts_returnsCachedValue_whenExists() {
        ProductDetailsResponse cachedResponse =
                new ProductDetailsResponse(List.of(), List.of());

        when(cache.get("1", ProductDetailsResponse.class)).thenReturn(cachedResponse);

        StepVerifier.create(similarProductService.getSimilarProducts("1"))
                .expectNext(cachedResponse)
                .verifyComplete();

        verify(productApiClient, never()).getSimilarProductIds(anyString());
        verify(productApiClient, never()).getProductDetail(anyString());
    }

    @Test
    void getSimilarProducts_returnsProductsAndFailures_andCachesResult() {
        ProductResponse product2 = new ProductResponse("2", "Shirt", 35.99, true);

        when(cache.get("1", ProductDetailsResponse.class)).thenReturn(null);
        when(productApiClient.getSimilarProductIds("1")).thenReturn(Mono.just(List.of("2", "3")));

        when(productApiClient.getProductDetail("2")).thenReturn(Mono.just(product2));
        when(productApiClient.getProductDetail("3")).thenReturn(Mono.error(notFoundException()));

        StepVerifier.create(similarProductService.getSimilarProducts("1"))
                .assertNext(response -> {
                    assertEquals(1, response.products().size());
                    assertEquals("2", response.products().getFirst().id());

                    assertEquals(1, response.failures().size());
                    assertEquals("3", response.failures().getFirst().productId());
                    assertEquals(404, response.failures().getFirst().status());
                })
                .verifyComplete();

        ArgumentCaptor<ProductDetailsResponse> captor =
                ArgumentCaptor.forClass(ProductDetailsResponse.class);

        verify(cache).put(eq("1"), captor.capture());

        ProductDetailsResponse cachedValue = captor.getValue();
        assertEquals(1, cachedValue.products().size());
        assertEquals(1, cachedValue.failures().size());
    }

    @Test
    void getSimilarProducts_throwsGlobalException_whenSimilarIdsEndpointReturns404() {
        when(cache.get("1", ProductDetailsResponse.class)).thenReturn(null);
        when(productApiClient.getSimilarProductIds("1"))
                .thenReturn(Mono.error(notFoundException()));

        StepVerifier.create(similarProductService.getSimilarProducts("1"))
                .expectErrorSatisfies(error -> {
                    assertEquals(GlobalException.class, error.getClass());
                    GlobalException ex = (GlobalException) error;
                    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
                    assertEquals("No similar products found", ex.getMessage());
                })
                .verify();

        verify(productApiClient, never()).getProductDetail(anyString());
        verify(cache, never()).put(anyString(), any());
    }

    @Test
    void getSimilarProducts_addsFailure500_whenProductDetailFailsWithGenericError() {
        when(cache.get("1", ProductDetailsResponse.class)).thenReturn(null);
        when(productApiClient.getSimilarProductIds("1")).thenReturn(Mono.just(List.of("2")));
        when(productApiClient.getProductDetail("2"))
                .thenReturn(Mono.error(new RuntimeException("boom")));

        StepVerifier.create(similarProductService.getSimilarProducts("1"))
                .assertNext(response -> {
                    assertEquals(0, response.products().size());
                    assertEquals(1, response.failures().size());
                    assertEquals("2", response.failures().getFirst().productId());
                    assertEquals(500, response.failures().getFirst().status());
                })
                .verifyComplete();

        verify(cache).put(eq("1"), any(ProductDetailsResponse.class));
    }

    private WebClientResponseException.NotFound notFoundException() {
        return (WebClientResponseException.NotFound) WebClientResponseException.create(
                404,
                "Not Found",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        );
    }
}
package com.danivelasco.products.service;

import com.danivelasco.products.client.ProductApiClient;
import com.danivelasco.products.dto.ProductDetailFailure;
import com.danivelasco.products.dto.ProductDetailResult;
import com.danivelasco.products.dto.ProductDetailsResponse;
import com.danivelasco.products.dto.ProductResponse;
import com.danivelasco.products.exception.GlobalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
public class SimilarProductServiceImpl implements SimilarProductService {

    private static final Logger log = LoggerFactory.getLogger(SimilarProductServiceImpl.class);
    private final ProductApiClient productApiClient;
    private final CacheManager cacheManager;

    public SimilarProductServiceImpl(ProductApiClient productApiClient, CacheManager cacheManager) {
        this.productApiClient = productApiClient;
        this.cacheManager = cacheManager;
    }

    @Override
    public Mono<ProductDetailsResponse> getSimilarProducts(String productId) {
        Cache cache = cacheManager.getCache("similar_products_full");

        if (cache != null) {
            ProductDetailsResponse cachedValue = cache.get(productId, ProductDetailsResponse.class);

            if (cachedValue != null) {
                return Mono.just(cachedValue);
            }

        }

        return productApiClient.getSimilarProductIds(productId)
                .onErrorMap(WebClientResponseException.NotFound.class,
                        ex -> {
                            log.info("Not Found {}", productId);
                            return new GlobalException("No similar products found", HttpStatus.NOT_FOUND);
                        })
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::getProductDetailResult, 8)
                .collectList()
                .map(results -> {
                    List<ProductResponse> products = results.stream()
                            .filter(ProductDetailResult::isSuccess)
                            .map(ProductDetailResult::product)
                            .toList();

                    List<ProductDetailFailure> failures = results.stream()
                            .filter(result -> !result.isSuccess())
                            .map(ProductDetailResult::failure)
                            .toList();

                    return new ProductDetailsResponse(products, failures);
                })
                .doOnNext(result -> {
                    if (cache != null) {
                        cache.put(productId, result);
                    }
                });
    }

    private Mono<ProductDetailResult> getProductDetailResult(String productId) {
        return productApiClient.getProductDetail(productId)
                .timeout(Duration.ofSeconds(2))
                .map(ProductDetailResult::success)
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.just(ProductDetailResult.failure(
                                new ProductDetailFailure(productId, ex.getStatusCode().value())
                        ))
                )
                .onErrorResume(ex ->
                        Mono.just(ProductDetailResult.failure(
                                new ProductDetailFailure(productId, 500)
                        ))
                );
    }
}

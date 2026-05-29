package com.inditex.similarproducts.service;

import com.inditex.similarproducts.client.ProductApiClient;
import com.inditex.similarproducts.model.ProductDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SimilarProductsService {

    private static final Logger log = LoggerFactory.getLogger(SimilarProductsService.class);

    private final ProductApiClient productApiClient;

    public SimilarProductsService(ProductApiClient productApiClient) {
        this.productApiClient = productApiClient;
    }

    public Mono<List<ProductDetail>> getSimilarProducts(String productId) {
        return productApiClient.getSimilarIds(productId)
                .flatMapMany(Flux::fromIterable)
                // flatMapSequential: concurrent fetches but preserves similarity order
                .flatMapSequential(id -> productApiClient.getProductDetail(id)
                        .onErrorResume(ex -> {
                            log.warn("Skipping product {} due to error: {}", id, ex.getMessage());
                            return Mono.empty();
                        }))
                .collectList();
    }
}

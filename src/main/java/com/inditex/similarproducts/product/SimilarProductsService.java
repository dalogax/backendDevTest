package com.inditex.similarproducts.product;

import java.util.List;

import com.inditex.similarproducts.catalog.CatalogClient;
import com.inditex.similarproducts.catalog.CatalogProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SimilarProductsService {
    private static final Logger log = LoggerFactory.getLogger(SimilarProductsService.class);
    private final CatalogClient catalog;
    private final int maxConcurrency;

    public SimilarProductsService(CatalogClient catalog, CatalogProperties properties) {
        this.catalog = catalog;
        this.maxConcurrency = properties.maxConcurrency();
    }

    public Mono<List<ProductDetail>> findSimilar(String productId) {
        return catalog.similarIds(productId)
                .flatMapMany(ids -> Flux.fromArray(ids).distinct())
                // Fetch concurrently while emitting in the original similarity order.
                .flatMapSequential(id -> catalog.product(id)
                        .onErrorResume(error -> {
                            log.warn("Omitting similar product {}: {}", id, error.toString());
                            return Mono.empty();
                        }), maxConcurrency, 1)
                .distinct(ProductDetail::id)
                .collectList();
    }
}

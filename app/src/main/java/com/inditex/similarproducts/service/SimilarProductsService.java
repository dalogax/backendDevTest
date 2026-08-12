package com.inditex.similarproducts.service;

import com.inditex.similarproducts.client.ProductClient;
import com.inditex.similarproducts.model.ProductDetail;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class SimilarProductsService {

    private final ProductClient productClient;

    public SimilarProductsService(ProductClient productClient) {
        this.productClient = productClient;
    }

    /**
     * Fetches the similar product IDs, then resolves each product detail concurrently.
     *
     * <p>{@code flatMapSequential} fires all the detail requests in parallel (bounded by the
     * connection pool) while preserving the similarity order in the emitted result.
     *
     * <p>No cache is used here on purpose. Load testing showed the throughput ceiling is imposed
     * by the k6 client pacing (sleep between iterations) and the per-product timeout, not by the
     * number of upstream calls; a cache could not raise the measured throughput and would only add
     * memory footprint and staleness. See README.md ("Caching: deliberately omitted") for the data.
     */
    public Flux<ProductDetail> getSimilarProducts(String productId) {
        return productClient.getSimilarIds(productId)
                .flatMapMany(Flux::fromIterable)
                .flatMapSequential(productClient::getProductDetail);
    }
}

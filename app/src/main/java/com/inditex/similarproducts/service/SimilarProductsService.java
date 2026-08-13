package com.inditex.similarproducts.service;

import com.inditex.similarproducts.client.ProductClient;
import com.inditex.similarproducts.model.ProductDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class SimilarProductsService {

    private static final Logger log = LoggerFactory.getLogger(SimilarProductsService.class);

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
                // Two DEBUG lines per request (here and in the controller) are enough to follow a
                // flow end to end: how many IDs came back, and how many survived the detail fetch.
                .doOnNext(similarIds -> log.debug("Product {} has similar IDs {}", productId, similarIds))
                .flatMapMany(Flux::fromIterable)
                .flatMapSequential(productClient::getProductDetail);
    }
}

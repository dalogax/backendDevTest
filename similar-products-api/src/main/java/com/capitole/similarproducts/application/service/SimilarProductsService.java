package com.capitole.similarproducts.application.service;

import com.capitole.similarproducts.domain.exception.ProductNotFoundException;
import com.capitole.similarproducts.domain.model.Product;
import com.capitole.similarproducts.domain.port.in.GetSimilarProductsUseCase;
import com.capitole.similarproducts.domain.port.out.ProductServicePort;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Application service implementing the GetSimilarProductsUseCase. Orchestrates the business logic
 * for retrieving similar products.
 */
@Service
public class SimilarProductsService implements GetSimilarProductsUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimilarProductsService.class);

    private final ProductServicePort productServicePort;
    private final com.capitole.similarproducts.configuration.SimilarProductsProperties properties;

    public SimilarProductsService(ProductServicePort productServicePort, 
            com.capitole.similarproducts.configuration.SimilarProductsProperties properties) {
        this.productServicePort = productServicePort;
        this.properties = properties;
    }

    /**
     * Retrieves similar products with parallel API calls and caching.
     *
     * @param productId the ID of the product to find similar products for
     * @return Mono containing list of similar products
     */
    @Override
    @Cacheable(value = "similarProducts", key = "#productId", sync = true)
    @Observed(
        name = "similar.products.fetch",
        contextualName = "fetch-similar-products"
    )
    public Mono<@NonNull List<Product>> getSimilarProducts(String productId) {
        LOGGER.info("Fetching similar products for productId: {}", productId);

        return productServicePort.getSimilarProductIds(productId)
            .doOnNext(
                ids -> LOGGER.debug("Found {} similar product IDs for product {}", ids.size(),
                    productId))
            .onErrorResume(error -> {
                LOGGER.error("Error fetching similar product IDs for productId: {}", productId, error);
                return Mono.error(new ProductNotFoundException(productId, error));
            })
            .flatMapMany(Flux::fromIterable)
            .flatMapSequential(this::getProductDetailSafely, properties.getMaxConcurrentCalls())
            .filter(Product::isValid)
            .collectList()
            .doOnSuccess(
                products -> LOGGER.info("Successfully retrieved {} similar products for productId: {}",
                    products.size(), productId))
            .doOnError(error -> LOGGER.error("Error processing similar products for productId: {}",
                productId, error));
    }

    /**
     * Safely retrieves product details with error handling. Returns empty on error to allow
     * filtering and continue processing other products.
     *
     * @param productId the product ID to fetch
     * @return Mono containing product details or empty on error
     */
    @Observed(name = "product.detail.fetch", contextualName = "fetch-product-detail")
    private Mono<@NonNull Product> getProductDetailSafely(String productId) {
        return productServicePort.getProductDetail(productId)
            .doOnNext(
                product -> LOGGER.debug("Retrieved product details for productId: {}", productId))
            .onErrorResume(error -> {
                LOGGER.warn("Failed to retrieve product details for productId: {}. Skipping.",
                    productId,
                    error);
                return Mono.empty();
            });
    }
}

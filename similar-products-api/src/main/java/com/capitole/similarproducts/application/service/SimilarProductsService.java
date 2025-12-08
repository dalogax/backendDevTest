package com.capitole.similarproducts.application.service;

import com.capitole.similarproducts.domain.exception.ProductNotFoundException;
import com.capitole.similarproducts.domain.model.Product;
import com.capitole.similarproducts.domain.port.in.GetSimilarProductsUseCase;
import com.capitole.similarproducts.domain.port.out.ProductServicePort;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
     * @return List of similar products
     */
    @Override
    @Cacheable(value = "similarProducts", key = "#productId", sync = true)
    @Observed(
        name = "similar.products.fetch",
        contextualName = "fetch-similar-products"
    )
    public @NonNull List<Product> getSimilarProducts(String productId) {
        LOGGER.info("Fetching similar products for productId: {}", productId);

        List<String> similarProductIds;
        try {
            similarProductIds = productServicePort.getSimilarProductIds(productId);
            LOGGER.debug("Found {} similar product IDs for product {}", similarProductIds.size(), productId);
        } catch (Exception error) {
            LOGGER.error("Error fetching similar product IDs for productId: {}", productId, error);
            throw new ProductNotFoundException(productId, error);
        }

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Product>> futures = similarProductIds.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> getProductDetailSafely(id), executor))
                .toList();

            List<Product> products = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .filter(Product::isValid)
                .collect(Collectors.toList());

            LOGGER.info("Successfully retrieved {} similar products for productId: {}",
                products.size(), productId);
            return products;

        } catch (Exception error) {
            LOGGER.error("Error processing similar products for productId: {}", productId, error);
            throw error instanceof RuntimeException ? (RuntimeException) error : new RuntimeException(error);
        }
    }

    /**
     * Safely retrieves product details with error handling. Returns null on error to allow
     * filtering and continue processing other products.
     *
     * @param productId the product ID to fetch
     * @return Product details or null on error
     */
    @Observed(name = "product.detail.fetch", contextualName = "fetch-product-detail")
    private Product getProductDetailSafely(String productId) {
        try {
            Product product = productServicePort.getProductDetail(productId);
            LOGGER.debug("Retrieved product details for productId: {}", productId);
            return product;
        } catch (Exception error) {
            LOGGER.warn("Failed to retrieve product details for productId: {}. Skipping.",
                productId,
                error);
            return null;
        }
    }
}

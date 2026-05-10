package com.sngular.similarproducts.application;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.sngular.similarproducts.application.outbound.ProductsPort;
import com.sngular.similarproducts.domain.ProductDetail;
import com.sngular.similarproducts.domain.exception.SimilarProductsNotFoundException;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;

/**
 * Application service to manage similar products with their details.
 */
@Service
@Validated
@Slf4j
public class DetailProductsUseCase {

    private final ProductsPort productsPort;

    public DetailProductsUseCase(ProductsPort productsPort) {
        this.productsPort = productsPort;
    }

    /**
     * Get details of similar products for a given product ID.
     * Each product detail is fetched concurrently using virtual threads.
     *
     * @param productId the ID of the product for which to find similar products
     * @return ProductDetail objects representing the similar products
     */
    public List<ProductDetail> getSimilarProducts(@NotBlank @Size(min = 1) String productId) {
        log.debug("Fetching similar IDs for productId {}", productId);

        var similarIds = productsPort.getSimilarProductIds(productId);

        List<ProductDetail> similarProducts;
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<ProductDetail>> futures = similarIds.stream()
                    .map(id -> CompletableFuture.supplyAsync(() -> {
                        try {
                            return productsPort.getProduct(id);
                        } catch (Exception ex) {
                            log.warn("Failed to fetch product detail for id {}, skipping", id, ex);
                            return null;
                        }
                    }, executor))
                    .toList();

            similarProducts = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
        }

        if (similarProducts.isEmpty()) {
            log.info("No similar products found for productId {}", productId);
            throw new SimilarProductsNotFoundException(productId);
        }

        log.info("Found {} similar products for productId {}", similarProducts.size(), productId);
        return similarProducts;
    }

}

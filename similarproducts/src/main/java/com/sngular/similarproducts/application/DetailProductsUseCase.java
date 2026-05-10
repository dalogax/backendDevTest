package com.sngular.similarproducts.application;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * 
     * @param productId the ID of the product for which to find similar products
     * @return ProductDetail objects representing the similar products
     */
    public Set<ProductDetail> getSimilarProducts(@NotBlank @Size(min = 1) String productId) {
        log.debug("Fetching similar IDs for productId {}", productId);

        Set<ProductDetail> similarProducts = productsPort.getSimilarProductIds(productId).stream()
                .flatMap(this::getProductDetail)
                .collect(Collectors.toSet());

        if (similarProducts.isEmpty()) {
            log.info("No similar products found for productId {}", productId);
            throw new SimilarProductsNotFoundException(productId);
        }

        log.info("Found {} similar products for productId {}", similarProducts.size(), productId);
        return similarProducts;
    }

    /**
     * Fetch product details for a given product ID, returning an empty stream if
     * the product is not found or an error occurs.
     */
    private Stream<ProductDetail> getProductDetail(String productId) {
        try {
            return Stream.of(productsPort.getProduct(productId));
        } catch (Exception ex) {
            log.warn("Failed to fetch product detail for id {}, skipping", productId, ex);
            return Stream.empty();
        }
    }

}

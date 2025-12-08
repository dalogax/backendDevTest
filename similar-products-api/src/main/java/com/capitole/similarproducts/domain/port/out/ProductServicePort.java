package com.capitole.similarproducts.domain.port.out;

import com.capitole.similarproducts.domain.model.Product;
import java.util.List;
import org.jspecify.annotations.NonNull;
import reactor.core.publisher.Mono;

/**
 * Output port defining the contract for external product service interactions
 */
public interface ProductServicePort {

    /**
     * Retrieves the list of similar product IDs for a given product
     *
     * @param productId the ID of the product
     * @return Mono containing list of similar product IDs, or empty if not found
     */
    Mono<@NonNull List<String>> getSimilarProductIds(String productId);

    /**
     * Retrieves detailed information for a specific product
     *
     * @param productId the ID of the product
     * @return Mono containing product details, or empty if not found
     */
    Mono<@NonNull Product> getProductDetail(String productId);
}
